package ataf.core.assertions.strategy;

/**
 * An interface defining a common strategy for different assertion frameworks. This allows the
 * selection of assertion methods at runtime.
 */
public interface AssertionStrategy {

    /**
     * Asserts that the specified object is not null.
     *
     * @param object The object to check for nullity.
     * @throws AssertionError if the object is null.
     */
    void assertNotNull(Object object);

    /**
     * Asserts that the specified object is not null.
     *
     * @param object The object to check for nullity.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the object is null.
     */
    void assertNotNull(Object object, String message);

    /**
     * Fails a test with the specified message.
     *
     * @param message The assertion message to display on failure.
     * @throws AssertionError unconditionally.
     */
    void fail(String message);

    /**
     * Fails a test with the specified message and exception.
     *
     * @param message The assertion message to display on failure.
     * @param exception The exception to be thrown.
     * @throws AssertionError wrapping the provided exception.
     */
    void fail(String message, Exception exception);

    /**
     * Asserts that the specified boolean condition is false.
     *
     * @param condition The condition to evaluate.
     * @throws AssertionError if the condition is true.
     */
    void assertFalse(boolean condition);

    /**
     * Asserts that the specified boolean condition is false.
     *
     * @param condition The condition to evaluate.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the condition is true.
     */
    void assertFalse(boolean condition, String message);

    /**
     * Asserts that the specified boolean condition is true.
     *
     * @param condition The condition to evaluate.
     * @throws AssertionError if the condition is false.
     */
    void assertTrue(boolean condition);

    /**
     * Asserts that the specified boolean condition is true.
     *
     * @param condition The condition to evaluate.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the condition is false.
     */
    void assertTrue(boolean condition, String message);

    /**
     * Asserts that two booleans are equal.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    void assertEquals(boolean actual, boolean expected);

    /**
     * Asserts that two booleans are equal.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    void assertEquals(boolean actual, boolean expected, String message);

    /**
     * Asserts that two integers are equal.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    void assertEquals(int actual, int expected);

    /**
     * Asserts that two integers are equal.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    void assertEquals(int actual, int expected, String message);

    /**
     * Asserts that two longs are equal.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    void assertEquals(long actual, long expected);

    /**
     * Asserts that two longs are equal.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    void assertEquals(long actual, long expected, String message);

    /**
     * Asserts that two floats are equal.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    void assertEquals(float actual, float expected);

    /**
     * Asserts that two floats are equal.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    void assertEquals(float actual, float expected, String message);

    /**
     * Asserts that two doubles are equal.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    void assertEquals(double actual, double expected);

    /**
     * Asserts that two doubles are equal.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    void assertEquals(double actual, double expected, String message);

    /**
     * Asserts that two strings are equal.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    void assertEquals(String actual, String expected);

    /**
     * Asserts that two strings are equal.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    void assertEquals(String actual, String expected, String message);

    /**
     * Asserts that two objects are equal.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    void assertEquals(Object actual, Object expected);

    /**
     * Asserts that two objects are equal.
     *
     * @param actual The actual value.
     * @param expected The expected value.
     * @param message The assertion message to display on failure.
     * @throws AssertionError if the actual value is not equal to the expected value.
     */
    void assertEquals(Object actual, Object expected, String message);

    /**
     * Asserts that executing the provided runnable throws an exception of the given type.
     *
     * @param expectedThrowable the expected exception type
     * @param runnable the code under test that is expected to throw
     * @param message the assertion message to display on failure
     * @throws AssertionError if no exception is thrown or the thrown exception is of a different type
     */
    void assertThrows(Class<? extends Throwable> expectedThrowable, ThrowingRunnable runnable, String message);

    /**
     * Asserts that executing the provided runnable throws an exception of the given type.
     *
     * @param expectedThrowable the expected exception type
     * @param runnable the code under test that is expected to throw
     * @throws AssertionError if no exception is thrown or the thrown exception is of a different type
     */
    void assertThrows(Class<? extends Throwable> expectedThrowable, ThrowingRunnable runnable);
}
