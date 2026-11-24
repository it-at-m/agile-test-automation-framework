package ataf.web.utils;

import ataf.web.model.AxeViolation;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for the JSON parsing, impact filtering and report rendering of {@link AxeCoreRunner}.
 * The browser interaction itself is thin enough to be covered by the module's Cucumber scenarios.
 */
public class AxeCoreRunnerTest {

    private static final String SAMPLE = """
            {"violations":[
              {"id":"color-contrast","impact":"serious","help":"Elements must meet color contrast ratio requirements",
               "helpUrl":"https://dequeuniversity.com/rules/axe/4.10/color-contrast",
               "failureSummary":"Fix the contrast","targets":["#login-button"]},
              {"id":"label","impact":"critical","help":"Form elements must have labels",
               "helpUrl":"https://dequeuniversity.com/rules/axe/4.10/label",
               "failureSummary":"Add a label","targets":["#username","#password"]},
              {"id":"meta-viewport","impact":null,"help":"Zooming and scaling must not be disabled",
               "helpUrl":"https://dequeuniversity.com/rules/axe/4.10/meta-viewport",
               "failureSummary":"","targets":["meta[name=viewport]"]}
            ]}""";

    @Test
    public void parsesEveryViolationAndSortsWorstFirst() {
        List<AxeViolation> violations = AxeCoreRunner.parseViolations(SAMPLE);
        Assert.assertEquals(violations.size(), 3);
        Assert.assertEquals(violations.get(0).id(), "label");
        Assert.assertEquals(violations.get(0).impact(), "critical");
        Assert.assertEquals(violations.get(0).targets(), List.of("#username", "#password"));
        Assert.assertEquals(violations.get(1).id(), "color-contrast");
        Assert.assertEquals(violations.get(2).impact(), "minor", "axe reports null impact as undefined; treated as minor");
    }

    @Test
    public void filtersByImpactAtLeast() {
        List<AxeViolation> violations = AxeCoreRunner.parseViolations(SAMPLE);
        Assert.assertEquals(AxeCoreRunner.withImpactAtLeast(violations, "critical").size(), 1);
        Assert.assertEquals(AxeCoreRunner.withImpactAtLeast(violations, "serious").size(), 2);
        Assert.assertEquals(AxeCoreRunner.withImpactAtLeast(violations, "minor").size(), 3);
        Assert.assertEquals(AxeCoreRunner.withImpactAtLeast(violations, "CRITICAL").size(), 1, "impact is case-insensitive");
    }

    @Test
    public void unknownMinImpactKeepsEverythingSoATypoCannotPassSilently() {
        List<AxeViolation> violations = AxeCoreRunner.parseViolations(SAMPLE);
        Assert.assertEquals(AxeCoreRunner.withImpactAtLeast(violations, "critcial").size(), 3);
    }

    @Test
    public void rendersAReadableReport() {
        String report = AxeCoreRunner.formatReport(AxeCoreRunner.parseViolations(SAMPLE));
        Assert.assertTrue(report.contains("axe-core accessibility violations: 3"));
        Assert.assertTrue(report.contains("[critical] label"));
        Assert.assertTrue(report.contains("element: #username"));
        Assert.assertTrue(report.contains("https://dequeuniversity.com/rules/axe/4.10/label"));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void surfacesAxeSideErrorsInsteadOfPassing() {
        AxeCoreRunner.parseViolations("{\"error\":\"axe.run failed\"}");
    }
}
