package ataf.mobile.utils;

/**
 * Exception thrown when no Android driver instance exists for the current thread.
 * <p>
 * This typically means that the scenario is missing the {@code @mobile} tag, so the mobile hook
 * did not start an Appium session, or that the session was already closed.
 * </p>
 *
 * @see MobileDriverUtil#getDriver()
 */
public class MobileDriverNotInitializedException extends RuntimeException {
    /**
     * Constructs a new MobileDriverNotInitializedException with the specified detail message.
     *
     * @param message the detail message that explains the cause of the exception.
     */
    public MobileDriverNotInitializedException(String message) {
        super(message);
    }
}
