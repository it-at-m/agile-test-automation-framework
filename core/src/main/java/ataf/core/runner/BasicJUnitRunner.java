package ataf.core.runner;

import ataf.core.assertions.CustomAssertions;
import ataf.core.assertions.strategy.impl.JUnitAssertionStrategy;
import ataf.core.utils.RunnerUtils;
import org.junit.platform.suite.api.AfterSuite;
import org.junit.platform.suite.api.BeforeSuite;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.Suite;

/**
 * A JUnit test suite runner for executing Cucumber tests.
 *
 * <p>
 * This class serves as a wrapper for running Cucumber tests within the JUnit framework. It includes
 * setup and teardown methods that are executed before and
 * after all tests in the suite, respectively.
 * </p>
 *
 * <p>
 * It uses annotations to specify that it is a test suite and includes the Cucumber engine for
 * executing the tests.
 * </p>
 */
@Suite
@IncludeEngines("cucumber")
public class BasicJUnitRunner {

    /**
     * Initializes the Basic JUnit Runner with JUnit-specific assertion strategy.
     * <p>
     * This constructor sets {@link CustomAssertions} to use {@link JUnitAssertionStrategy}, enabling
     * dynamic assertion handling for JUnit 5. All assertions
     * made using {@code CustomAssertions} will now delegate to JUnit 5's assertion methods.
     * </p>
     */
    public BasicJUnitRunner() {
        CustomAssertions.setStrategy(new JUnitAssertionStrategy());
    }

    /**
     * Sets up the test suite before any tests are run.
     *
     * <p>
     * This method is annotated with {@link BeforeSuite}, indicating that it should be executed once
     * before all tests in the suite. It typically contains
     * initialization logic required for the tests.
     * </p>
     */
    @BeforeSuite
    public void beforeTestSuite() {
        RunnerUtils.setupTestSuite();
    }

    /**
     * Tears down the test suite after all tests have been executed.
     *
     * <p>
     * This method is annotated with {@link AfterSuite}, indicating that it should be executed once
     * after
     * all tests in the suite have completed. It typically
     * contains cleanup logic to release resources or reset states.
     * </p>
     */
    @AfterSuite
    public void afterTestSuite() {
        RunnerUtils.tearDownTestSuite();
    }
}
