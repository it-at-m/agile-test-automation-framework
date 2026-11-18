package example.mobile.apidemos.pages;

import ataf.mobile.pages.MobileBasePage;
import org.openqa.selenium.TimeoutException;

/**
 * The list screens of ApiDemos, such as the start screen, "Views" and "Views/Controls". Every list
 * entry carries its label as content description, so it is located by accessibility id. Entries
 * below the visible part of a list, such as "Views" on the start screen, are scrolled into view.
 */
public class ApiDemosListPage extends MobileBasePage {

    /**
     * Checks whether a list entry is displayed, waiting up to the configured explicit wait timeout.
     *
     * @param label the label of a visible entry, for example {@code App}.
     * @return {@code true} if the entry is displayed.
     */
    public boolean isEntryDisplayed(String label) {
        try {
            waitForElementVisible(accessibilityId(label));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Opens the list entries of a path one after another.
     *
     * @param path the labels of the entries separated by {@code /}, for example
     *            {@code Views/Controls/1. Light Theme}.
     */
    public void navigateTo(String path) {
        for (String label : path.split("/")) {
            scrollToAccessibilityId(label.trim()).click();
        }
    }
}
