package ataf.web.steps;

import ataf.core.context.ScenarioContext;
import ataf.core.logging.ScenarioLogManager;
import ataf.core.properties.TestProperties;
import ataf.web.model.AxeViolation;
import ataf.web.utils.AxeCoreRunner;
import ataf.web.utils.DriverUtil;
import io.cucumber.java.de.Dann;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Accessibility steps: run the axe-core engine on the page that is currently open and fail the
 * scenario when violations at or above a configured impact are found. The violation summary is
 * attached to the Cucumber report either way, so passing runs document what was checked.
 *
 * <p>
 * Configuration via {@code testautomation.a11y.*} properties:
 * <ul>
 * <li>{@code testautomation.a11y.enabled} (default {@code true}): set to {@code false} to skip
 * the check, for example while a known violation is being fixed.</li>
 * <li>{@code testautomation.a11y.tags}: comma separated WCAG tags to restrict the run to, for
 * example {@code wcag2a,wcag2aa}. Empty uses the axe default rule set.</li>
 * <li>{@code testautomation.a11y.disabled.rules}: comma separated axe rule ids to skip.</li>
 * </ul>
 */
public class A11ySteps {

    /***
     * Runs axe-core on the current page and fails when violations with at least the given impact
     * (minor &lt; moderate &lt; serious &lt; critical) exist. The full summary is attached to the
     * Cucumber report as {@code axe-violations.txt}.
     *
     * @param minImpact the lowest impact that fails the scenario, for example "critical"
     */
    @Dann("sollte die Seite keine Accessibility-Verletzungen mit Impact {string} haben.")
    public void thePageShouldHaveNoAccessibilityViolationsWithImpact(String minImpact) {
        boolean enabled = TestProperties.getProperty("testautomation.a11y.enabled", false, true).orElse(true);
        if (!enabled) {
            ScenarioLogManager.getLogger().info("Accessibility check skipped (testautomation.a11y.enabled=false).");
            return;
        }
        Set<String> tags = csvProperty("testautomation.a11y.tags");
        Set<String> disabledRules = csvProperty("testautomation.a11y.disabled.rules");

        List<AxeViolation> all = new AxeCoreRunner(DriverUtil.getDriver()).analyze(tags, disabledRules);
        List<AxeViolation> failing = AxeCoreRunner.withImpactAtLeast(all, minImpact);

        String report = AxeCoreRunner.formatReport(all);
        ScenarioContext.get().attach(report.getBytes(StandardCharsets.UTF_8), "text/plain", "axe-violations.txt");
        ScenarioLogManager.getLogger()
                .info("axe-core found {} violation(s), {} at or above impact \"{}\".", all.size(), failing.size(), minImpact);

        if (!failing.isEmpty()) {
            throw new AssertionError(
                    "Expected no accessibility violations with impact at least \"" + minImpact.toLowerCase(Locale.ROOT)
                            + "\", but axe-core found " + failing.size() + " of " + all.size() + " total. "
                            + "See the attached axe-violations.txt for details. First violation: " + failing.get(0).id()
                            + " (" + failing.get(0).impact() + ") on " + String.join(", ", failing.get(0).targets()));
        }
    }

    private static Set<String> csvProperty(String name) {
        return TestProperties.getProperty(name, false, "")
                .map(String::valueOf)
                .stream()
                .flatMap(value -> Arrays.stream(value.split(",")))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
    }
}
