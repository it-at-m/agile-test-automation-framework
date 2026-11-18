package ataf.mobile.config;

import java.util.List;

/**
 * Thrown when the {@code testautomation.mobile.*} configuration is incomplete or invalid.
 * <p>
 * All problems found while reading the configuration are collected and reported together, so a
 * single test run shows every property that needs to be fixed.
 * </p>
 */
public class MobileConfigException extends RuntimeException {
    /**
     * The individual configuration problems, each naming the affected property.
     */
    private final List<String> problems;

    /**
     * Constructs a new MobileConfigException listing all configuration problems.
     *
     * @param problems the human-readable descriptions of all configuration problems, each naming the
     *            affected property.
     */
    public MobileConfigException(List<String> problems) {
        super(buildMessage(problems));
        this.problems = List.copyOf(problems);
    }

    /**
     * Returns the individual configuration problems.
     *
     * @return an unmodifiable list of problem descriptions.
     */
    public List<String> getProblems() {
        return problems;
    }

    private static String buildMessage(List<String> problems) {
        StringBuilder message = new StringBuilder(
                "Invalid mobile configuration. Fix the following properties in testautomation.properties or pass them as -D system properties:");
        for (String problem : problems) {
            message.append(System.lineSeparator()).append(" - ").append(problem);
        }
        return message.toString();
    }
}
