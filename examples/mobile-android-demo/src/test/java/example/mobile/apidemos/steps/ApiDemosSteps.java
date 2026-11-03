package example.mobile.apidemos.steps;

import ataf.core.assertions.CustomAssertions;
import example.mobile.apidemos.pages.ApiDemosListPage;
import example.mobile.apidemos.pages.ControlsPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions of the ApiDemos example. The Appium session is started and closed by the ATAF
 * mobile hook ({@code ataf.mobile.steps}) for every scenario tagged with {@code @mobile}; the page
 * objects bind to that session.
 */
public class ApiDemosSteps {

    @Given("the ApiDemos app is open")
    public void theApiDemosAppIsOpen() {
        CustomAssertions.assertTrue(new ApiDemosListPage().isEntryDisplayed("Views"), "The start screen of ApiDemos is not displayed");
    }

    @When("the user navigates to {string}")
    public void theUserNavigatesTo(String path) {
        new ApiDemosListPage().navigateTo(path);
    }

    @When("the user enters {string} into the text field")
    public void theUserEntersIntoTheTextField(String text) {
        new ControlsPage().enterText(text);
    }

    @Then("the text field contains {string}")
    public void theTextFieldContains(String expectedText) {
        CustomAssertions.assertEquals(new ControlsPage().getText(), (Object) expectedText, "Unexpected content of the text field");
    }
}
