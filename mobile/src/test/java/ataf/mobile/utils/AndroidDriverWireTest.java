package ataf.mobile.utils;

import ataf.mobile.config.MobileConfig;
import ataf.mobile.pages.MobileBasePage;
import com.google.gson.JsonElement;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static ataf.mobile.config.MobileConfig.PROPERTY_ADDITIONAL_CAPABILITIES;
import static ataf.mobile.config.MobileConfig.PROPERTY_APPIUM_SERVER_URL;
import static ataf.mobile.config.MobileConfig.PROPERTY_APP_ACTIVITY;
import static ataf.mobile.config.MobileConfig.PROPERTY_APP_PACKAGE;
import static ataf.mobile.config.MobileConfig.PROPERTY_UDID;

/**
 * Drives a real Appium {@link AndroidDriver} against {@link AppiumStubServer}, verifying the HTTP
 * contract with the Appium server (W3C capabilities, base path, screenshots, element lookup and
 * error mapping) without a device.
 */
public class AndroidDriverWireTest {
    private AppiumStubServer server;

    private static MobileConfig config(URL appiumServerUrl) {
        return MobileConfig.fromProperties(Map.of(
                PROPERTY_APPIUM_SERVER_URL, appiumServerUrl.toString(),
                PROPERTY_UDID, "emulator-5554",
                PROPERTY_APP_PACKAGE, "io.appium.android.apis",
                PROPERTY_APP_ACTIVITY, ".ApiDemos",
                PROPERTY_ADDITIONAL_CAPABILITIES, "{\"appium:noReset\":true}"));
    }

    private AppiumStubServer startServer(String basePath) throws IOException {
        server = new AppiumStubServer(basePath);
        return server;
    }

    @AfterMethod(alwaysRun = true)
    public void closeSessionsAndServer() {
        // TestNG runs tests with a timeout on their own thread, so all threads' sessions are closed
        MobileDriverUtil.closeAllDrivers();
        if (server != null) {
            server.close();
            server = null;
        }
    }

    @Test(timeOut = 30000)
    public void shouldStartSessionWithW3cCapabilitiesAndQuitIt() throws IOException {
        AppiumStubServer appium = startServer("");

        AndroidDriver driver = MobileDriverUtil.initDriver(config(appium.url()));

        Assert.assertEquals(String.valueOf(driver.getSessionId()), AppiumStubServer.SESSION_ID);
        Map<String, JsonElement> capabilities = appium.requestedCapabilities();
        Assert.assertTrue(capabilities.get("platformName").getAsString().equalsIgnoreCase("Android"), capabilities.toString());
        Assert.assertEquals(capabilities.get("appium:automationName").getAsString(), "UiAutomator2");
        Assert.assertEquals(capabilities.get("appium:udid").getAsString(), "emulator-5554");
        Assert.assertEquals(capabilities.get("appium:appPackage").getAsString(), "io.appium.android.apis");
        Assert.assertEquals(capabilities.get("appium:appActivity").getAsString(), ".ApiDemos");
        Assert.assertEquals(capabilities.get("appium:newCommandTimeout").getAsLong(), MobileConfig.DEFAULT_NEW_COMMAND_TIMEOUT_SECONDS);
        Assert.assertTrue(capabilities.get("appium:noReset").getAsBoolean());

        MobileDriverUtil.closeDriver();

        Assert.assertTrue(appium.received("DELETE", "/session/" + AppiumStubServer.SESSION_ID), appium.requests().toString());
    }

    @Test(timeOut = 30000)
    public void shouldUseConfiguredBasePath() throws IOException {
        AppiumStubServer appium = startServer("/wd/hub");

        MobileDriverUtil.initDriver(config(appium.url()));

        Assert.assertTrue(appium.received("POST", "/session"), appium.requests().toString());
    }

    @Test(timeOut = 30000)
    public void shouldTakeScreenshotOfSession() throws IOException {
        AppiumStubServer appium = startServer("");
        AndroidDriver driver = MobileDriverUtil.initDriver(config(appium.url()));

        byte[] screenshot = new MobileBasePage(driver, Duration.ofSeconds(1)).takeScreenshot();

        Assert.assertEquals(screenshot, AppiumStubServer.SCREENSHOT);
    }

    @Test(timeOut = 30000)
    public void shouldWaitUntilElementIsFound() throws IOException {
        AppiumStubServer appium = startServer("");
        AndroidDriver driver = MobileDriverUtil.initDriver(config(appium.url()));
        appium.answerNoSuchElement(2);

        WebElement element = new MobileBasePage(driver, Duration.ofSeconds(5)).waitForElementVisible(MobileBasePage.accessibilityId("Views"));

        Assert.assertNotNull(element);
        List<AppiumStubServer.RecordedRequest> findRequests = appium.requests().stream()
                .filter(request -> request.path().equals("/session/" + AppiumStubServer.SESSION_ID + "/element"))
                .toList();
        Assert.assertEquals(findRequests.size(), 3, "Two 'no such element' answers are retried before the element is found");
        Assert.assertTrue(findRequests.getFirst().body().contains("\"accessibility id\""), findRequests.getFirst().body());
    }

    @Test(timeOut = 30000)
    public void shouldExplainSessionRejectedByServer() throws IOException {
        AppiumStubServer appium = startServer("");
        appium.failNewSessionWith("Could not find a connected Android device in 20000ms.");

        MobileDriverInitializationException exception = Assert.expectThrows(MobileDriverInitializationException.class,
                () -> MobileDriverUtil.initDriver(config(appium.url())));

        Assert.assertTrue(exception.getMessage().contains("could not create an Android session for device [emulator-5554]"), exception.getMessage());
        Assert.assertTrue(exception.getMessage().contains("Could not find a connected Android device"), exception.getMessage());
        Assert.assertFalse(MobileDriverUtil.hasDriver());
    }

    @Test(timeOut = 30000)
    public void shouldExplainUnreachableServer() throws IOException {
        int unusedPort;
        try (ServerSocket socket = new ServerSocket(0)) {
            unusedPort = socket.getLocalPort();
        }
        MobileConfig config = MobileConfig.fromProperties(Map.of(
                PROPERTY_APPIUM_SERVER_URL, "http://127.0.0.1:" + unusedPort,
                PROPERTY_APP_PACKAGE, "io.appium.android.apis"));

        MobileDriverInitializationException exception = Assert.expectThrows(MobileDriverInitializationException.class,
                () -> MobileDriverUtil.initDriver(config));

        Assert.assertTrue(exception.getMessage().startsWith("Appium server is not reachable at [http://127.0.0.1:" + unusedPort + "]"),
                exception.getMessage());
        Assert.assertTrue(exception.getMessage().contains("ConnectException"), exception.getMessage());
        Assert.assertFalse(MobileDriverUtil.hasDriver());
    }
}
