package com.eventhub.base;

import com.eventhub.api.ApiClient;
import com.eventhub.config.Config;
import com.eventhub.utils.Screenshots;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.util.ArrayList;
import java.util.List;

import static com.eventhub.utils.Locators.testId;

/** Per-test setup: API login, a fresh logged-in browser, and cleanup of recorded bookings. */
public abstract class BaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected ApiClient api;
    protected String token;
    protected final List<Long> createdBookingIds = new ArrayList<>();

    @BeforeMethod
    public void setUp() {
        api    = new ApiClient();
        token  = api.login(Config.USER_EMAIL, Config.USER_PASSWORD);
        driver = DriverFactory.create();
        wait   = new WebDriverWait(driver, Config.WAIT);
        loginInBrowser(token);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        try {
            if (result.getStatus() == ITestResult.FAILURE && driver != null) {
                Screenshots.save(driver, result.getMethod().getMethodName());
            }
        } finally {
            if (driver != null) driver.quit();
            if (api != null) api.deleteBookings(token, createdBookingIds);
            createdBookingIds.clear();
        }
    }

    /** The app reads the JWT from localStorage; it can only be set once the origin is loaded. */
    protected void loginInBrowser(String jwt) {
        driver.get(Config.BASE_URL);
        ((JavascriptExecutor) driver).executeScript("localStorage.setItem('eventhub_token', arguments[0]);", jwt);
        driver.navigate().refresh();
        wait.until(ExpectedConditions.visibilityOfElementLocated(testId("nav-bookings")));
    }
}
