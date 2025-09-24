package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CartPage {
    private WebDriver driver;

    // Constructor
    public CartPage(WebDriver driver) {
        this.driver = driver;
    }

    // ====================
    // Locatori
    // ====================

    private By cartItems = By.className("cart_item");
    private By itemName = By.className("inventory_item_name");
    private By itemPrice = By.className("inventory_item_price");
    private By removeButton = By.xpath("//button[contains(text(),'Remove')]");
    private By continueShoppingButton = By.id("continue-shopping");
    private By checkoutButton = By.id("checkout");

    // ====================
    // Acțiuni
    // ====================

    // Returnează numărul de produse din coș
    public int getCartItemsCount() {
        return driver.findElements(cartItems).size();
    }

    // Returnează numele produsului de la un anumit index
    public String getCartItemNameByIndex(int index) {
        List<WebElement> items = driver.findElements(cartItems);
        return items.get(index).findElement(itemName).getText();
    }

    // Returnează prețul produsului de la un anumit index
    public String getCartItemPriceByIndex(int index) {
        List<WebElement> items = driver.findElements(cartItems);
        return items.get(index).findElement(itemPrice).getText();
    }

    // Șterge primul produs din coș
    public void removeFirstItem() {
        List<WebElement> removeButtons = driver.findElements(removeButton);
        if (!removeButtons.isEmpty()) {
            removeButtons.get(0).click();
        }
    }

    // Apasă pe butonul "Continue Shopping"
    public void clickContinueShopping() {
        driver.findElement(continueShoppingButton).click();
    }

    // Apasă pe butonul "Checkout"
    public void clickCheckout() {
        driver.findElement(checkoutButton).click();
    }
}