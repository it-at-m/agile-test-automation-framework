package ataf.mobile.utils;

/**
 * Exception thrown when an Android automation session cannot be started.
 * <p>
 * The message describes the most likely cause (Appium server not reachable, session rejected by
 * the server, another session still active) together with hints on how to fix it. The original
 * exception is kept as the cause.
 * </p>
 *
 * @see MobileDriverUtil#initDriver(ataf.mobile.config.MobileConfig)
 */
public class MobileDriverInitializationException extends RuntimeException {
    /**
     * Constructs a new MobileDriverInitializationException with the specified detail message.
     *
     * @param message the detail message that explains the cause of the exception.
     */
    public MobileDriverInitializationException(String message) {
        super(message);
    }

    /**
     * Constructs a new MobileDriverInitializationException with a detail message and a cause.
     *
     * @param message the detail message that explains the cause of the exception.
     * @param cause the exception raised while starting the session.
     */
    public MobileDriverInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
