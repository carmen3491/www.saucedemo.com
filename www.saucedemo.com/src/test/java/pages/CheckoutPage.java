package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutPage {
    private static WebDriver driver;

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
    }

    //  Locatori1
    private By firstNameField = By.id("first-name");
    private By lastNameField = By.id("last-name");
    private By postalCodeField = By.id("postal-code");
    private By continueButton = By.id("continue");

    // Locatori2
    private static By totalPriceLabel = By.className("summary_total_label");
    private By finishButton = By.id("finish");

    // Checkout Complet
    private By confirmationMessage = By.className("complete-header");

    // === Pasul1===
    public void fillCustomerInfo(String firstName, String lastName, String postalCode) {
        driver.findElement(firstNameField).sendKeys(firstName);
        driver.findElement(lastNameField).sendKeys(lastName);
        driver.findElement(postalCodeField).sendKeys(postalCode);
    }

    public void clickContinue() {
        driver.findElement(continueButton).click();
    }

    // === Pasul2 ===
    public static String getTotalPriceText() {
        return driver.findElement(totalPriceLabel).getText();
    }

    public void clickFinish() {
        driver.findElement(finishButton).click();
    }

    // === Complet ===
    public String getConfirmationMessage() {
        return driver.findElement(confirmationMessage).getText();
    }

    // === Full Flow ===
    public void completeCheckout(String firstName, String lastName, String postalCode) {
        fillCustomerInfo(firstName, lastName, postalCode);
        clickContinue();
        clickFinish();
    }
}
