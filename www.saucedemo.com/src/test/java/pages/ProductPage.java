package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProductPage {
    private WebDriver driver;

    // Constructor
    public ProductPage(WebDriver driver) {
        this.driver = driver;
    }

    // Locatori
    public By firstAddToCartButton = By.xpath("//button[contains(text(),'Add to cart')]");
    private By shoppingCartLink = By.className("shopping_cart_link");
    private By pageTitle = By.className("title");

    // Metode simple

    public String getPageTitle() {
        return driver.findElement(pageTitle).getText();
    }

    public void addFirstProductToCart() {
        driver.findElement(firstAddToCartButton).click();
    }

    public void goToCart() {
        driver.findElement(shoppingCartLink).click();
    }
}

