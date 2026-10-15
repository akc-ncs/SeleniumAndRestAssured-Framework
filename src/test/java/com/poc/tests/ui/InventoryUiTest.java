package com.poc.tests.ui;

import com.poc.framework.base.AuthenticatedUiTest;
import com.poc.framework.pages.InventoryPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Epic("Web UI")
@Feature("Inventory")
public class InventoryUiTest extends AuthenticatedUiTest {

    @Test(groups = {"smoke", "ui"}, description = "Logged-in user sees the product list (no login step)")
    public void productListIsShown() {
        InventoryPage inventory = new InventoryPage();   // session already restored: we are on the inventory page

        assertEquals(inventory.title(), "Products");
        assertTrue(inventory.itemCount() > 0, "Expected at least one product");
    }
}
