package com.poc.framework.driver;

import org.openqa.selenium.WebDriver;

import java.time.Duration;

/** Thread-safe driver holder: each parallel thread (one per browser) gets its own driver. */
public final class DriverManager {
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {}

    public static void start(String testName, BrowserTarget target) {
        WebDriver driver = DriverFactory.create(testName, target);
        driver.manage().timeouts().implicitlyWait(Duration.ZERO); // explicit waits only
        driver.manage().window().maximize();
        DRIVER.set(driver);
    }

    public static WebDriver get() {
        WebDriver d = DRIVER.get();
        if (d == null) throw new IllegalStateException("Driver not started for this thread");
        return d;
    }

    public static boolean isStarted() {
        return DRIVER.get() != null;
    }

    public static void quit() {
        WebDriver d = DRIVER.get();
        if (d != null) {
            try {
                d.quit();
            } finally {
                DRIVER.remove();
            }
        }
    }
}
