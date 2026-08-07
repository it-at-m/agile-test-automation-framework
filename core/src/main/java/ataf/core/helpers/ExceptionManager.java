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
     * Processes the given exception and logs the error message with the full stack trace.
     *
     * <p>
     * Kept as a deprecated binary-compatibility bridge for callers compiled against older ATAF
     * versions that invoke {@code process(Exception)} by exact signature. New code should call
     * {@link #process(Throwable)} directly.
     * </p>
     *
     * @param e The exception to be processed
     * @deprecated Use {@link #process(Throwable)} instead.
     */
    @Deprecated(forRemoval = true)
    public static void process(Exception e) {
        process((Throwable) e);
    }

    /**
     * Processes the given {@link Throwable} (including subclasses such as {@link Exception} and
     * {@link AssertionError}) and logs the error message together with the full stack trace via
     * {@link ScenarioLogManager}.
     *
     * @param t the throwable to be processed
     */
    public static void process(Throwable t) {
        ScenarioLogManager.getLogger()
                .error("Error message: {}", t.getMessage(), t);
    }
}
