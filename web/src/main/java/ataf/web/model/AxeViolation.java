package ataf.web.model;

import java.util.List;

/**
 * One axe-core violation, reduced to what a failing scenario needs to tell the tester.
 *
 * @param id the axe rule id, for example "color-contrast"
 * @param impact the worst impact across the affected nodes: minor, moderate, serious or critical
 * @param help a short human readable description of the rule
 * @param helpUrl the Deque University page explaining the rule
 * @param targets the CSS selectors of the affected elements
 * @param failureSummary the repair hints axe attached to the first affected node
 */
public record AxeViolation(String id, String impact, String help, String helpUrl, List<String> targets, String failureSummary) {
}
