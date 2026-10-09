package com.poc.framework.listeners;

import com.poc.framework.config.ConfigManager;
import com.poc.framework.driver.DriverManager;
import io.qameta.allure.Allure;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;

/** Screenshot on failure + sets pass/fail status on the LambdaTest dashboard. */
public class TestListener implements ITestListener {

    @Override
    public void onTestSuccess(ITestResult result) {
        markLambdaTest("passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        if (DriverManager.isStarted()) {
            byte[] png = ((TakesScreenshot) DriverManager.get()).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Failure screenshot", "image/png", new ByteArrayInputStream(png), "png");
        }
        markLambdaTest("failed");
    }

    private void markLambdaTest(String status) {
        if (ConfigManager.isLambdaTest() && DriverManager.isStarted()) {
            ((JavascriptExecutor) DriverManager.get()).executeScript("lambda-status=" + status);
        }
    }
}
