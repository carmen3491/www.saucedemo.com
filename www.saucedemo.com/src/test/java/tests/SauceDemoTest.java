package tests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.CartPage;
import pages.CheckoutPage;
import pages.LoginPage;
import pages.ProductPage;
import utils.DriverFactory;
import utils.WaitUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SauceDemoTest {

    WebDriver driver;
    Properties props;

    // Page objects (instanțiate în @BeforeEach)
    LoginPage loginPage;
    ProductPage productPage;
    CartPage cartPage;
    CheckoutPage checkoutPage;

    @BeforeAll
    void loadConfig() throws IOException {
        props = new Properties();
        try (FileInputStream fis = new FileInputStream("src/test/resources/config.properties")) {
            props.load(fis);
        }
    }

    @BeforeEach
    void init() {
        String browser = System.getProperty("browser", props.getProperty("browser", "chrome"));
        driver = DriverFactory.createDriver(browser);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.get("https://www.saucedemo.com/");

        // Inițializare pagini (folosind același driver)
        loginPage = new LoginPage(driver);
        productPage = new ProductPage(driver);
        cartPage = new CartPage(driver);
        checkoutPage = new CheckoutPage(driver);
    }

    @Test
    @DisplayName("End-to-end: login -> adaugă produs -> checkout -> verificare total și confirmare")
    void testPurchaseFlow() {
        // 1. Login
        loginPage.login("standard_user", "secret_sauce");

        // 2. Adaugă primul produs în coș și navighează la cart
        productPage.addFirstProductToCart();
        productPage.goToCart();

        // 3. Checkout (cart)
        cartPage.clickCheckout();

        // 4. Complete checkout info și continuă
        checkoutPage.fillCustomerInfo("Carmen", "Chirita", "12069");
        checkoutPage.clickContinue();

        // 5. Verifică dacă prețul total este cel așteptat (metodă de instanță)
        String actualTotal = checkoutPage.getTotalPriceText();
        assertEquals("Total: $32.39", actualTotal, "Prețul total afișat în checkout nu este cel așteptat.");

        // 6. Finalizează (Finish) - presupun că există butonul finish
        checkoutPage.clickFinish();

        // 7. Așteaptă și verifică mesajul de confirmare (folosind WebDriverWait direct)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // Locatori folosiți des pe saucedemo; ajustează dacă ai alt locator în pagină.
        By confirmationHeader = By.className("complete-header"); // exemplu: "THANK YOU FOR YOUR ORDER"
        By confirmationText = By.className("complete-text"); // exemplu text detaliat sub header

        // Așteptăm ca header-ul să fie vizibil
        wait.until(ExpectedConditions.visibilityOfElementLocated(confirmationHeader));
        String headerText = driver.findElement(confirmationHeader).getText().trim();
        String bodyText = driver.findElement(confirmationText).getText().trim();

        // Verificări mai puțin fragile: comparăm conținut așteptat (poți modifica exact textul)
        // Dacă dorești o egalitate strictă, înlocuiește assertTrue(...) cu assertEquals(...)
        assertTrue(headerText.equalsIgnoreCase("Thank you for your order!") ||
                        headerText.equalsIgnoreCase("THANK YOU FOR YOUR ORDER"),
                "Header-ul de confirmare nu conține textul așteptat. Text găsit: " + headerText);
        
    }

    @Test
    void  testLoginValidUserAndInvalidPassword() {
        loginPage.login("standard_user", "wrong_password");

        String expectedError = "Epic sadface: Username and password do not match any user in this service";
        String actualError = loginPage.getErrorMessage();

        assertTrue(actualError.contains(expectedError));
    }

    @Test
    void testCheckoutWithAllProducts() {
        // Login
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        // Add all products to cart
        List<WebElement> addToCartButtons = driver.findElements(By.xpath("//button[contains(text(),'Add to cart')]"));
        int totalProducts = addToCartButtons.size();
        for (WebElement button : addToCartButtons) {
            button.click();
        }

        // Click on cart
        driver.findElement(By.className("shopping_cart_link")).click();

        // Verify all products added to cart
        List<WebElement> cartItems = driver.findElements(By.className("cart_item"));
        assertEquals(totalProducts, cartItems.size(), "All products should be added to cart");

        // Click Checkout
        driver.findElement(By.id("checkout")).click();

        // Fill out checkout info
        driver.findElement(By.id("first-name")).sendKeys("Test");
        driver.findElement(By.id("last-name")).sendKeys("User");
        driver.findElement(By.id("postal-code")).sendKeys("12345");
        driver.findElement(By.id("continue")).click();

        // Verify on overview page
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("checkout-step-two.html"), "Should be on overview page");

        // Verify all products are listed
        List<WebElement> overviewItems = driver.findElements(By.className("cart_item"));
        assertEquals(totalProducts, overviewItems.size(), "All products should be listed in the overview");

        // Finish checkout
        driver.findElement(By.id("finish")).click();

        // Confirm success message
        WebElement confirmationMessage = driver.findElement(By.className("complete-header"));
        assertEquals("Thank you for your order!", confirmationMessage.getText(), "Checkout should be successful");
    }

    @AfterEach
    void close() {
        if (driver != null) {
            driver.quit();
        }
    }
}