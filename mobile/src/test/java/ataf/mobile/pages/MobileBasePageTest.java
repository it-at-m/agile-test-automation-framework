package ataf.mobile.pages;

import ataf.mobile.config.MobileConfig;
import ataf.mobile.utils.MobileDriverNotInitializedException;
import ataf.mobile.utils.MobileDriverUtil;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.mockito.InOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link MobileBasePage} with a mocked {@link AndroidDriver}.
 */
public class MobileBasePageTest {
    private static final By LOCATOR = MobileBasePage.accessibilityId("Log in");

    private AndroidDriver driver;
    private WebElement element;
    private MobileBasePage page;

    @BeforeMethod
    public void setUp() {
        driver = mock(AndroidDriver.class);
        element = mock(WebElement.class);
        page = new MobileBasePage(driver, Duration.ofMillis(600));
    }

    @Test
    public void shouldCreateAppiumLocators() {
        Assert.assertEquals(MobileBasePage.accessibilityId("Log in").toString(), AppiumBy.accessibilityId("Log in").toString());
        Assert.assertEquals(MobileBasePage.resourceId("com.example.app:id/login").toString(), AppiumBy.id("com.example.app:id/login").toString());
    }

    @Test
    public void shouldReturnDisplayedElement() {
        when(driver.findElement(LOCATOR)).thenReturn(element);
        when(element.isDisplayed()).thenReturn(true);

        Assert.assertSame(page.waitForElementVisible(LOCATOR), element);
    }

    @Test
    public void shouldRetryUntilElementAppears() {
        when(driver.findElement(LOCATOR)).thenThrow(new NoSuchElementException("not yet")).thenReturn(element);
        when(element.isDisplayed()).thenReturn(true);

        Assert.assertSame(page.waitForElementVisible(LOCATOR), element);
        verify(driver, times(2)).findElement(LOCATOR);
    }

    @Test
    public void shouldTimeOutWhenElementStaysInvisible() {
        when(driver.findElement(LOCATOR)).thenReturn(element);
        when(element.isDisplayed()).thenReturn(false);

        TimeoutException exception = Assert.expectThrows(TimeoutException.class, () -> page.waitForElementVisible(LOCATOR));

        Assert.assertTrue(exception.getMessage().contains(LOCATOR.toString()), exception.getMessage());
        Assert.assertTrue(exception.getMessage().contains("to be visible"), exception.getMessage());
    }

    @Test
    public void shouldRequireEnabledElementForClickableWait() {
        when(driver.findElement(LOCATOR)).thenReturn(element);
        when(element.isDisplayed()).thenReturn(true);
        when(element.isEnabled()).thenReturn(false);

        Assert.assertThrows(TimeoutException.class, () -> page.waitForElementClickable(LOCATOR));
    }

    @Test
    public void shouldTapOnClickableElement() {
        when(driver.findElement(LOCATOR)).thenReturn(element);
        when(element.isDisplayed()).thenReturn(true);
        when(element.isEnabled()).thenReturn(true);

        page.tapOnElement(LOCATOR);

        verify(element).click();
    }

    @Test
    public void shouldNotTapOnDisabledElement() {
        when(driver.findElement(LOCATOR)).thenReturn(element);
        when(element.isDisplayed()).thenReturn(true);
        when(element.isEnabled()).thenReturn(false);

        Assert.assertThrows(TimeoutException.class, () -> page.tapOnElement(LOCATOR));
        verify(element, never()).click();
    }

    @Test
    public void shouldClearTextFieldBeforeTyping() {
        when(driver.findElement(LOCATOR)).thenReturn(element);
        when(element.isDisplayed()).thenReturn(true);

        page.enterTextInElement(LOCATOR, "Hello ATAF");

        InOrder order = inOrder(element);
        order.verify(element).clear();
        order.verify(element).sendKeys("Hello ATAF");
    }

    @Test
    public void shouldClearText() {
        when(driver.findElement(LOCATOR)).thenReturn(element);
        when(element.isDisplayed()).thenReturn(true);

        page.clearElementText(LOCATOR);

        verify(element).clear();
    }

    @Test
    public void shouldReturnElementText() {
        when(driver.findElement(LOCATOR)).thenReturn(element);
        when(element.isDisplayed()).thenReturn(true);
        when(element.getText()).thenReturn("Welcome");

        Assert.assertEquals(page.getElementText(LOCATOR), "Welcome");
    }

    @Test
    public void shouldCheckVisibilityWithoutWaiting() {
        WebElement hidden = mock(WebElement.class);
        when(hidden.isDisplayed()).thenReturn(false);
        when(element.isDisplayed()).thenReturn(true);

        when(driver.findElements(LOCATOR)).thenReturn(List.of(hidden, element));
        Assert.assertTrue(page.isElementVisible(LOCATOR));

        when(driver.findElements(LOCATOR)).thenReturn(List.of(hidden));
        Assert.assertFalse(page.isElementVisible(LOCATOR));

        when(driver.findElements(LOCATOR)).thenReturn(List.of());
        Assert.assertFalse(page.isElementVisible(LOCATOR));
    }

    @Test
    public void shouldTreatStaleElementAsInvisible() {
        when(element.isDisplayed()).thenThrow(new StaleElementReferenceException("gone"));
        when(driver.findElements(LOCATOR)).thenReturn(List.of(element));

        Assert.assertFalse(page.isElementVisible(LOCATOR));
    }

    @Test
    public void shouldCheckPresenceInHierarchy() {
        when(driver.findElements(LOCATOR)).thenReturn(List.of(element));
        Assert.assertTrue(page.isElementPresent(LOCATOR));

        when(driver.findElements(LOCATOR)).thenReturn(List.of());
        Assert.assertFalse(page.isElementPresent(LOCATOR));
    }

    @Test
    public void shouldScrollToElementByAccessibilityId() {
        when(driver.findElement(argThat(locator -> locator.toString().contains("UiScrollable")))).thenReturn(element);
        when(element.isDisplayed()).thenReturn(true);

        Assert.assertSame(page.scrollToAccessibilityId("Views"), element);
    }

    @Test
    public void shouldScrollIntoViewByContentDescription() {
        String locator = MobileBasePage.scrollIntoViewLocator("Views").toString();

        Assert.assertTrue(locator.contains("new UiScrollable(new UiSelector().scrollable(true))"), locator);
        Assert.assertTrue(locator.contains(".scrollIntoView(new UiSelector().description(\"Views\"))"), locator);
    }

    @Test
    public void shouldEscapeQuotesInScrollLocator() {
        String locator = MobileBasePage.scrollIntoViewLocator("Say \"hi\" \\ bye").toString();

        Assert.assertTrue(locator.contains("description(\"Say \\\"hi\\\" \\\\ bye\")"), locator);
    }

    @Test
    public void shouldTakeScreenshot() {
        byte[] screenshot = { 1, 2, 3 };
        when(driver.getScreenshotAs(OutputType.BYTES)).thenReturn(screenshot);

        Assert.assertEquals(page.takeScreenshot(), screenshot);
    }

    @Test
    public void shouldBindToSessionOfCurrentScenario() {
        MobileDriverUtil.setDriverFactory((appiumServerUrl, options) -> driver);
        try {
            MobileDriverUtil.initDriver(MobileConfig.fromProperties(Map.of(
                    MobileConfig.PROPERTY_APP_PACKAGE, "com.example.app",
                    MobileConfig.PROPERTY_EXPLICIT_WAIT_SECONDS, "7")));

            MobileBasePage currentPage = new MobileBasePage();

            Assert.assertSame(currentPage.DRIVER, driver);
            Assert.assertEquals(currentPage.EXPLICIT_WAIT_TIMEOUT, Duration.ofSeconds(7));
        } finally {
            MobileDriverUtil.closeAllDrivers();
            MobileDriverUtil.setDriverFactory(null);
        }
    }

    @Test
    public void shouldRequireSessionForDefaultConstructor() {
        Assert.assertThrows(MobileDriverNotInitializedException.class, MobileBasePage::new);
    }
}
