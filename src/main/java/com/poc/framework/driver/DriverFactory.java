package com.poc.framework.driver;

import com.poc.framework.config.ConfigManager;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/** Builds a local or LambdaTest (remote) WebDriver for the requested browser target. */
public final class DriverFactory {
    private DriverFactory() {}

    public static WebDriver create(String testName, BrowserTarget target) {
        return ConfigManager.isLambdaTest() ? remote(testName, target) : local(target.browser());
    }

    // ---------- Local ----------
    private static WebDriver local(String browser) {
        boolean headless = ConfigManager.getBoolean("headless", false);
        return switch (browser) {
            case "firefox" -> {
                FirefoxOptions o = new FirefoxOptions();
                if (headless) o.addArguments("-headless");
                yield new FirefoxDriver(o);
            }
            case "edge" -> {
                EdgeOptions o = new EdgeOptions();
                if (headless) o.addArguments("--headless=new");
                yield new EdgeDriver(o);
            }
            default -> {
                ChromeOptions o = new ChromeOptions();

                // Use a specific Chrome install (e.g. /usr/bin/google-chrome) instead of
                // whatever Selenium Manager finds, such as the snap Chromium stub on Ubuntu.
                String chromeBinary = ConfigManager.get("chrome.binary");
                if (chromeBinary != null && !chromeBinary.isBlank()) {
                    o.setBinary(chromeBinary);
                }

                o.addArguments("--window-size=1920,1080", "--no-sandbox", "--disable-dev-shm-usage");
                if (headless) o.addArguments("--headless=new");
                yield new ChromeDriver(o);
            }
        };
    }

    // ---------- LambdaTest ----------
    private static WebDriver remote(String testName, BrowserTarget target) {
        Map<String, Object> lt = new HashMap<>();
        lt.put("username", ConfigManager.require("lt.username"));
        lt.put("accessKey", ConfigManager.require("lt.access.key"));
        lt.put("platformName", target.platform());
        lt.put("build", ConfigManager.get("lt.build", "local-build"));
        lt.put("project", ConfigManager.get("lt.project", "Selenium PoC"));
        lt.put("name", testName + " [" + target.browser() + "]");
        lt.put("w3c", true);
        lt.put("plugin", "java-testNG");
        lt.put("video", true);
        lt.put("console", true);
        lt.put("network", true);

        MutableCapabilities caps = switch (target.browser()) {
            case "firefox" -> new FirefoxOptions();
            case "edge" -> new EdgeOptions();
            case "safari" -> new SafariOptions();
            default -> new ChromeOptions();
        };
        caps.setCapability("browserVersion", target.version());
        caps.setCapability("LT:Options", lt);

        try {
            return new RemoteWebDriver(URI.create(ConfigManager.get("lt.hub")).toURL(), caps);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Invalid LambdaTest hub URL", e);
        }
    }
}
