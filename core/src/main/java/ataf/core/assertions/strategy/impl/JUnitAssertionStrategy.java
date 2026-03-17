package ataf.core.assertions.strategy.impl;

import ataf.core.assertions.strategy.AssertionStrategy;
import ataf.core.assertions.strategy.ThrowingRunnable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

/**
 * A concrete implementation of {@link AssertionStrategy} using JUnit 5's assertion methods.
 * <p>
 * This class adapts the JUnit 5 assertion library to the common {@link AssertionStrategy}
 * interface, enabling unified assertion handling. Each method
 * corresponds to a specific assertion in {@link Assertions}.
 * </p>
 */
public class JUnitAssertionStrategy implements AssertionStrategy {

    /**
     * Asserts that the specified object is not null using JUnit 5's
     * {@link Assertions#assertNotNull(Object)}.
     *
     * @param object The object to check for nullity.
     * @throws AssertionError if the object is null.
     */
    @Override
    public void assertNotNull(Object object) {
        Assertions.assertNotNull(object);
    }

    /**
     * Asserts that the specified object is not null using JUnit 5's
     * {@link Assertions#assertNotNull(Object, String)}.
     *
     * @param object The object to check for nullity.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the object is null.
     */
    @Override
    public void assertNotNull(Object object, String message) {
        Assertions.assertNotNull(object, message);
    }

    /**
     * Fails a test with the specified message using JUnit 5's {@link Assertions#fail(String)}.
     *
     * @param message The assertion message to display on failure.
     * @throws AssertionError unconditionally.
     */
    @Override
    public void fail(String message) {
        Assertions.fail(message);
    }

    /**
     * Fails a test with the specified message and exception using JUnit 5's
     * {@link Assertions#fail(String, Throwable)}.
     *
     * @param message The assertion message to display on failure.
     * @param exception The exception to be thrown.
     * @throws AssertionError wrapping the provided exception.
     */
    @Override
    public void fail(String message, Exception exception) {
        Assertions.fail(message, exception);
    }

    /**
     * Asserts that the specified boolean condition is false using JUnit 5's
     * {@link Assertions#assertFalse(boolean)}.
     *
     * @param condition The condition to evaluate.
     * @throws AssertionError if the condition is true.
     */
    @Override
    public void assertFalse(boolean condition) {
        Assertions.assertFalse(condition);
    }

    /**
     * Asserts that the specified boolean condition is false using JUnit 5's
     * {@link Assertions#assertFalse(boolean, String)}.
     *
     * @param condition The condition to evaluate.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the condition is true.
     */
    @Override
    public void assertFalse(boolean condition, String message) {
        Assertions.assertFalse(condition, message);
    }

    /**
     * Asserts that the specified boolean condition is true using JUnit 5's
     * {@link Assertions#assertTrue(boolean)}.
     *
     * @param condition The condition to evaluate.
     * @throws AssertionError if the condition is false.
     */
    @Override
    public void assertTrue(boolean condition) {
        Assertions.assertTrue(condition);
    }

    /**
     * Asserts that the specified boolean condition is true using JUnit 5's
     * {@link Assertions#assertTrue(boolean, String)}.
     *
     * @param condition The condition to evaluate.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the condition is false.
     */
    @Override
    public void assertTrue(boolean condition, String message) {
        Assertions.assertTrue(condition, message);
    }

    /**
     * Asserts that two booleans are equal using JUnit 5's
     * {@link Assertions#assertEquals(Object, Object)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(boolean actual, boolean expected) {
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Asserts that two booleans are equal using JUnit 5's
     * {@link Assertions#assertEquals(Object, Object, String)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(boolean actual, boolean expected, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    /**
     * Asserts that two integers are equal using JUnit 5's
     * {@link Assertions#assertEquals(int, int)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(int actual, int expected) {
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Asserts that two integers are equal using JUnit 5's
     * {@link Assertions#assertEquals(int, int, String)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(int actual, int expected, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    /**
     * Asserts that two longs are equal using JUnit 5's
     * {@link Assertions#assertEquals(long, long)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(long actual, long expected) {
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Asserts that two longs are equal using JUnit 5's
     * {@link Assertions#assertEquals(long, long, String)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(long actual, long expected, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    /**
     * Asserts that two floats are equal using JUnit 5's
     * {@link Assertions#assertEquals(float, float)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(float actual, float expected) {
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Asserts that two floats are equal using JUnit 5's
     * {@link Assertions#assertEquals(float, float, String)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(float actual, float expected, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    /**
     * Asserts that two doubles are equal using JUnit 5's
     * {@link Assertions#assertEquals(double, double)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(double actual, double expected) {
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Asserts that two doubles are equal using JUnit 5's
     * {@link Assertions#assertEquals(double, double, String)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(double actual, double expected, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    /**
     * Asserts that two strings are equal using JUnit 5's
     * {@link Assertions#assertEquals(Object, Object)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(String actual, String expected) {
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Asserts that two strings are equal using JUnit 5's
     * {@link Assertions#assertEquals(Object, Object, String)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(String actual, String expected, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    /**
     * Asserts that two objects are equal using JUnit 5's
     * {@link Assertions#assertEquals(Object, Object)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(Object actual, Object expected) {
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Asserts that two objects are equal using JUnit 5's
     * {@link Assertions#assertEquals(Object, Object, String)}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    @Override
    public void assertEquals(Object actual, Object expected, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    /**
     * Asserts that executing the provided runnable throws an exception of the given type using JUnit
     * 5's
     * {@link Assertions#assertThrows(Class, Executable, String)}.
     *
     * @param expectedThrowable the expected exception type
     * @param runnable the code under test that is expected to throw
     * @throws AssertionError if no exception is thrown or the thrown exception is of a different type
     */
    @Override
    public void assertThrows(Class<? extends Throwable> expectedThrowable, ThrowingRunnable runnable) {
        Assertions.assertThrows(expectedThrowable, runnable::run);
    }

    /**
     * Asserts that executing the provided runnable throws an exception of the given type using JUnit
     * 5's
     * {@link Assertions#assertThrows(Class, Executable)}.
     *
     * @param expectedThrowable the expected exception type
     * @param runnable the code under test that is expected to throw
     * @param message The assertion message to display on failure.
     * @throws AssertionError if no exception is thrown or the thrown exception is of a different type
     */
    @Override
    public void assertThrows(Class<? extends Throwable> expectedThrowable, ThrowingRunnable runnable, String message) {
        Assertions.assertThrows(expectedThrowable, runnable::run, message);
    }
}
