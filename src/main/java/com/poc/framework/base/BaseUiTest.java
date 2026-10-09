package com.poc.framework.base;

import com.poc.framework.driver.BrowserTarget;
import com.poc.framework.driver.DriverManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.lang.reflect.Method;

public abstract class BaseUiTest {

    /**
     * Browser, platform and version come from the <parameter> tags of the TestNG
     * <test> block. Missing values fall back to config.properties / -D / env vars,
     * so single-browser suites and local runs keep working unchanged.
     */
    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser", "platform", "browserVersion"})
    public void setUp(Method method,
                      @Optional String browser,
                      @Optional String platform,
                      @Optional String browserVersion) {
        BrowserTarget target = BrowserTarget.of(browser, platform, browserVersion);
        DriverManager.start(getClass().getSimpleName() + "." + method.getName(), target);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverManager.quit();
    }
}
