package com.poc.framework.driver;

import com.poc.framework.config.ConfigManager;

/**
 * One browser/OS combination. Suites only declare what differs; anything left blank
 * falls back to config.properties / -D flags / env vars.
 */
public record BrowserTarget(String browser, String platform, String version) {

    public static BrowserTarget of(String browser, String platform, String version) {
        return new BrowserTarget(
                firstNonBlank(browser, ConfigManager.get("browser", "chrome")).toLowerCase(),
                firstNonBlank(platform, ConfigManager.get("lt.platform", "Windows 11")),
                firstNonBlank(version, ConfigManager.get("lt.browser.version", "latest")));
    }

    private static String firstNonBlank(String value, String fallback) {
        return value != null && !value.isBlank() ? value : fallback;
    }
}
