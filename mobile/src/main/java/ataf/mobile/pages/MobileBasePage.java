package ataf.mobile.pages;

import ataf.core.logging.ScenarioLogManager;
import ataf.mobile.utils.MobileDriverUtil;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.support.ui.FluentWait;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.function.Function;

/**
 * This is the base class for page objects of native Android apps. It provides element lookup by
 * accessibility id and resource id, explicit waits and the basic interactions most screens need.
 * <p>
 * All waits are explicit and poll every {@link #POLLING_INTERVAL}; no implicit waits or fixed
 * sleeps are used. Prefer {@link #accessibilityId(String)} (Android content description) and
 * {@link #resourceId(String)} over XPath, which is slow and brittle on mobile.
 * </p>
 *
 * <pre>{@code
 * public class LoginPage extends MobileBasePage {
 *     private static final By USER_NAME = resourceId("com.example.app:id/user_name");
 *     private static final By LOGIN = accessibilityId("Log in");
 *
 *     public void logIn(String userName) {
 *         enterTextInElement(USER_NAME, userName);
 *         tapOnElement(LOGIN);
 *     }
 * }
 * }</pre>
 */
public class MobileBasePage {
    /**
     * Interval in which explicit waits check their condition.
     */
    public static final Duration POLLING_INTERVAL = Duration.ofMillis(250);

    /**
     * Constant AndroidDriver object of the session this page belongs to.
     */
    protected final AndroidDriver DRIVER;

    /**
     * Default timeout of the explicit waits of this page.
     */
    protected final Duration EXPLICIT_WAIT_TIMEOUT;

    /**
     * Constructs a page object for the session of the current scenario, using the configured
     * explicit wait timeout ({@code testautomation.mobile.explicitWaitSeconds}).
     *
     * @throws ataf.mobile.utils.MobileDriverNotInitializedException if the current thread has no
     *             session, for example because the scenario is missing the {@code @mobile} tag.
     */
    public MobileBasePage() {
        this(MobileDriverUtil.getDriver(), MobileDriverUtil.getConfig().explicitWaitTimeout());
    }

    /**
     * Constructs a page object for the given driver.
     *
     * @param driver the AndroidDriver used to interact with the app.
     * @param explicitWaitTimeout the default timeout of explicit waits.
     */
    public MobileBasePage(@NotNull AndroidDriver driver, @NotNull Duration explicitWaitTimeout) {
        this.DRIVER = driver;
        this.EXPLICIT_WAIT_TIMEOUT = explicitWaitTimeout;
    }

    /**
     * Creates a locator for the accessibility id of an element, which is the content description
     * ({@code content-desc}) on Android.
     *
     * @param accessibilityId the accessibility id of the element.
     * @return the locator.
     */
    public static By accessibilityId(@NotNull String accessibilityId) {
        return AppiumBy.accessibilityId(accessibilityId);
    }

    /**
     * Creates a locator for the resource id of an element, for example
     * {@code com.example.app:id/login}.
     *
     * @param resourceId the resource id of the element, preferably including the package.
     * @return the locator.
     */
    public static By resourceId(@NotNull String resourceId) {
        return AppiumBy.id(resourceId);
    }

    /**
     * Waits for an element to be displayed, using the default explicit wait timeout.
     *
     * @param locator the locator of the element.
     * @return the displayed element.
     * @throws TimeoutException if the element is not displayed in time.
     */
    public WebElement waitForElementVisible(@NotNull By locator) {
        return waitForElementVisible(locator, EXPLICIT_WAIT_TIMEOUT);
    }

    /**
     * Waits for an element to be displayed.
     *
     * @param locator the locator of the element.
     * @param timeout the maximum time to wait.
     * @return the displayed element.
     * @throws TimeoutException if the element is not displayed in time.
     */
    public WebElement waitForElementVisible(@NotNull By locator, @NotNull Duration timeout) {
        return waitFor(locator, timeout, "visible", driver -> {
            WebElement element = driver.findElement(locator);
            return element.isDisplayed() ? element : null;
        });
    }

    /**
     * Waits for an element to be displayed and enabled, using the default explicit wait timeout.
     *
     * @param locator the locator of the element.
     * @return the clickable element.
     * @throws TimeoutException if the element is not clickable in time.
     */
    public WebElement waitForElementClickable(@NotNull By locator) {
        return waitForElementClickable(locator, EXPLICIT_WAIT_TIMEOUT);
    }

    /**
     * Waits for an element to be displayed and enabled.
     *
     * @param locator the locator of the element.
     * @param timeout the maximum time to wait.
     * @return the clickable element.
     * @throws TimeoutException if the element is not clickable in time.
     */
    public WebElement waitForElementClickable(@NotNull By locator, @NotNull Duration timeout) {
        return waitFor(locator, timeout, "clickable", driver -> {
            WebElement element = driver.findElement(locator);
            return element.isDisplayed() && element.isEnabled() ? element : null;
        });
    }

    /**
     * Waits for an element to be clickable and taps on it.
     *
     * @param locator the locator of the element.
     * @throws TimeoutException if the element is not clickable in time.
     */
    public void tapOnElement(@NotNull By locator) {
        ScenarioLogManager.getLogger().debug("Tap on element {}", locator);
        waitForElementClickable(locator).click();
    }

    /**
     * Waits for an element to be displayed, clears it and enters the given text. The text is not
     * logged, as it may contain sensitive test data.
     *
     * @param locator the locator of the text field.
     * @param text the text to enter.
     * @throws TimeoutException if the element is not displayed in time.
     */
    public void enterTextInElement(@NotNull By locator, @NotNull CharSequence text) {
        ScenarioLogManager.getLogger().debug("Enter text in element {}", locator);
        WebElement element = waitForElementVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    /**
     * Waits for an element to be displayed and clears its text.
     *
     * @param locator the locator of the text field.
     * @throws TimeoutException if the element is not displayed in time.
     */
    public void clearElementText(@NotNull By locator) {
        ScenarioLogManager.getLogger().debug("Clear text of element {}", locator);
        waitForElementVisible(locator).clear();
    }

    /**
     * Waits for an element to be displayed and returns its text.
     *
     * @param locator the locator of the element.
     * @return the text of the element.
     * @throws TimeoutException if the element is not displayed in time.
     */
    public String getElementText(@NotNull By locator) {
        return waitForElementVisible(locator).getText();
    }

    /**
     * Checks without waiting whether a matching element is currently displayed.
     *
     * @param locator the locator of the element.
     * @return {@code true} if at least one matching element is displayed.
     */
    public boolean isElementVisible(@NotNull By locator) {
        try {
            return DRIVER.findElements(locator).stream().anyMatch(WebElement::isDisplayed);
        } catch (StaleElementReferenceException e) {
            return false;
        }
    }

    /**
     * Checks without waiting whether a matching element exists in the current UI hierarchy, even if
     * it is not displayed.
     *
     * @param locator the locator of the element.
     * @return {@code true} if at least one matching element exists.
     */
    public boolean isElementPresent(@NotNull By locator) {
        return !DRIVER.findElements(locator).isEmpty();
    }

    /**
     * Scrolls the first scrollable container of the screen, for example a list, until an element
     * with the given accessibility id is displayed. Android only includes the visible rows of a
     * list in the UI hierarchy, so entries further down can't be found without scrolling. Uses
     * UiAutomator's {@code UiScrollable}, which requires the UiAutomator2 driver.
     *
     * @param accessibilityId the accessibility id (content description) of the element.
     * @return the displayed element.
     * @throws TimeoutException if no such element is displayed within the default explicit wait
     *             timeout.
     */
    public WebElement scrollToAccessibilityId(@NotNull String accessibilityId) {
        ScenarioLogManager.getLogger().debug("Scroll to element with accessibility id [{}]", accessibilityId);
        return waitForElementVisible(scrollIntoViewLocator(accessibilityId));
    }

    /**
     * Creates the UiAutomator locator that scrolls the first scrollable container until an element
     * with the given content description is visible.
     */
    static By scrollIntoViewLocator(String accessibilityId) {
        String description = accessibilityId.replace("\\", "\\\\").replace("\"", "\\\"");
        return AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().description(\"" + description + "\"))");
    }

    /**
     * Captures a screenshot of the device screen.
     *
     * @return the screenshot as PNG bytes.
     */
    public byte[] takeScreenshot() {
        return DRIVER.getScreenshotAs(OutputType.BYTES);
    }

    private WebElement waitFor(By locator, Duration timeout, String state, Function<AndroidDriver, WebElement> condition) {
        return new FluentWait<>(DRIVER)
                .withTimeout(timeout)
                .pollingEvery(POLLING_INTERVAL)
                .ignoring(NoSuchElementException.class, StaleElementReferenceException.class)
                .withMessage("Waited " + timeout.toMillis() + " ms in vain for element " + locator + " to be " + state + "!")
                .until(condition);
    }
}
