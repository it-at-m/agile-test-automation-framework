package example.mobile.apidemos;

import ataf.core.runner.BasicTestNGRunner;

/**
 * TestNG runner of the ApiDemos example. The Cucumber options (features, glue, plugins, tags) are
 * read from {@code src/test/resources/cucumber.properties}, which ATAF's runner lifecycle requires.
 * <p>
 * The class name does not match Surefire's default test patterns, so the scenarios only run with
 * the Maven profile {@code android-device}. Mobile scenarios run sequentially: the proof of
 * concept drives one device, so don't switch to {@code ParallelTestNGRunner}.
 * </p>
 */
public class ApiDemosRunner extends BasicTestNGRunner {
}
