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
     * Processes the given {@link Throwable} (including subclasses such as {@link Exception} and
     * {@link AssertionError}) and logs the error message together with the full stack trace via
     * {@link ScenarioLogManager}.
     *
     * <p>
     * Previously this class also exposed an {@code Exception}-only overload that logged the
     * message without the stack trace. That same-arity overload was confusing (CodeQL
     * {@code java/confusing-method-signature}) and silently dropped the trace on the
     * {@code Exception} path. Consolidated into this single {@code Throwable} entry point so all
     * paths get full diagnostics.
     * </p>
     *
     * @param t the throwable to be processed
     */
    public static void process(Throwable t) {
        ScenarioLogManager.getLogger()
                .error("Error message: {}", t.getMessage(), t);
    }
}
