package ataf.mobile.testsupport.glue;

import ataf.mobile.utils.MobileDriverUtil;
import io.cucumber.java.en.Then;
import org.testng.Assert;

/**
 * Step definitions for the in-process Cucumber run of {@code MobileHookCucumberTest}. They only
 * inspect the session state managed by the mobile hook.
 */
public class LifecycleSteps {

    @Then("an Android session is active")
    public void anAndroidSessionIsActive() {
        Assert.assertTrue(MobileDriverUtil.hasDriver(), "The @mobile hook should have started an Android session");
        Assert.assertNotNull(MobileDriverUtil.getDriver());
    }

    @Then("no Android session is active")
    public void noAndroidSessionIsActive() {
        Assert.assertFalse(MobileDriverUtil.hasDriver(), "Scenarios without @mobile must not start an Android session");
    }

    @Then("the scenario fails on purpose")
    public void theScenarioFailsOnPurpose() {
        throw new AssertionError("Intended failure to verify the reporting of failed mobile scenarios");
    }
}
