package ataf.mobile.utils;

import ataf.mobile.config.MobileConfig;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static ataf.mobile.config.MobileConfig.PROPERTY_ADDITIONAL_CAPABILITIES;
import static ataf.mobile.config.MobileConfig.PROPERTY_APP_ACTIVITY;
import static ataf.mobile.config.MobileConfig.PROPERTY_APP_PACKAGE;
import static ataf.mobile.config.MobileConfig.PROPERTY_NEW_COMMAND_TIMEOUT_SECONDS;
import static ataf.mobile.config.MobileConfig.PROPERTY_PLATFORM_VERSION;
import static ataf.mobile.config.MobileConfig.PROPERTY_UDID;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link MobileDriverUtil}. The Appium server is replaced by a factory returning
 * Mockito mocks of {@link AndroidDriver}.
 */
public class MobileDriverUtilTest {
    private static final MobileConfig CONFIG = MobileConfig.fromProperties(Map.of(
            PROPERTY_UDID, "emulator-5554",
            PROPERTY_APP_PACKAGE, "com.example.app",
            PROPERTY_APP_ACTIVITY, ".MainActivity"));

    private final List<AndroidDriver> createdDrivers = new CopyOnWriteArrayList<>();

    @BeforeMethod
    public void registerFakeDriverFactory() {
        createdDrivers.clear();
        MobileDriverUtil.setDriverFactory((appiumServerUrl, options) -> {
            AndroidDriver driver = mock(AndroidDriver.class);
            createdDrivers.add(driver);
            return driver;
        });
    }

    @AfterMethod(alwaysRun = true)
    public void closeSessions() {
        MobileDriverUtil.closeAllDrivers();
        MobileDriverUtil.setDriverFactory(null);
    }

    @Test
    public void shouldMapConfigurationToUiAutomator2Capabilities() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PROPERTY_UDID, "emulator-5554");
        properties.put(PROPERTY_APP_PACKAGE, "com.example.app");
        properties.put(PROPERTY_APP_ACTIVITY, ".MainActivity");
        properties.put(PROPERTY_PLATFORM_VERSION, "14");
        properties.put(PROPERTY_NEW_COMMAND_TIMEOUT_SECONDS, "300");
        properties.put(PROPERTY_ADDITIONAL_CAPABILITIES, "{\"appium:noReset\":true,\"autoGrantPermissions\":true}");

        Map<String, Object> capabilities = MobileDriverUtil.createOptions(MobileConfig.fromProperties(properties)).asMap();

        Assert.assertTrue(String.valueOf(capabilities.get("platformName")).equalsIgnoreCase("Android"), capabilities.toString());
        Assert.assertEquals(capabilities.get("appium:automationName"), "UiAutomator2");
        Assert.assertEquals(capabilities.get("appium:platformVersion"), "14");
        Assert.assertEquals(capabilities.get("appium:udid"), "emulator-5554");
        Assert.assertEquals(capabilities.get("appium:appPackage"), "com.example.app");
        Assert.assertEquals(capabilities.get("appium:appActivity"), ".MainActivity");
        Assert.assertEquals(capabilities.get("appium:newCommandTimeout"), 300L);
        Assert.assertEquals(capabilities.get("appium:noReset"), Boolean.TRUE);
        Assert.assertEquals(capabilities.get("appium:autoGrantPermissions"), Boolean.TRUE, "Capabilities without vendor prefix get the appium: prefix");
        Assert.assertFalse(capabilities.containsKey("appium:app"));
    }

    @Test
    public void shouldOmitUnsetOptionalCapabilities() {
        Map<String, Object> capabilities = MobileDriverUtil.createOptions(MobileConfig.fromProperties(Map.of(PROPERTY_APP_PACKAGE, "com.example.app")))
                .asMap();

        Assert.assertFalse(capabilities.containsKey("appium:udid"));
        Assert.assertFalse(capabilities.containsKey("appium:platformVersion"));
        Assert.assertFalse(capabilities.containsKey("appium:appActivity"));
        Assert.assertFalse(capabilities.containsKey("appium:app"));
    }

    @Test
    public void shouldLetAdditionalCapabilitiesOverrideDerivedValues() {
        MobileConfig config = MobileConfig.fromProperties(Map.of(
                PROPERTY_UDID, "emulator-5554",
                PROPERTY_APP_PACKAGE, "com.example.app",
                PROPERTY_ADDITIONAL_CAPABILITIES, "{\"appium:udid\":\"R5CT1234ABC\"}"));

        Assert.assertEquals(MobileDriverUtil.createOptions(config).asMap().get("appium:udid"), "R5CT1234ABC");
    }

    @Test
    public void shouldPointToMissingMobileTagWhenNoSessionExists() {
        MobileDriverNotInitializedException exception = Assert.expectThrows(MobileDriverNotInitializedException.class, MobileDriverUtil::getDriver);

        Assert.assertTrue(exception.getMessage().contains("'mobile' tag"), exception.getMessage());
        Assert.assertThrows(MobileDriverNotInitializedException.class, MobileDriverUtil::getConfig);
        Assert.assertFalse(MobileDriverUtil.hasDriver());
    }

    @Test
    public void shouldRegisterStartedSessionForCurrentThread() {
        AndroidDriver driver = MobileDriverUtil.initDriver(CONFIG);

        Assert.assertTrue(MobileDriverUtil.hasDriver());
        Assert.assertSame(MobileDriverUtil.getDriver(), driver);
        Assert.assertSame(MobileDriverUtil.getConfig(), CONFIG);
        Assert.assertEquals(createdDrivers, List.of(driver));
    }

    @Test
    public void shouldQuitAndRemoveSessionOnClose() {
        AndroidDriver driver = MobileDriverUtil.initDriver(CONFIG);

        MobileDriverUtil.closeDriver();

        verify(driver).quit();
        Assert.assertFalse(MobileDriverUtil.hasDriver());
        Assert.assertThrows(MobileDriverNotInitializedException.class, MobileDriverUtil::getDriver);
    }

    @Test
    public void shouldIgnoreCloseWithoutSession() {
        MobileDriverUtil.closeDriver();

        Assert.assertFalse(MobileDriverUtil.hasDriver());
    }

    @Test
    public void shouldNotRethrowErrorsWhileQuitting() {
        AndroidDriver driver = MobileDriverUtil.initDriver(CONFIG);
        doThrow(new WebDriverException("Session is already gone")).when(driver).quit();

        MobileDriverUtil.closeDriver();

        Assert.assertFalse(MobileDriverUtil.hasDriver());
    }

    @Test
    public void shouldCloseLeftoverSessionOfSameThreadBeforeStartingNewOne() {
        AndroidDriver first = MobileDriverUtil.initDriver(CONFIG);

        AndroidDriver second = MobileDriverUtil.initDriver(CONFIG);

        verify(first).quit();
        Assert.assertSame(MobileDriverUtil.getDriver(), second);
    }

    @Test
    public void shouldRejectConcurrentSessionFromAnotherThread() throws Exception {
        MobileDriverUtil.initDriver(CONFIG);
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            Future<Throwable> result = executor.submit(() -> {
                try {
                    MobileDriverUtil.initDriver(CONFIG);
                    return null;
                } catch (Throwable throwable) {
                    return throwable;
                }
            });
            Throwable failure = result.get(10, TimeUnit.SECONDS);

            Assert.assertTrue(failure instanceof MobileDriverInitializationException, String.valueOf(failure));
            Assert.assertTrue(failure.getMessage().contains("one device and one session at a time"), failure.getMessage());
            Assert.assertEquals(createdDrivers.size(), 1, "No second session may be started");
        }
    }

    @Test
    public void shouldExplainUnreachableAppiumServer() {
        WebDriverException failure = new WebDriverException("Could not start a new session", new ConnectException("Connection refused"));
        MobileDriverUtil.setDriverFactory((appiumServerUrl, options) -> {
            throw failure;
        });

        MobileDriverInitializationException exception = Assert.expectThrows(MobileDriverInitializationException.class,
                () -> MobileDriverUtil.initDriver(CONFIG));

        Assert.assertTrue(exception.getMessage().startsWith("Appium server is not reachable at [http://127.0.0.1:4723]"), exception.getMessage());
        Assert.assertTrue(exception.getMessage().contains("Connection refused"), exception.getMessage());
        Assert.assertSame(exception.getCause(), failure);
        Assert.assertFalse(MobileDriverUtil.hasDriver());
    }

    @Test
    public void shouldExplainSessionRejectedByAppium() {
        MobileDriverUtil.setDriverFactory((appiumServerUrl, options) -> {
            throw new SessionNotCreatedException("Could not find a connected Android device in 20000ms.");
        });

        MobileDriverInitializationException exception = Assert.expectThrows(MobileDriverInitializationException.class,
                () -> MobileDriverUtil.initDriver(CONFIG));

        Assert.assertTrue(exception.getMessage().contains("could not create an Android session for device [emulator-5554]"), exception.getMessage());
        Assert.assertTrue(exception.getMessage().contains("app [com.example.app/.MainActivity]"), exception.getMessage());
        Assert.assertTrue(exception.getMessage().contains("Could not find a connected Android device in 20000ms."), exception.getMessage());
        Assert.assertFalse(exception.getMessage().contains("Build info"), "Only the first line of Selenium's message is kept");
        Assert.assertFalse(MobileDriverUtil.hasDriver());
    }

    @Test
    public void shouldDescribeOtherStartupFailures() {
        MobileDriverUtil.setDriverFactory((appiumServerUrl, options) -> {
            throw new IllegalStateException("Unexpected response");
        });

        MobileDriverInitializationException exception = Assert.expectThrows(MobileDriverInitializationException.class,
                () -> MobileDriverUtil.initDriver(CONFIG));

        Assert.assertTrue(exception.getMessage().contains("failed: java.lang.IllegalStateException: Unexpected response"), exception.getMessage());
    }

    @Test
    public void shouldRejectFactoryReturningNoDriver() {
        MobileDriverUtil.setDriverFactory((appiumServerUrl, options) -> null);

        Assert.assertThrows(MobileDriverInitializationException.class, () -> MobileDriverUtil.initDriver(CONFIG));
        Assert.assertFalse(MobileDriverUtil.hasDriver());
    }

    @Test
    public void shouldCloseAllSessions() {
        AndroidDriver driver = MobileDriverUtil.initDriver(CONFIG);

        MobileDriverUtil.closeAllDrivers();

        verify(driver).quit();
        Assert.assertFalse(MobileDriverUtil.hasDriver());
    }
}
