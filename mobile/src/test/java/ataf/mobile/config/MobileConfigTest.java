package ataf.mobile.config;

import ataf.core.properties.TestProperties;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ataf.mobile.config.MobileConfig.PROPERTY_ADDITIONAL_CAPABILITIES;
import static ataf.mobile.config.MobileConfig.PROPERTY_APP;
import static ataf.mobile.config.MobileConfig.PROPERTY_APPIUM_SERVER_URL;
import static ataf.mobile.config.MobileConfig.PROPERTY_APP_ACTIVITY;
import static ataf.mobile.config.MobileConfig.PROPERTY_APP_PACKAGE;
import static ataf.mobile.config.MobileConfig.PROPERTY_AUTOMATION_NAME;
import static ataf.mobile.config.MobileConfig.PROPERTY_EXPLICIT_WAIT_SECONDS;
import static ataf.mobile.config.MobileConfig.PROPERTY_NEW_COMMAND_TIMEOUT_SECONDS;
import static ataf.mobile.config.MobileConfig.PROPERTY_PAGE_SOURCE_ON_FAILURE;
import static ataf.mobile.config.MobileConfig.PROPERTY_PLATFORM_NAME;
import static ataf.mobile.config.MobileConfig.PROPERTY_PLATFORM_VERSION;
import static ataf.mobile.config.MobileConfig.PROPERTY_SCREENSHOT_ON_FAILURE;
import static ataf.mobile.config.MobileConfig.PROPERTY_UDID;

/**
 * Unit tests for {@link MobileConfig}: defaults, validation and the mapping of
 * {@code testautomation.mobile.*} properties.
 */
public class MobileConfigTest {

    private static Map<String, Object> withAppPackage() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PROPERTY_APP_PACKAGE, "com.example.app");
        return properties;
    }

    private static MobileConfigException expectConfigException(Map<String, Object> properties) {
        return Assert.expectThrows(MobileConfigException.class, () -> MobileConfig.fromProperties(properties));
    }

    @Test
    public void shouldApplyDefaultsWhenOnlyAppPackageIsSet() {
        MobileConfig config = MobileConfig.fromProperties(withAppPackage());

        Assert.assertEquals(config.appiumServerUrl().toString(), MobileConfig.DEFAULT_APPIUM_SERVER_URL);
        Assert.assertEquals(config.platformName(), "Android");
        Assert.assertEquals(config.automationName(), "UiAutomator2");
        Assert.assertNull(config.platformVersion());
        Assert.assertNull(config.udid());
        Assert.assertNull(config.appActivity());
        Assert.assertNull(config.app());
        Assert.assertEquals(config.newCommandTimeout(), Duration.ofSeconds(MobileConfig.DEFAULT_NEW_COMMAND_TIMEOUT_SECONDS));
        Assert.assertEquals(config.explicitWaitTimeout(), Duration.ofSeconds(MobileConfig.DEFAULT_EXPLICIT_WAIT_SECONDS));
        Assert.assertTrue(config.screenshotOnFailure());
        Assert.assertFalse(config.pageSourceOnFailure());
        Assert.assertTrue(config.additionalCapabilities().isEmpty());
    }

    @Test
    public void shouldReadAllConfiguredValues() {
        Map<String, Object> properties = withAppPackage();
        properties.put(PROPERTY_APPIUM_SERVER_URL, "https://appium.example.org:4443");
        properties.put(PROPERTY_PLATFORM_NAME, "Android");
        properties.put(PROPERTY_PLATFORM_VERSION, "14");
        properties.put(PROPERTY_AUTOMATION_NAME, "UiAutomator2");
        properties.put(PROPERTY_UDID, "R5CT1234ABC");
        properties.put(PROPERTY_APP_ACTIVITY, ".MainActivity");
        properties.put(PROPERTY_NEW_COMMAND_TIMEOUT_SECONDS, "300");
        properties.put(PROPERTY_EXPLICIT_WAIT_SECONDS, "5");
        properties.put(PROPERTY_SCREENSHOT_ON_FAILURE, "false");
        properties.put(PROPERTY_PAGE_SOURCE_ON_FAILURE, "TRUE");

        MobileConfig config = MobileConfig.fromProperties(properties);

        Assert.assertEquals(config.appiumServerUrl().toString(), "https://appium.example.org:4443");
        Assert.assertEquals(config.platformVersion(), "14");
        Assert.assertEquals(config.udid(), "R5CT1234ABC");
        Assert.assertEquals(config.appPackage(), "com.example.app");
        Assert.assertEquals(config.appActivity(), ".MainActivity");
        Assert.assertEquals(config.newCommandTimeout(), Duration.ofSeconds(300));
        Assert.assertEquals(config.explicitWaitTimeout(), Duration.ofSeconds(5));
        Assert.assertFalse(config.screenshotOnFailure());
        Assert.assertTrue(config.pageSourceOnFailure());
    }

    @Test
    public void shouldKeepBasePathAndRemoveTrailingSlashes() {
        Map<String, Object> properties = withAppPackage();
        properties.put(PROPERTY_APPIUM_SERVER_URL, "http://appium-host:4723/wd/hub//");

        Assert.assertEquals(MobileConfig.fromProperties(properties).appiumServerUrl().toString(), "http://appium-host:4723/wd/hub");
    }

    @Test
    public void shouldAcceptTypedPropertyValues() {
        Map<String, Object> properties = withAppPackage();
        properties.put(PROPERTY_NEW_COMMAND_TIMEOUT_SECONDS, 90L);
        properties.put(PROPERTY_EXPLICIT_WAIT_SECONDS, 7);
        properties.put(PROPERTY_SCREENSHOT_ON_FAILURE, Boolean.FALSE);

        MobileConfig config = MobileConfig.fromProperties(properties);

        Assert.assertEquals(config.newCommandTimeout(), Duration.ofSeconds(90));
        Assert.assertEquals(config.explicitWaitTimeout(), Duration.ofSeconds(7));
        Assert.assertFalse(config.screenshotOnFailure());
    }

    @Test
    public void shouldTreatBlankValuesAsUnset() {
        Map<String, Object> properties = withAppPackage();
        properties.put(PROPERTY_APPIUM_SERVER_URL, "  ");
        properties.put(PROPERTY_UDID, "");
        properties.put(PROPERTY_PLATFORM_VERSION, " ");
        properties.put(PROPERTY_NEW_COMMAND_TIMEOUT_SECONDS, "");
        properties.put(PROPERTY_ADDITIONAL_CAPABILITIES, " ");

        MobileConfig config = MobileConfig.fromProperties(properties);

        Assert.assertEquals(config.appiumServerUrl().toString(), MobileConfig.DEFAULT_APPIUM_SERVER_URL);
        Assert.assertNull(config.udid());
        Assert.assertNull(config.platformVersion());
        Assert.assertEquals(config.newCommandTimeout(), Duration.ofSeconds(MobileConfig.DEFAULT_NEW_COMMAND_TIMEOUT_SECONDS));
        Assert.assertTrue(config.additionalCapabilities().isEmpty());
    }

    @Test
    public void shouldRejectMissingAppTarget() {
        MobileConfigException exception = expectConfigException(new HashMap<>());

        Assert.assertEquals(exception.getProblems().size(), 1);
        Assert.assertTrue(exception.getMessage().contains("testautomation.mobile.appPackage"), exception.getMessage());
        Assert.assertTrue(exception.getMessage().contains("testautomation.mobile.app]"), exception.getMessage());
    }

    @DataProvider(name = "invalidUrls")
    public Object[][] invalidUrls() {
        return new Object[][] {
                { "htp://127.0.0.1:4723" },
                { "ftp://127.0.0.1:4723" },
                { "127.0.0.1:4723" },
                { "http://" },
                { "not a url" }
        };
    }

    @Test(dataProvider = "invalidUrls")
    public void shouldRejectInvalidAppiumServerUrls(String url) {
        Map<String, Object> properties = withAppPackage();
        properties.put(PROPERTY_APPIUM_SERVER_URL, url);

        MobileConfigException exception = expectConfigException(properties);

        Assert.assertTrue(exception.getMessage().contains("[testautomation.mobile.appiumServerUrl] must be an http(s) URL"), exception.getMessage());
        Assert.assertTrue(exception.getMessage().contains(url), exception.getMessage());
    }

    @Test
    public void shouldRejectNonAndroidPlatforms() {
        Map<String, Object> properties = withAppPackage();
        properties.put(PROPERTY_PLATFORM_NAME, "iOS");

        MobileConfigException exception = expectConfigException(properties);

        Assert.assertTrue(exception.getMessage().contains("[testautomation.mobile.platformName] must be Android"), exception.getMessage());
    }

    @Test
    public void shouldAcceptPlatformNameIgnoringCase() {
        Map<String, Object> properties = withAppPackage();
        properties.put(PROPERTY_PLATFORM_NAME, "android");

        Assert.assertEquals(MobileConfig.fromProperties(properties).platformName(), "Android");
    }

    @DataProvider(name = "invalidSeconds")
    public Object[][] invalidSeconds() {
        return new Object[][] { { "abc" }, { "0" }, { "-5" }, { "2.5" }, { 0L } };
    }

    @Test(dataProvider = "invalidSeconds")
    public void shouldRejectInvalidTimeouts(Object seconds) {
        Map<String, Object> properties = withAppPackage();
        properties.put(PROPERTY_EXPLICIT_WAIT_SECONDS, seconds);

        MobileConfigException exception = expectConfigException(properties);

        Assert.assertTrue(exception.getMessage().contains("[testautomation.mobile.explicitWaitSeconds] must be a positive whole number of seconds"),
                exception.getMessage());
    }

    @Test
    public void shouldRejectInvalidBooleans() {
        Map<String, Object> properties = withAppPackage();
        properties.put(PROPERTY_SCREENSHOT_ON_FAILURE, "yes");

        MobileConfigException exception = expectConfigException(properties);

        Assert.assertTrue(exception.getMessage().contains("[testautomation.mobile.screenshotOnFailure] must be true or false"), exception.getMessage());
    }

    @Test
    public void shouldResolveApkPathToAbsolutePath() throws IOException {
        Path targetDirectory = Files.createDirectories(Path.of("target"));
        Path apk = Files.createTempFile(targetDirectory, "mobile-config-test", ".apk");
        try {
            Map<String, Object> properties = new HashMap<>();
            properties.put(PROPERTY_APP, Path.of("target").resolve(apk.getFileName()).toString());

            MobileConfig config = MobileConfig.fromProperties(properties);

            Assert.assertEquals(config.app(), apk.toAbsolutePath().normalize().toString());
            Assert.assertNull(config.appPackage(), "An APK alone is a valid app target");
        } finally {
            Files.deleteIfExists(apk);
        }
    }

    @Test
    public void shouldRejectMissingApkFile() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PROPERTY_APP, "does/not/exist/app.apk");

        MobileConfigException exception = expectConfigException(properties);

        Assert.assertEquals(exception.getProblems().size(), 1, exception.getMessage());
        Assert.assertTrue(exception.getMessage().contains("which is not an existing file"), exception.getMessage());
    }

    @Test
    public void shouldPassApkUrlsThrough() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PROPERTY_APP, "https://downloads.example.org/app-release.apk");

        Assert.assertEquals(MobileConfig.fromProperties(properties).app(), "https://downloads.example.org/app-release.apk");
    }

    @Test
    public void shouldParseAdditionalCapabilitiesWithTheirTypes() {
        Map<String, Object> properties = withAppPackage();
        properties.put(PROPERTY_ADDITIONAL_CAPABILITIES,
                "{\"appium:noReset\":true,\"appium:adbExecTimeout\":60000,\"appium:language\":\"de\",\"appium:settings\":{\"waitForIdleTimeout\":100}}");

        Map<String, Object> capabilities = MobileConfig.fromProperties(properties).additionalCapabilities();

        Assert.assertEquals(capabilities.get("appium:noReset"), Boolean.TRUE);
        Assert.assertEquals(capabilities.get("appium:adbExecTimeout"), 60000L);
        Assert.assertEquals(capabilities.get("appium:language"), "de");
        Assert.assertEquals(capabilities.get("appium:settings"), Map.of("waitForIdleTimeout", 100L));
        Assert.assertEquals(List.copyOf(capabilities.keySet()),
                List.of("appium:noReset", "appium:adbExecTimeout", "appium:language", "appium:settings"), "The order of the JSON object is kept");
    }

    @DataProvider(name = "invalidCapabilities")
    public Object[][] invalidCapabilities() {
        return new Object[][] {
                { "[\"appium:noReset\"]", "must be a JSON object" },
                { "{\"appium:noReset\":", "is not valid JSON" },
                { "{\"appium:noReset\":null}", "without a value" },
                { "{\"\":true}", "without a name" }
        };
    }

    @Test(dataProvider = "invalidCapabilities")
    public void shouldRejectInvalidAdditionalCapabilities(String json, String expectedProblem) {
        Map<String, Object> properties = withAppPackage();
        properties.put(PROPERTY_ADDITIONAL_CAPABILITIES, json);

        MobileConfigException exception = expectConfigException(properties);

        Assert.assertTrue(exception.getMessage().contains(expectedProblem), exception.getMessage());
    }

    @Test
    public void shouldReportAllProblemsTogether() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PROPERTY_APPIUM_SERVER_URL, "htp:/broken");
        properties.put(PROPERTY_PLATFORM_NAME, "iOS");
        properties.put(PROPERTY_NEW_COMMAND_TIMEOUT_SECONDS, "soon");

        MobileConfigException exception = expectConfigException(properties);

        Assert.assertEquals(exception.getProblems().size(), 4, exception.getMessage());
        for (String problem : exception.getProblems()) {
            Assert.assertTrue(exception.getMessage().contains(problem));
        }
    }

    @Test
    public void shouldMaskSensitiveCapabilitiesInToString() {
        Map<String, Object> properties = withAppPackage();
        properties.put(PROPERTY_ADDITIONAL_CAPABILITIES,
                "{\"appium:keystorePassword\":\"keystore-secret\",\"appium:unlockKey\":\"1234\",\"appium:noReset\":true}");

        String description = MobileConfig.fromProperties(properties).toString();

        Assert.assertFalse(description.contains("keystore-secret"), description);
        Assert.assertFalse(description.contains("1234"), description);
        Assert.assertTrue(description.contains("appium:noReset=true"), description);
        Assert.assertTrue(description.contains("appium:keystorePassword=******"), description);
    }

    @Test
    public void shouldMaskOnlySensitiveCapabilityNames() {
        Map<String, Object> masked = MobileConfig.maskSensitiveCapabilities(Map.of(
                "appium:appPackage", "com.example.app",
                "appium:keyPassword", "secret",
                "appium:accessToken", "token-value",
                "appium:unlockKey", "1234"));

        Assert.assertEquals(masked.get("appium:appPackage"), "com.example.app");
        Assert.assertEquals(masked.get("appium:keyPassword"), "******");
        Assert.assertEquals(masked.get("appium:accessToken"), "******");
        Assert.assertEquals(masked.get("appium:unlockKey"), "******");
    }

    @Test
    public void shouldLoadMobilePropertiesFromTestProperties() {
        // Compared with the raw TestProperties values, so -D overrides of a device run don't break the test
        MobileConfig config = MobileConfig.load();

        Assert.assertEquals(config.appPackage(), TestProperties.getProperty(PROPERTY_APP_PACKAGE).orElseThrow());
        Assert.assertEquals(config.appActivity(), TestProperties.getProperty(PROPERTY_APP_ACTIVITY).orElseThrow());

        long expectedExplicitWaitSeconds;
        long expectedNewCommandTimeoutSeconds;
        try {
            expectedExplicitWaitSeconds = Long.parseLong(
                    TestProperties.getProperty(PROPERTY_EXPLICIT_WAIT_SECONDS).orElseThrow().toString());
            expectedNewCommandTimeoutSeconds = Long.parseLong(
                    TestProperties.getProperty(PROPERTY_NEW_COMMAND_TIMEOUT_SECONDS).orElseThrow().toString());
        } catch (NumberFormatException e) {
            Assert.fail("Invalid numeric mobile timeout property value", e);
            return;
        }

        Assert.assertEquals(config.explicitWaitTimeout().toSeconds(), expectedExplicitWaitSeconds);
        Assert.assertEquals(config.newCommandTimeout().toSeconds(), expectedNewCommandTimeoutSeconds);
        Assert.assertFalse(config.additionalCapabilities().isEmpty(), "src/test/resources/testautomation.properties defines additional capabilities");
    }

    @Test
    public void shouldNameExternalProperties() {
        Assert.assertEquals(MobileConfig.externalName(PROPERTY_UDID), "testautomation.mobile.udid");
    }
}
