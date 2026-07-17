package ataf.web.steps;

import ataf.core.helpers.TestDataHelper;
import ataf.core.utils.CryptoUtils;
import ataf.web.model.LocatorType;
import java.lang.reflect.Field;
import java.util.Arrays;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Ludwig Haas (ex.haas02)
 */
public class BaseStepsSsoConfigurationTest {

    private static final String LOCATOR_KEY = "ssoUsernameFieldLocator";

    private static final String LOCATOR_TYPE_KEY = "ssoUsernameFieldLocatorType";

    private static final char[] TEST_DATA_ENCRYPTION_PASSWORD = "BaseStepsSsoConfigurationTestEncryptionPassword123456789".toCharArray();

    private char[] originalEncryptionSecret;

    @BeforeClass
    public void initializeEncryption() {
        originalEncryptionSecret = getCryptoUtilsSecret();
        CryptoUtils.setSecret(TEST_DATA_ENCRYPTION_PASSWORD.clone());
    }

    @BeforeMethod
    public void setUp() {
        TestDataHelper.flushMapSuiteTestData();
        TestDataHelper.initializeTestDataMap();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        TestDataHelper.flushMapTestData();
        TestDataHelper.flushMapSuiteTestData();
    }

    @AfterClass(alwaysRun = true)
    public void clearEncryption() {
        try {
            CryptoUtils.clearSecret();
            CryptoUtils.setSecret(originalEncryptionSecret);
        } finally {
            Arrays.fill(TEST_DATA_ENCRYPTION_PASSWORD, '\0');
        }
    }

    private char[] getCryptoUtilsSecret() {
        try {
            Field secretField = CryptoUtils.class.getDeclaredField("secret");
            secretField.setAccessible(true);
            char[] currentSecret = (char[]) secretField.get(null);
            return currentSecret == null ? null : currentSecret.clone();
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new IllegalStateException("Unable to capture CryptoUtils secret", exception);
        }
    }

    @DataProvider(name = "ssoConfigurationKeys")
    public Object[][] provideSsoConfigurationKeys() {
        return new Object[][] {
                { "ssoUsernameFieldLocator" },
                { "ssoUsernameFieldLocatorType" },
                { "ssoPasswordFieldLocator" },
                { "ssoPasswordFieldLocatorType" },
                { "ssoLoginButtonLocator" },
                { "ssoLoginButtonLocatorType" },
                { "ssoUrlRegexp" }
        };
    }

    @Test(dataProvider = "ssoConfigurationKeys")
    public void shouldResolveAllSsoKeysFromSuiteTestData(String key) {

        String expectedValue = "suite-value-for-" + key;

        TestDataHelper.setSuiteTestData(key, expectedValue);

        String actualValue = BaseSteps.getRequiredSsoTestData(key);

        Assert.assertEquals(actualValue, expectedValue);
    }

    @Test
    public void shouldResolveValueFromTestData() {
        TestDataHelper.setTestData(LOCATOR_KEY, "test-username");

        String result = BaseSteps.getRequiredSsoTestData(LOCATOR_KEY);

        Assert.assertEquals(result, "test-username");
    }

    @Test
    public void shouldResolveValueFromSuiteTestData() {
        TestDataHelper.setSuiteTestData(LOCATOR_KEY, "suite-username");

        String result = BaseSteps.getRequiredSsoTestData(LOCATOR_KEY);

        Assert.assertEquals(result, "suite-username");
    }

    @Test
    public void shouldPreferTestDataOverSuiteTestData() {
        TestDataHelper.setSuiteTestData(LOCATOR_KEY, "suite-username");

        TestDataHelper.setTestData(LOCATOR_KEY, "test-username");

        String result = BaseSteps.getRequiredSsoTestData(LOCATOR_KEY);

        Assert.assertEquals(result, "test-username");
    }

    @Test
    public void shouldFallBackToSuiteDataWhenTestDataIsBlank() {
        TestDataHelper.setSuiteTestData(LOCATOR_KEY, "suite-username");

        TestDataHelper.setTestData(LOCATOR_KEY, "   ");

        String result = BaseSteps.getRequiredSsoTestData(LOCATOR_KEY);

        Assert.assertEquals(result, "suite-username");
    }

    @Test
    public void shouldRejectMissingValue() {
        IllegalArgumentException exception = Assert.expectThrows(IllegalArgumentException.class, () -> BaseSteps.getRequiredSsoTestData(LOCATOR_KEY));

        Assert.assertEquals(exception.getMessage(), "Value for [ssoUsernameFieldLocator] " + "not found in test data or suite test data!");
    }

    @Test
    public void shouldRejectBlankValueInBothScopes() {
        TestDataHelper.setTestData(LOCATOR_KEY, " ");

        TestDataHelper.setSuiteTestData(LOCATOR_KEY, "\t");

        IllegalArgumentException exception = Assert.expectThrows(IllegalArgumentException.class, () -> BaseSteps.getRequiredSsoTestData(LOCATOR_KEY));

        Assert.assertEquals(exception.getMessage(), "Value for [ssoUsernameFieldLocator] " + "not found in test data or suite test data!");
    }

    @Test
    public void shouldResolveLocatorTypeIgnoringCaseAndWhitespace() {
        TestDataHelper.setSuiteTestData(LOCATOR_TYPE_KEY, "  xpath  ");

        LocatorType result = BaseSteps.getRequiredLocatorType(LOCATOR_TYPE_KEY);

        Assert.assertEquals(result, LocatorType.XPATH);
    }

    @Test
    public void shouldPreferTestSpecificLocatorType() {
        TestDataHelper.setSuiteTestData(LOCATOR_TYPE_KEY, LocatorType.ID.name());

        TestDataHelper.setTestData(LOCATOR_TYPE_KEY, LocatorType.CSSSELECTOR.name());

        LocatorType result = BaseSteps.getRequiredLocatorType(LOCATOR_TYPE_KEY);

        Assert.assertEquals(result, LocatorType.CSSSELECTOR);
    }

    @Test
    public void shouldRejectInvalidLocatorType() {
        String invalidValue = "unsupported-locator";

        TestDataHelper.setSuiteTestData(LOCATOR_TYPE_KEY, invalidValue);

        IllegalArgumentException exception = Assert.expectThrows(IllegalArgumentException.class, () -> BaseSteps.getRequiredLocatorType(LOCATOR_TYPE_KEY));

        Assert.assertTrue(exception.getMessage().contains("Invalid LocatorType [" + invalidValue + "]"));

        Assert.assertTrue(exception.getMessage().contains("for [" + LOCATOR_TYPE_KEY + "]"));

        Assert.assertTrue(exception.getMessage().contains(Arrays.toString(LocatorType.values())));

        Assert.assertNotNull(exception.getCause());

        Assert.assertTrue(exception.getCause() instanceof IllegalArgumentException);
    }

    @Test
    public void shouldReportMissingLocatorTypeAsMissingValue() {
        IllegalArgumentException exception = Assert.expectThrows(IllegalArgumentException.class, () -> BaseSteps.getRequiredLocatorType(LOCATOR_TYPE_KEY));

        Assert.assertEquals(exception.getMessage(), "Value for [ssoUsernameFieldLocatorType] " + "not found in test data or suite test data!");
    }
}
