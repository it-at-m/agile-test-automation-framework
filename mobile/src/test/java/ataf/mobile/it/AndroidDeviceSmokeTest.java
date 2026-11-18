package ataf.mobile.it;

import ataf.mobile.config.MobileConfig;
import ataf.mobile.pages.MobileBasePage;
import ataf.mobile.utils.MobileDriverUtil;
import io.appium.java_client.android.AndroidDriver;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Opt-in smoke test against a real Appium server and Android device.
 * <p>
 * Disabled by default. Enable it with {@code -Dataf.it.mobile.enabled=true} and select the device
 * with {@code -Dtestautomation.mobile.udid=<adb serial>}. By default it launches the Settings app
 * configured in {@code src/test/resources/testautomation.properties}.
 * </p>
 */
public class AndroidDeviceSmokeTest {

    @Test
    public void shouldStartSessionOnConnectedDevice() throws IOException {
        if (!Boolean.getBoolean("ataf.it.mobile.enabled")) {
            throw new SkipException(
                    "Android device test disabled; run with -Dataf.it.mobile.enabled=true -Dtestautomation.mobile.udid=<adb serial> to enable.");
        }
        try {
            AndroidDriver driver = MobileDriverUtil.initDriver(MobileConfig.load());
            MobileConfig config = MobileDriverUtil.getConfig();

            Assert.assertNotNull(driver.getSessionId(), "Appium returned no session id");
            if (config.appPackage() != null) {
                Assert.assertEquals(driver.getCurrentPackage(), config.appPackage(), "The configured app is not in the foreground");
            }
            byte[] screenshot = new MobileBasePage(driver, config.explicitWaitTimeout()).takeScreenshot();
            Assert.assertTrue(screenshot.length > 0, "The screenshot is empty");
            Files.write(Files.createDirectories(Path.of("target")).resolve("android-device-smoke-test.png"), screenshot);
        } finally {
            MobileDriverUtil.closeDriver();
        }
    }
}
