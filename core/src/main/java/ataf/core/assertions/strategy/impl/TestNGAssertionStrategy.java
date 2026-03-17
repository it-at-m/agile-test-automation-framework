package ataf.core.assertions.strategy.impl;

import ataf.core.assertions.strategy.AssertionStrategy;
import ataf.core.assertions.strategy.ThrowingRunnable;
import org.testng.Assert;

/**
 * A concrete implementation of {@link AssertionStrategy} using TestNG's assertion methods.
 * <p>
 * This class adapts the TestNG assertion library to the common {@link AssertionStrategy} interface,
 * enabling unified assertion handling. Each method
 * corresponds to a specific assertion in {@link Assert}.
 * </p>
 */
public class TestNGAssertionStrategy implements AssertionStrategy {

    /**
     * Asserts that the specified object is not null using TestNG's
     * {@link Assert#assertNotNull(Object)}.
     *
     * @param object The object to check for nullity.
     * @throws AssertionError if the object is null.
     */
    @Override
    public void assertNotNull(Object object) {
        Assert.assertNotNull(object);
    }

    /**
     * Asserts that the specified object is not null using TestNG's
     * {@link Assert#assertNotNull(Object, String)}.
     *
     * @param object The object to check for nullity.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the object is null.
     */
    @Override
    public void assertNotNull(Object object, String message) {
        Assert.assertNotNull(object, message);
    }

    /**
     * Fails a test with the specified message using TestNG's {@link Assert#fail(String)}.
     *
     * @param message The assertion message to display on failure.
     * @throws AssertionError unconditionally.
     */
    @Override
    public void fail(String message) {
        Assert.fail(message);
    }

    /**
     * Fails a test with the specified message and exception using TestNG's
     * {@link Assert#fail(String, Throwable)}.
     *
     * @param message The assertion message to display on failure.
     * @param exception The exception to be thrown.
     * @throws AssertionError wrapping the provided exception.
     */
    @Override
    public void fail(String message, Exception exception) {
        Assert.fail(message, exception);
    }

    /**
     * Asserts that the specified boolean condition is false using TestNG's
     * {@link Assert#assertFalse(boolean)}.
     *
     * @param condition The condition to evaluate.
     * @throws AssertionError if the condition is true.
     */
    @Override
    public void assertFalse(boolean condition) {
        Assert.assertFalse(condition);
    }

    /**
     * Asserts that the specified boolean condition is false using TestNG's
     * {@link Assert#assertFalse(boolean, String)}.
     *
     * @param condition The condition to evaluate.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the condition is true.
     */
    @Override
    public void assertFalse(boolean condition, String message) {
        Assert.assertFalse(condition, message);
    }

    /**
     * Asserts that the specified boolean condition is true using TestNG's
     * {@link Assert#assertTrue(boolean)}.
     *
     * @param condition The condition to evaluate.
     * @throws AssertionError if the condition is false.
     */
    @Override
    public void assertTrue(boolean condition) {
        Assert.assertTrue(condition);
    }

    /**
     * Asserts that the specified boolean condition is true using TestNG's
     * {@link Assert#assertTrue(boolean, String)}.
     *
     * @param condition The condition to evaluate.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the condition is false.
     */
    @Override
    public void assertTrue(boolean condition, String message) {
        Assert.assertTrue(condition, message);
    }

    /**
     * Asserts that two booleans are equal using TestNG's {@link Assert#assertEquals(boolean, boolean)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(boolean actual, boolean expected) {
        Assert.assertEquals(actual, expected);
    }

    /**
     * Asserts that two booleans are equal using TestNG's
     * {@link Assert#assertEquals(boolean, boolean, String)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(boolean actual, boolean expected, String message) {
        Assert.assertEquals(actual, expected, message);
    }

    /**
     * Asserts that two integers are equal using TestNG's {@link Assert#assertEquals(int, int)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(int actual, int expected) {
        Assert.assertEquals(actual, expected);
    }

    /**
     * Asserts that two integers are equal using TestNG's {@link Assert#assertEquals(int, int, String)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(int actual, int expected, String message) {
        Assert.assertEquals(actual, expected, message);
    }

    /**
     * Asserts that two longs are equal using TestNG's {@link Assert#assertEquals(long, long)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(long actual, long expected) {
        Assert.assertEquals(actual, expected);
    }

    /**
     * Asserts that two longs are equal using TestNG's {@link Assert#assertEquals(long, long, String)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(long actual, long expected, String message) {
        Assert.assertEquals(actual, expected, message);
    }

    /**
     * Asserts that two floats are equal using TestNG's {@link Assert#assertEquals(float, float)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(float actual, float expected) {
        Assert.assertEquals(actual, expected);
    }

    /**
     * Asserts that two floats are equal using TestNG's
     * {@link Assert#assertEquals(float, float, String)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(float actual, float expected, String message) {
        Assert.assertEquals(actual, expected, message);
    }

    /**
     * Asserts that two doubles are equal using TestNG's {@link Assert#assertEquals(double, double)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(double actual, double expected) {
        Assert.assertEquals(actual, expected);
    }

    /**
     * Asserts that two doubles are equal using TestNG's
     * {@link Assert#assertEquals(double, double, String)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(double actual, double expected, String message) {
        Assert.assertEquals(actual, expected, message);
    }

    /**
     * Asserts that two strings are equal using TestNG's {@link Assert#assertEquals(String, String)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(String actual, String expected) {
        Assert.assertEquals(actual, expected);
    }

    /**
     * Asserts that two strings are equal using TestNG's
     * {@link Assert#assertEquals(String, String, String)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(String actual, String expected, String message) {
        Assert.assertEquals(actual, expected, message);
    }

    /**
     * Asserts that two objects are equal using TestNG's {@link Assert#assertEquals(Object, Object)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(Object actual, Object expected) {
        Assert.assertEquals(actual, expected);
    }

    /**
     * Asserts that two objects are equal using TestNG's
     * {@link Assert#assertEquals(Object, Object, String)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(Object actual, Object expected, String message) {
        Assert.assertEquals(actual, expected, message);
    }

    /**
     * Asserts that executing the provided runnable throws an exception of the given type using TestNG's
     * {@link Assert#assertThrows(Class, Assert.ThrowingRunnable)}.
     *
     * @param expectedThrowable the expected exception type
     * @param runnable the code under test that is expected to throw
     * @throws AssertionError if no exception is thrown or the thrown exception is of a different type
     */
    @Override
    public void assertThrows(Class<? extends Throwable> expectedThrowable, ThrowingRunnable runnable) {
        Assert.assertThrows(expectedThrowable, runnable::run);
    }

    /**
     * Asserts that executing the provided runnable throws an exception of the given type using TestNG's
     * {@link Assert#assertThrows(String, Class, Assert.ThrowingRunnable)}.
     *
     * @param expectedThrowable the expected exception type
     * @param runnable the code under test that is expected to throw
     * @param message The assertion message to display on failure.
     * @throws AssertionError if no exception is thrown or the thrown exception is of a different type
     */
    @Override
    public void assertThrows(Class<? extends Throwable> expectedThrowable, ThrowingRunnable runnable, String message) {
        Assert.assertThrows(message, expectedThrowable, runnable::run);
    }
}
