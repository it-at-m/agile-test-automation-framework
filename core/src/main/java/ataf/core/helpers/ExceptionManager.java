package ataf.core.helpers;

import ataf.core.logging.ScenarioLogManager;

/**
 * This class handles exception processing and logging.
 */
public class ExceptionManager {
    /**
     * Default constructor.
     */
    public ExceptionManager() {
        // Implementation will follow later
    }

    /**
     * Processes the given exception and logs the error message.
     *
     * @param e The exception to be processed
     */
    public static void process(Exception e) {
        ScenarioLogManager.getLogger().error("Error message: {}", e.getMessage());
    }

    /**
     * Processes the given {@link Throwable} and logs the error message including the stack trace.
     *
     * <p>
     * This overload allows handling of errors that are not subclasses of {@link Exception},
     * such as {@link AssertionError}, while preserving the existing {@link #process(Exception)}
     * method for backward compatibility.
     * </p>
     *
     * @param t the throwable to be processed
     */
    public static void process(Throwable t) {
        ScenarioLogManager.getLogger()
                .error("Error message: {}", t.getMessage(), t);
    }
}
