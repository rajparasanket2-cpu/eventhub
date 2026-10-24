package com.eventhub.pages;

import com.eventhub.config.Config;
import com.eventhub.utils.Locators;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static com.eventhub.utils.Locators.headingText;
import static com.eventhub.utils.Locators.testId;

/** /bookings — the "My Bookings" list. */
public class BookingsPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By navBookings = testId("nav-bookings");
    private final By heading     = headingText("My Bookings");
    private final By cards       = testId("booking-card");
    private final By emptyState  = headingText("No bookings yet");
    private final By viewDetails = By.linkText("View Details");
    private final By cancelBtn   = testId("cancel-booking-btn");
    private final By bookingId   = testId("booking-id");

    public BookingsPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Config.WAIT);
    }

    public BookingsPage open() {
        driver.get(Config.BASE_URL + "/bookings");
        return waitForLoaded();
    }

    /** Opens the list the way a user does: from the navbar on the home page. */
    public BookingsPage openFromNavbar() {
        driver.get(Config.BASE_URL);
        wait.until(ExpectedConditions.elementToBeClickable(navBookings)).click();
        wait.until(ExpectedConditions.urlToBe(Config.BASE_URL + "/bookings"));
        return waitForLoaded();
    }

    /** The list has finished loading once it shows cards or the empty state (not the skeletons). */
    public BookingsPage waitForLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(heading));
        wait.until(d -> !d.findElements(cards).isEmpty() || !d.findElements(emptyState).isEmpty());
        return this;
    }

    public boolean isHeadingVisible() {
        return driver.findElement(heading).isDisplayed();
    }

    public WebElement cardFor(String bookingRef) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(Locators.bookingCardFor(bookingRef)));
    }

    public String cardText(String bookingRef) {
        return cardFor(bookingRef).getText();
    }

    public String cardBookingId(String bookingRef) {
        return cardFor(bookingRef).findElement(bookingId).getText();
    }

    public boolean cardHasViewDetails(String bookingRef) {
        return cardFor(bookingRef).findElement(viewDetails).isDisplayed();
    }

    public boolean cardHasCancelButton(String bookingRef) {
        return cardFor(bookingRef).findElement(cancelBtn).isDisplayed();
    }

    public BookingDetailPage viewDetails(String bookingRef) {
        cardFor(bookingRef).findElement(viewDetails).click();
        return new BookingDetailPage(driver).waitForLoaded();
    }

    /** Waits until no card for this ref is shown; returns false if it's still there after the wait. */
    public boolean waitForCardGone(String bookingRef) {
        try {
            wait.until(ExpectedConditions.numberOfElementsToBe(Locators.bookingCardFor(bookingRef), 0));
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }
}
