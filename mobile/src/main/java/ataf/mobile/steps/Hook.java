package ataf.mobile.steps;

import ataf.core.context.ScenarioContext;
import ataf.core.context.TestContext;
import ataf.core.context.TestExecutionContext;
import ataf.core.helpers.TestDataHelper;
import ataf.core.logging.ScenarioLogManager;
import ataf.core.utils.CucumberUtils;
import ataf.core.utils.DateUtils;
import ataf.core.utils.RunnerUtils;
import ataf.core.xray.TestStatus;
import ataf.mobile.config.MobileConfig;
import ataf.mobile.utils.MobileDriverUtil;
import io.appium.java_client.android.AndroidDriver;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;

import java.nio.charset.StandardCharsets;

/**
 * Provides setup and teardown of Cucumber scenarios tagged with {@code @mobile}.
 * <p>
 * Before a mobile scenario, the hook reads the {@code testautomation.mobile.*} configuration and
 * starts an Appium session on the configured Android device. After the scenario, it attaches a
 * screenshot (and optionally the page source) if the scenario failed, attaches the test data and
 * closes the session. Scenarios without the {@code @mobile} tag, such as {@code @web} or
 * {@code @rest} scenarios, are not affected.
 * </p>
 * <p>
 * The hooks run with order {@value #HOOK_ORDER}: the session already exists in consumer
 * {@code @Before} hooks with the default order and is still open in their {@code @After} hooks.
 * Cleanup never throws, so the original failure of a scenario stays visible in the reports.
 * </p>
 */
public class Hook {
    /**
     * Order of the mobile hooks. Lower than Cucumber's default (10000), so the session starts before
     * and closes after consumer hooks with the default order.
     */
    public static final int HOOK_ORDER = 1000;

    /**
     * Creates the hook. Cucumber instantiates it for every scenario.
     */
    public Hook() {
    }

    /**
     * Captures a screenshot from the provided AndroidDriver instance and attaches it to the current
     * scenario. Errors are logged and not rethrown.
     *
     * @param driver the AndroidDriver instance used to capture the screenshot.
     * @param fileName the name of the attachment (without extension).
     */
    public static void makeScreenshot(AndroidDriver driver, String fileName) {
        try {
            final byte[] screenshot = driver.getScreenshotAs(OutputType.BYTES);
            ScenarioContext.get().attach(screenshot, "image/png", fileName + ".png");
            ScenarioContext.get().log("Made screenshot \"" + fileName + ".png" + "\"");
        } catch (Exception e) {
            ScenarioLogManager.getLogger().error("Making of screenshot has failed,", e);
        }
    }

    /**
     * Initializes a mobile scenario: sets up scenario context and test data, then starts the session.
     *
     * @param scenario the Cucumber scenario being executed.
     * @throws ataf.mobile.config.MobileConfigException if the mobile configuration is invalid.
     * @throws ataf.mobile.utils.MobileDriverInitializationException if the session cannot be started.
     */
    @Before(value = "@mobile", order = HOOK_ORDER)
    public void initializeTest(Scenario scenario) {
        ScenarioContext.put(scenario);
        if (RunnerUtils.isJiraBasedTestExecution()) {
            TestExecutionContext.set(scenario);
            TestExecutionContext.get().assign();
            TestContext.set(scenario);
            TestContext.get().assign();
            TestContext.get().setStatus(TestStatus.EXECUTING);
        }
        ScenarioLogManager.getLogger().info(">>>>>>>Start of test scenario: {}>>>>>>>", scenario.getName());
        TestDataHelper.initializeTestDataMap();
        MobileDriverUtil.initDriver(MobileConfig.load());
    }

    /**
     * Cleans up after a mobile scenario: attaches failure diagnostics and test data, closes the
     * Appium session and clears the scenario contexts.
     *
     * @param scenario the Cucumber scenario being executed, used to check the outcome.
     */
    @After(value = "@mobile", order = HOOK_ORDER)
    public void tearDownTest(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                ScenarioLogManager.getLogger().error("Test failed: {}", scenario.getName());
                attachFailureDiagnostics();
                if (RunnerUtils.isJiraBasedTestExecution()) {
                    TestContext.get().setStatus(TestStatus.FAIL);
                }
            } else {
                ScenarioLogManager.getLogger().info("Test passed: {}", scenario.getName());
                if (RunnerUtils.isJiraBasedTestExecution()) {
                    TestContext.get().setStatus(TestStatus.PASS);
                }
            }
            CucumberUtils.attachTestData();
        } finally {
            TestDataHelper.flushMapTestData();
            MobileDriverUtil.closeDriver();
            ScenarioLogManager.getLogger().info("<<<<<<<End of test scenario: {}<<<<<<<", scenario.getName());
            if (RunnerUtils.isJiraBasedTestExecution()) {
                CucumberUtils.attachLogFileAsEvidence();
            }
            ScenarioContext.clear();
            ScenarioLogManager.clear();
            TestContext.clear();
        }
    }

    private static void attachFailureDiagnostics() {
        if (!MobileDriverUtil.hasDriver()) {
            ScenarioLogManager.getLogger().warn("No Android session available, so no screenshot of the failure can be taken.");
            return;
        }
        AndroidDriver driver = MobileDriverUtil.getDriver();
        MobileConfig config = MobileDriverUtil.getConfig();
        String timestamp = DateUtils.getFileTimestamp();
        if (config.screenshotOnFailure()) {
            makeScreenshot(driver, timestamp + "_Screenshot_of_failure");
        }
        if (config.pageSourceOnFailure()) {
            attachPageSource(driver, timestamp + "_Page_source_of_failure");
        }
    }

    private static void attachPageSource(AndroidDriver driver, String fileName) {
        try {
            String pageSource = driver.getPageSource();
            if (pageSource != null) {
                ScenarioContext.get().attach(pageSource.getBytes(StandardCharsets.UTF_8), "text/xml", fileName + ".xml");
                ScenarioContext.get().log("Saved page source to \"" + fileName + ".xml\"");
            }
        } catch (Exception e) {
            ScenarioLogManager.getLogger().error("Saving the page source has failed,", e);
        }
    }
}
