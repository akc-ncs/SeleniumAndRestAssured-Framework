package com.poc.tests.ui;

import com.poc.framework.base.BaseUiTest;
import com.poc.framework.pages.InventoryPage;
import com.poc.framework.pages.LoginPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Epic("Web UI")
@Feature("Login")
public class LoginUiTest extends BaseUiTest {

    @Test(groups = {"smoke", "ui"}, description = "Valid user can log in")
    @Severity(SeverityLevel.CRITICAL)
    public void validUserCanLogin() {
        LoginPage login = new LoginPage();
        login.open("/");
        InventoryPage inventory = login.loginAs("standard_user", "secret_sauce");

        assertEquals(inventory.title(), "Products");
        assertTrue(inventory.itemCount() > 0, "Expected at least one product");
    }

    // Not parallel: the three browsers already run in parallel, so this keeps the
    // number of concurrent LambdaTest sessions at exactly 3.
    @DataProvider(name = "badCredentials")
    public Object[][] badCredentials() {
        return new Object[][]{
                {"locked_out_user", "secret_sauce", "locked out"},
                {"standard_user", "wrong_password", "do not match"}
        };
    }

    @Test(groups = {"regression", "ui"}, dataProvider = "badCredentials")
    @Severity(SeverityLevel.NORMAL)
    public void invalidLoginShowsError(String user, String pass, String expectedFragment) {
        LoginPage login = new LoginPage();
        login.open("/");
        String error = login.loginExpectingError(user, pass).errorMessage();

        assertTrue(error.contains(expectedFragment), "Unexpected error: " + error);
    }
}
