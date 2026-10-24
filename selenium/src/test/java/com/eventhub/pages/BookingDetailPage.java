package com.eventhub.pages;

import com.eventhub.config.Config;
import com.eventhub.utils.Visibility;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static com.eventhub.utils.Locators.buttonText;
import static com.eventhub.utils.Locators.headingText;
import static com.eventhub.utils.Locators.testId;

/** /bookings/:id — a single booking. */
public class BookingDetailPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By title          = By.tagName("h1");
    private final By main           = By.tagName("main");
    private final By checkRefundBtn = testId("check-refund-btn");
    private final By cancelBtn      = buttonText("Cancel Booking");
    private final By dialog         = By.cssSelector("[role='dialog']");
    private final By confirmYes     = testId("confirm-dialog-yes");

    public BookingDetailPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Config.WAIT);
    }

    public BookingDetailPage open(long bookingId) {
        driver.get(Config.BASE_URL + "/bookings/" + bookingId);
        return waitForLoaded();
    }

    public BookingDetailPage waitForLoaded() {
        wait.until(ExpectedConditions.urlMatches("/bookings/\\d+$"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(checkRefundBtn));
        return this;
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    public String title() {
        return driver.findElement(title).getText();
    }

    /** Text of the breadcrumb nav that ends with this booking ref ("My Bookings / <ref>"). */
    public String breadcrumbText(String bookingRef) {
        return driver.findElement(By.xpath("//nav[.//span[normalize-space()='" + bookingRef + "']]")).getText();
    }

    public boolean isSectionVisible(String sectionTitle) {
        return Visibility.appears(wait, headingText(sectionTitle));
    }

    public String mainText() {
        return driver.findElement(main).getText();
    }

    public boolean isRefundCheckVisible() {
        return Visibility.isDisplayed(driver, checkRefundBtn);
    }

    /** Clicks "Cancel Booking" and returns the confirmation dialog's text. */
    public String openCancelDialog() {
        wait.until(ExpectedConditions.elementToBeClickable(cancelBtn)).click();
        return wait.until(ExpectedConditions.visibilityOfElementLocated(dialog)).getText();
    }

    /**
     * Confirms the open dialog. Returns right after the click so the caller can
     * check the toast before it auto-dismisses; the app then redirects to the
     * bookings list (use BookingsPage.waitForLoaded()).
     */
    public void confirmCancel() {
        WebElement d = driver.findElement(dialog);
        d.findElement(confirmYes).click();
    }
}
