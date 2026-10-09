package com.poc.framework.pages;

import com.poc.framework.config.ConfigManager;
import com.poc.framework.driver.DriverManager;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BasePage {
    protected final WebDriver driver = DriverManager.get();
    protected final WebDriverWait wait =
            new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.getInt("timeout.seconds", 15)));

    @Step("Open {path}")
    public void open(String path) {
        driver.get(ConfigManager.require("base.url") + path);
    }

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void type(By locator, String text) {
        WebElement el = visible(locator);
        el.clear();
        el.sendKeys(text);
    }

    protected String textOf(By locator) {
        return visible(locator).getText();
    }
}
