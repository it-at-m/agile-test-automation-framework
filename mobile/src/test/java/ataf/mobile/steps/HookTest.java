package ataf.mobile.steps;

import ataf.core.context.ScenarioContext;
import ataf.mobile.utils.MobileDriverInitializationException;
import ataf.mobile.utils.MobileDriverUtil;
import io.appium.java_client.android.AndroidDriver;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.endsWith;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the mobile {@link Hook} with a mocked Cucumber {@link Scenario} and a mocked
 * {@link AndroidDriver}.
 */
public class HookTest {
    private final Hook hook = new Hook();

    private AndroidDriver driver;
    private Scenario scenario;

    @BeforeMethod
    public void setUp() {
        driver = mock(AndroidDriver.class);
        scenario = mock(Scenario.class);
        when(scenario.getName()).thenReturn("Mobile hook unit test");
        MobileDriverUtil.setDriverFactory((appiumServerUrl, options) -> driver);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        MobileDriverUtil.closeAllDrivers();
        MobileDriverUtil.setDriverFactory(null);
        ScenarioContext.clear();
    }

    @Test
    public void shouldStartSessionBeforeMobileScenario() {
        hook.initializeTest(scenario);

        Assert.assertSame(MobileDriverUtil.getDriver(), driver);
        Assert.assertSame(ScenarioContext.get(), scenario);
    }

    @Test
    public void shouldCloseSessionOfPassedScenarioWithoutScreenshot() {
        hook.initializeTest(scenario);
        when(scenario.isFailed()).thenReturn(false);

        hook.tearDownTest(scenario);

        verify(driver).quit();
        verify(driver, never()).getScreenshotAs(any());
        Assert.assertFalse(MobileDriverUtil.hasDriver());
        Assert.assertNull(ScenarioContext.get());
    }

    @Test
    public void shouldAttachScreenshotOfFailedScenario() {
        byte[] screenshot = { 1, 2, 3 };
        when(driver.getScreenshotAs(OutputType.BYTES)).thenReturn(screenshot);
        hook.initializeTest(scenario);
        when(scenario.isFailed()).thenReturn(true);

        hook.tearDownTest(scenario);

        verify(scenario).attach(eq(screenshot), eq("image/png"), endsWith("_Screenshot_of_failure.png"));
        verify(driver, never()).getPageSource();
        verify(driver).quit();
        Assert.assertFalse(MobileDriverUtil.hasDriver());
    }

    @Test
    public void shouldNotFailTearDownWhenScreenshotAndQuitFail() {
        when(driver.getScreenshotAs(OutputType.BYTES)).thenThrow(new WebDriverException("Device disconnected"));
        doThrow(new WebDriverException("Session is already gone")).when(driver).quit();
        hook.initializeTest(scenario);
        when(scenario.isFailed()).thenReturn(true);

        hook.tearDownTest(scenario);

        Assert.assertFalse(MobileDriverUtil.hasDriver());
        Assert.assertNull(ScenarioContext.get());
    }

    @Test
    public void shouldTearDownScenarioWhoseSessionCouldNotBeStarted() {
        MobileDriverUtil.setDriverFactory((appiumServerUrl, options) -> {
            throw new SessionNotCreatedException("Could not find a connected Android device");
        });
        Assert.assertThrows(MobileDriverInitializationException.class, () -> hook.initializeTest(scenario));
        when(scenario.isFailed()).thenReturn(true);

        hook.tearDownTest(scenario);

        verify(scenario, never()).attach(any(byte[].class), anyString(), anyString());
        Assert.assertFalse(MobileDriverUtil.hasDriver());
        Assert.assertNull(ScenarioContext.get());
    }

    @Test
    public void shouldOnlyRunForMobileScenarios() throws NoSuchMethodException {
        Before before = Hook.class.getMethod("initializeTest", Scenario.class).getAnnotation(Before.class);
        After after = Hook.class.getMethod("tearDownTest", Scenario.class).getAnnotation(After.class);

        Assert.assertEquals(before.value(), "@mobile");
        Assert.assertEquals(after.value(), "@mobile");
        Assert.assertEquals(before.order(), Hook.HOOK_ORDER);
        Assert.assertEquals(after.order(), Hook.HOOK_ORDER);
    }
}
