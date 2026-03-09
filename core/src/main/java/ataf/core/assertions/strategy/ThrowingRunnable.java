package ataf.core.assertions.strategy;

/**
 * Functional interface representing a block of code that may throw a checked or unchecked
 * exception.
 */
@FunctionalInterface
public interface ThrowingRunnable {
    /**
     * Executes the code block.
     *
     * @throws Exception any exception thrown by the executed code
     */
    void run() throws Exception;
}
