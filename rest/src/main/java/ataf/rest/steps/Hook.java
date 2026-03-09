package ataf.rest.steps;

import ataf.core.context.ScenarioContext;
import ataf.core.context.TestContext;
import ataf.core.context.TestExecutionContext;
import ataf.core.helpers.TestDataHelper;
import ataf.core.logging.ScenarioLogManager;
import ataf.core.utils.CucumberUtils;
import ataf.core.utils.RunnerUtils;
import ataf.core.xray.TestStatus;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;

/**
 * This class contains hooks for setting up and tearing down test scenarios in Cucumber tests. Hooks
 * are methods that are executed before or after test
 * scenarios or the entire test suite.
 */
public class Hook {

    /**
     * Executes once before all test scenarios in the test suite. Logs the start of the test framework
     * setup.
     */
    @BeforeAll
    public static void setupFramework() {
        ScenarioLogManager.getLogger().info("Hook: BeforeAll (Framework-API)");
    }

    /**
     * Executes before each test scenario. Logs the start of the test case setup and checks for
     * scenarios tagged with "@rest".
     *
     * @param scenario The Cucumber scenario being executed
     */
    @Before(value = "@rest")
    public void setupTestCase(Scenario scenario) {
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
    }

    /**
     * Executes after each test scenario. Logs the completion of the scenario and checks for scenarios
     * tagged with "@rest".
     *
     * @param scenario The Cucumber scenario that has completed
     */
    @After(value = "@rest")
    public void sessionReset(Scenario scenario) {
        if (scenario.isFailed()) {
            ScenarioLogManager.getLogger().error("Test failed: {}", scenario.getName());
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
        TestDataHelper.flushMapTestData();
        ScenarioLogManager.getLogger().info("<<<<<<<End of test scenario: {}<<<<<<<", scenario.getName());
        if (RunnerUtils.isJiraBasedTestExecution()) {
            CucumberUtils.attachLogFileAsEvidence();
        }
        ScenarioContext.clear();
        ScenarioLogManager.clear();
        TestContext.clear();
    }

    /**
     * Executes once after all test scenarios in the test suite. Logs the completion of the test
     * framework teardown.
     */
    @AfterAll
    public static void beforeOrAfterAll() {
        ScenarioLogManager.getLogger().info("Hook: AfterAll (Framework-API)");
    }
}
