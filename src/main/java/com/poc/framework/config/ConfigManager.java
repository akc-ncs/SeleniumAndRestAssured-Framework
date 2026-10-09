package com.poc.framework.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/** Resolution order: -Dkey  >  ENV_VAR (key upper-cased, dots -> underscores)  >  config.properties */
public final class ConfigManager {
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = ConfigManager.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) PROPS.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not load config.properties", e);
        }
    }

    private ConfigManager() {}

    public static String get(String key) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) return sys;
        String env = System.getenv(key.toUpperCase().replace('.', '_'));
        if (env != null && !env.isBlank()) return env;
        return PROPS.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        String v = get(key);
        return v == null ? defaultValue : v;
    }

    public static String require(String key) {
        String v = get(key);
        if (v == null || v.isBlank()) throw new IllegalStateException("Missing required config: " + key);
        return v;
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String v = get(key);
        return v == null ? defaultValue : Boolean.parseBoolean(v);
    }

    public static int getInt(String key, int defaultValue) {
        String v = get(key);
        return v == null ? defaultValue : Integer.parseInt(v.trim());
    }

    public static boolean isLambdaTest() {
        return "lambdatest_crossbrowser".equalsIgnoreCase(get("execution", "local"));
    }
}
