package example.mobile.apidemos.pages;

import ataf.mobile.pages.MobileBasePage;
import org.openqa.selenium.By;

/**
 * The "Views/Controls/1. Light Theme" screen of ApiDemos with its text field.
 */
public class ControlsPage extends MobileBasePage {
    private static final By TEXT_FIELD = resourceId("io.appium.android.apis:id/edit");

    /**
     * Replaces the content of the text field.
     *
     * @param text the text to enter.
     */
    public void enterText(String text) {
        enterTextInElement(TEXT_FIELD, text);
    }

    /**
     * Returns the content of the text field.
     *
     * @return the current text.
     */
    public String getText() {
        return getElementText(TEXT_FIELD);
    }
}
