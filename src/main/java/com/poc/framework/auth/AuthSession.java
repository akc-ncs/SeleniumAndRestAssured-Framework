package com.poc.framework.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.framework.config.ConfigManager;
import com.poc.framework.driver.DriverManager;
import com.poc.framework.pages.LoginPage;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Logs in through the UI once, saves the session cookies, and restores them into every
 * later browser instead of logging in again.
 *
 * - Cached in memory for the whole test run (shared by parallel threads).
 * - Optionally saved to disk (session.persist=true) to reuse across runs - local use only.
 * - Cached per browser, so Chrome, Edge and Safari each keep their own session.
 * - If the saved session no longer works (expired), it logs in again automatically.
 */
public final class AuthSession {

    public record CookieData(String name, String value, String path, Long expiry,
                             boolean secure, boolean httpOnly, String sameSite) {}

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final Map<String, List<CookieData>> CACHE = new ConcurrentHashMap<>();
    private static final Object LOCK = new Object();

    private AuthSession() {}

    /** Call after the driver has started. Leaves the browser on the logged-in home page. */
    public static void ensureLoggedIn() {
        WebDriver driver = DriverManager.get();
        String key = browserKey(driver);

        List<CookieData> saved = load(key);
        if (saved != null && restore(driver, saved)) return;

        synchronized (LOCK) {                       // only one thread logs in at a time
            saved = load(key);
            if (saved != null && restore(driver, saved)) return;   // another thread just did it

            uiLogin();
            save(key, driver.manage().getCookies().stream().map(AuthSession::toData).toList());
        }
    }

    // ---------- login / restore ----------

    private static void uiLogin() {
        LoginPage login = new LoginPage();
        login.open("/");
        login.loginAs(ConfigManager.get("app.username", "standard_user"),
                      ConfigManager.get("app.password", "secret_sauce"))
             .title();                              // waits until the logged-in page is showing
    }

    private static boolean restore(WebDriver driver, List<CookieData> data) {
        try {
            String base = ConfigManager.require("base.url");
            driver.get(base + "/");                 // cookies can only be set for the site you are on
            driver.manage().deleteAllCookies();
            for (CookieData c : data) driver.manage().addCookie(toCookie(c));
            driver.get(base + ConfigManager.get("session.home.path", "/inventory.html"));
            return driver.getCurrentUrl().contains(ConfigManager.get("session.home.marker", "inventory"));
        } catch (WebDriverException e) {
            return false;                           // bad or expired cookie: fall back to a real login
        }
    }

    // ---------- cache + disk ----------

    private static String browserKey(WebDriver driver) {
        return ((HasCapabilities) driver).getCapabilities().getBrowserName().toLowerCase().replaceAll("\\W", "");
    }

    private static List<CookieData> load(String key) {
        List<CookieData> cached = CACHE.get(key);
        if (cached == null && persist()) {
            Path file = file(key);
            if (Files.exists(file)) {
                try {
                    cached = JSON.readValue(file.toFile(), new TypeReference<List<CookieData>>() {});
                    CACHE.put(key, cached);
                } catch (IOException e) {
                    System.err.println("Could not read saved session " + file + ": " + e.getMessage());
                }
            }
        }
        return cached;
    }

    private static void save(String key, List<CookieData> data) {
        CACHE.put(key, data);
        if (!persist()) return;
        Path file = file(key);
        try {
            Files.createDirectories(file.getParent());
            JSON.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), data);
            file.toFile().setReadable(false, false);   // the file is a credential: owner only
            file.toFile().setReadable(true, true);
            file.toFile().setWritable(false, false);
            file.toFile().setWritable(true, true);
        } catch (IOException e) {
            System.err.println("Could not save session " + file + ": " + e.getMessage());
        }
    }

    private static boolean persist() {
        return ConfigManager.getBoolean("session.persist", false);
    }

    private static Path file(String key) {
        return Path.of(ConfigManager.get("session.file", ".auth/session") + "-" + key + ".json");
    }

    // ---------- Selenium Cookie <-> data ----------

    private static CookieData toData(Cookie c) {
        return new CookieData(c.getName(), c.getValue(), c.getPath(),
                c.getExpiry() == null ? null : c.getExpiry().getTime(),
                c.isSecure(), c.isHttpOnly(), c.getSameSite());
    }

    private static Cookie toCookie(CookieData d) {
        Cookie.Builder b = new Cookie.Builder(d.name(), d.value())
                .path(d.path() == null ? "/" : d.path())
                .isSecure(d.secure())
                .isHttpOnly(d.httpOnly());
        if (d.expiry() != null) b.expiresOn(new Date(d.expiry()));
        if (d.sameSite() != null) b.sameSite(d.sameSite());
        return b.build();                           // no domain: it applies to the site we are on
    }
}
