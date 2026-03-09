package ataf.core.assertions;

import ataf.core.assertions.strategy.AssertionStrategy;
import ataf.core.assertions.strategy.ThrowingRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * A wrapper class for dynamic assertion methods that adapts to the underlying assertion framework.
 * <p>
 * This class provides static methods that delegate to the currently set {@link AssertionStrategy}.
 * Set the strategy at runtime to switch between different
 * assertion frameworks.
 * </p>
 */
public class CustomAssertions {
    private static AssertionStrategy strategy;

    private static AssertionStrategy getStrategy() {
        if (strategy == null) {
            throw new IllegalStateException(
                    "Assertion strategy has not been initialized! Example usage: CustomAssertions.setStrategy(new TestNGAssertionStrategy())");
        }
        return strategy;
    }

    /**
     * Sets the current {@link AssertionStrategy} to use for assertions.
     *
     * @param strategy The strategy to use for assertions.
     */
    public static void setStrategy(@NotNull AssertionStrategy strategy) {
        CustomAssertions.strategy = strategy;
    }

    /**
     * Asserts that the specified object is not null using the currently active
     * {@link AssertionStrategy}.
     *
     * @param object The object to check for nullity.
     * @throws AssertionError if the object is null.
     */
    public static void assertNotNull(Object object) {
        getStrategy().assertNotNull(object);
    }

    /**
     * Asserts that the specified object is not null using the currently active
     * {@link AssertionStrategy}.
     *
     * @param object The object to check for nullity.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the object is null.
     */
    public static void assertNotNull(Object object, String message) {
        getStrategy().assertNotNull(object, message);
    }

    /**
     * Fails a test with the specified message using the currently active {@link AssertionStrategy}.
     *
     * @param message The assertion message to display on failure.
     * @throws AssertionError unconditionally.
     */
    public static void fail(String message) {
        getStrategy().fail(message);
    }

    /**
     * Fails a test with the specified message and exception using the currently active
     * {@link AssertionStrategy}.
     *
     * @param message The assertion message to display on failure.
     * @param exception The exception to be thrown.
     * @throws AssertionError wrapping the provided exception.
     */
    public static void fail(String message, Exception exception) {
        getStrategy().fail(message, exception);
    }

    /**
     * Asserts that the specified boolean condition is false using the currently active
     * {@link AssertionStrategy}.
     *
     * @param condition The condition to evaluate.
     * @throws AssertionError if the condition is true.
     */
    public static void assertFalse(boolean condition) {
        getStrategy().assertFalse(condition);
    }

    /**
     * Asserts that the specified boolean condition is false using the currently active
     * {@link AssertionStrategy}.
     *
     * @param condition The condition to evaluate.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the condition is true.
     */
    public static void assertFalse(boolean condition, String message) {
        getStrategy().assertFalse(condition, message);
    }

    /**
     * Asserts that the specified boolean condition is true using the currently active
     * {@link AssertionStrategy}.
     *
     * @param condition The condition to evaluate.
     * @throws AssertionError if the condition is false.
     */
    public static void assertTrue(boolean condition) {
        getStrategy().assertTrue(condition);
    }

    /**
     * Asserts that the specified boolean condition is true using the currently active
     * {@link AssertionStrategy}.
     *
     * @param condition The condition to evaluate.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the condition is false.
     */
    public static void assertTrue(boolean condition, String message) {
        getStrategy().assertTrue(condition, message);
    }

    /**
     * Asserts that two booleans are equal using the currently active {@link AssertionStrategy}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    public static void assertEquals(boolean actual, boolean expected) {
        getStrategy().assertEquals(actual, expected);
    }

    /**
     * Asserts that two booleans are equal using the currently active {@link AssertionStrategy}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    public static void assertEquals(boolean actual, boolean expected, String message) {
        getStrategy().assertEquals(actual, expected, message);
    }

    /**
     * Asserts that two integers are equal using the currently active {@link AssertionStrategy}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    public static void assertEquals(int actual, int expected) {
        getStrategy().assertEquals(actual, expected);
    }

    /**
     * Asserts that two integers are equal using the currently active {@link AssertionStrategy}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    public static void assertEquals(int actual, int expected, String message) {
        getStrategy().assertEquals(actual, expected, message);
    }

    /**
     * Asserts that two longs are equal using the currently active {@link AssertionStrategy}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    public static void assertEquals(long actual, long expected) {
        getStrategy().assertEquals(actual, expected);
    }

    /**
     * Asserts that two longs are equal using the currently active {@link AssertionStrategy}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    public static void assertEquals(long actual, long expected, String message) {
        getStrategy().assertEquals(actual, expected, message);
    }

    /**
     * Asserts that two floats are equal using the currently active {@link AssertionStrategy}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    public static void assertEquals(float actual, float expected) {
        getStrategy().assertEquals(actual, expected);
    }

    /**
     * Asserts that two floats are equal using the currently active {@link AssertionStrategy}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    public static void assertEquals(float actual, float expected, String message) {
        getStrategy().assertEquals(actual, expected, message);
    }

    /**
     * Asserts that two doubles are equal using the currently active {@link AssertionStrategy}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    public static void assertEquals(double actual, double expected) {
        getStrategy().assertEquals(actual, expected);
    }

    /**
     * Asserts that two doubles are equal using the currently active {@link AssertionStrategy}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    public static void assertEquals(double actual, double expected, String message) {
        getStrategy().assertEquals(actual, expected, message);
    }

    /**
     * Asserts that two strings are equal using the currently active {@link AssertionStrategy}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    public static void assertEquals(String actual, String expected) {
        getStrategy().assertEquals(actual, expected);
    }

    /**
     * Asserts that two strings are equal using the currently active {@link AssertionStrategy}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    public static void assertEquals(String actual, String expected, String message) {
        getStrategy().assertEquals(actual, expected, message);
    }

    /**
     * Asserts that two objects are equal using the currently active {@link AssertionStrategy}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    public static void assertEquals(Object actual, Object expected) {
        getStrategy().assertEquals(actual, expected);
    }

    /**
     * Asserts that two objects are equal using the currently active {@link AssertionStrategy}.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    public static void assertEquals(Object actual, Object expected, String message) {
        getStrategy().assertEquals(actual, expected, message);
    }

    /**
     * Asserts that executing the provided runnable throws an exception of the given type using the
     * currently active {@link AssertionStrategy}.
     *
     * @param message the assertion message to display on failure (may be {@code null})
     * @param expectedThrowable the expected exception type
     * @param runnable the code under test that is expected to throw
     * @throws AssertionError if no exception is thrown or the thrown exception is of a different type
     */
    public static void assertThrows(String message,
            Class<? extends Throwable> expectedThrowable,
            ThrowingRunnable runnable) {
        getStrategy().assertThrows(expectedThrowable, runnable, message);
    }

    /**
     * Asserts that executing the provided runnable throws an exception of the given type using the
     * currently active {@link AssertionStrategy}.
     *
     * @param expectedThrowable the expected exception type
     * @param runnable the code under test that is expected to throw
     * @throws AssertionError if no exception is thrown or the thrown exception is of a different type
     */
    public static void assertThrows(Class<? extends Throwable> expectedThrowable,
            ThrowingRunnable runnable) {
        getStrategy().assertThrows(expectedThrowable, runnable, null);
    }
}
