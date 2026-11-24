package ataf.web.utils;

import ataf.web.model.AxeViolation;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Runs the axe-core accessibility engine against the page that is currently open in the browser.
 * The axe script ships as a classpath resource ({@code axe/axe.min.js}, vendored from axe-core
 * 4.10.3, Mozilla Public License 2.0) and is injected through Selenium, so the check works on any
 * page the suite already navigated to, without a build step or a network download at test time.
 */
public class AxeCoreRunner {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String AXE_RESOURCE = "/axe/axe.min.js";
    private static final List<String> IMPACT_ORDER = List.of("minor", "moderate", "serious", "critical");

    private static volatile String axeScript;

    private final WebDriver driver;

    public AxeCoreRunner(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Analyzes the current page and returns every violation axe found, worst impact first.
     *
     * @param runOnlyTags WCAG tags to restrict the run to (for example {@code wcag2a}); empty means
     *            the axe default rule set
     * @param disabledRules axe rule ids to skip; empty means none
     */
    public List<AxeViolation> analyze(Set<String> runOnlyTags, Set<String> disabledRules) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(loadAxeScript());
        Map<String, Object> options = buildOptions(runOnlyTags, disabledRules);
        Object raw = js.executeAsyncScript(
                "var done = arguments[arguments.length - 1];"
                        + "axe.run(document, arguments[0]).then(function (results) {"
                        + "  done(JSON.stringify({violations: results.violations.map(function (v) {"
                        + "    return {id: v.id, impact: v.impact, help: v.help, helpUrl: v.helpUrl,"
                        + "      failureSummary: (v.nodes.length ? v.nodes[0].failureSummary : ''),"
                        + "      targets: v.nodes.reduce(function (all, n) { return all.concat(n.target); }, [])};"
                        + "  })}));"
                        + "}).catch(function (err) { done(JSON.stringify({error: String(err)})); });",
                options);
        return parseViolations(String.valueOf(raw));
    }

    private static Map<String, Object> buildOptions(Set<String> runOnlyTags, Set<String> disabledRules) {
        Map<String, Object> options = new java.util.LinkedHashMap<>();
        if (runOnlyTags != null && !runOnlyTags.isEmpty()) {
            options.put("runOnly", Map.of("type", "tag", "values", List.copyOf(runOnlyTags)));
        }
        if (disabledRules != null && !disabledRules.isEmpty()) {
            Map<String, Object> rules = new java.util.LinkedHashMap<>();
            disabledRules.forEach(rule -> rules.put(rule, Map.of("enabled", false)));
            options.put("rules", rules);
        }
        return options;
    }

    /**
     * Keeps only the violations whose impact is at least {@code minImpact}. An unknown
     * {@code minImpact} keeps everything, so a typo in a feature file can never silently pass.
     */
    public static List<AxeViolation> withImpactAtLeast(List<AxeViolation> violations, String minImpact) {
        int minimum = IMPACT_ORDER.indexOf(String.valueOf(minImpact).toLowerCase(Locale.ROOT));
        return violations.stream()
                .filter(violation -> IMPACT_ORDER.indexOf(String.valueOf(violation.impact()).toLowerCase(Locale.ROOT)) >= minimum)
                .toList();
    }

    /** Renders the violations as the plain text report that gets attached to the Cucumber report. */
    public static String formatReport(List<AxeViolation> violations) {
        StringBuilder report = new StringBuilder("axe-core accessibility violations: " + violations.size()).append(System.lineSeparator());
        for (AxeViolation violation : violations) {
            report.append(System.lineSeparator())
                    .append("- [").append(violation.impact()).append("] ").append(violation.id()).append(": ").append(violation.help())
                    .append(System.lineSeparator())
                    .append("  help: ").append(violation.helpUrl()).append(System.lineSeparator());
            violation.targets().forEach(target -> report.append("  element: ").append(target).append(System.lineSeparator()));
            if (violation.failureSummary() != null && !violation.failureSummary().isBlank()) {
                report.append("  how to fix: ").append(violation.failureSummary().replace('\n', ' ')).append(System.lineSeparator());
            }
        }
        return report.toString();
    }

    /** Parses the JSON produced by the browser side of {@link #analyze}. */
    static List<AxeViolation> parseViolations(String json) {
        JsonNode root = MAPPER.readTree(json);
        if (root.has("error")) {
            throw new IllegalStateException("axe-core could not analyze the page: " + root.get("error").asText());
        }
        List<AxeViolation> violations = new ArrayList<>();
        for (JsonNode node : root.get("violations")) {
            List<String> targets = new ArrayList<>();
            node.get("targets").forEach(target -> targets.add(target.asText()));
            violations.add(new AxeViolation(
                    node.get("id").asText(),
                    node.path("impact").isMissingNode() || node.get("impact").isNull() ? "minor" : node.get("impact").asText(),
                    node.path("help").isMissingNode() ? "" : node.get("help").asText(),
                    node.path("helpUrl").isMissingNode() ? "" : node.get("helpUrl").asText(),
                    targets,
                    node.path("failureSummary").isMissingNode() ? "" : node.get("failureSummary").asText()));
        }
        violations.sort(Comparator.comparingInt(violation -> -IMPACT_ORDER.indexOf(violation.impact().toLowerCase(Locale.ROOT))));
        return violations;
    }

    private static String loadAxeScript() {
        if (axeScript == null) {
            synchronized (AxeCoreRunner.class) {
                if (axeScript == null) {
                    try (InputStream stream = AxeCoreRunner.class.getResourceAsStream(AXE_RESOURCE)) {
                        if (stream == null) {
                            throw new IllegalStateException("Classpath resource " + AXE_RESOURCE + " is missing");
                        }
                        axeScript = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        throw new UncheckedIOException("Could not read " + AXE_RESOURCE, e);
                    }
                }
            }
        }
        return axeScript;
    }
}
