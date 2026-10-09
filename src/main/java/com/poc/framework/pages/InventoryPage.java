package com.poc.framework.pages;

import org.openqa.selenium.By;

public class InventoryPage extends BasePage {
    private static final By TITLE = By.cssSelector(".title");
    private static final By ITEMS = By.cssSelector(".inventory_item");

    public String title() {
        return textOf(TITLE);
    }

    public int itemCount() {
        visible(ITEMS);
        return driver.findElements(ITEMS).size();
    }
}
