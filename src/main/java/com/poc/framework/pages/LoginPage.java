package com.poc.framework.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class LoginPage extends BasePage {
    private static final By USERNAME = By.id("user-name");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN_BTN = By.id("login-button");
    private static final By ERROR = By.cssSelector("[data-test='error']");

    @Step("Log in as {user}")
    public InventoryPage loginAs(String user, String pass) {
        type(USERNAME, user);
        type(PASSWORD, pass);
        click(LOGIN_BTN);
        return new InventoryPage();
    }

    @Step("Attempt login as {user} (expecting failure)")
    public LoginPage loginExpectingError(String user, String pass) {
        type(USERNAME, user);
        type(PASSWORD, pass);
        click(LOGIN_BTN);
        return this;
    }

    public String errorMessage() {
        return textOf(ERROR);
    }
}
