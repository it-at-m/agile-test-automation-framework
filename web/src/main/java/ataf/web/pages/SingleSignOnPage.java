package ataf.web.pages;

import ataf.core.logging.ScenarioLogManager;
import ataf.core.properties.DefaultValues;
import ataf.core.properties.TestProperties;
import ataf.web.model.LocatorType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Page Object representing a Single Sign-On (SSO) login page.
 *
 * <div>
 * This class provides reusable functionality to perform login operations on
 * SSO-based authentication pages. It encapsulates the interaction with the
 * username field, password field, and login button using the Page Object
 * Pattern.
 * </div>
 *
 * <div>
 * In Chrome browsers an optional optimization is used: the credentials are
 * injected into the current URL (Basic Authentication style) in order to
 * trigger automatic authentication.
 * </div>
 *
 * <div>
 * The class is designed to work with different SSO environments by using a
 * configurable URL regular expression that identifies the SSO login page.
 * </div>
 *
 * <p>
 * <b>Example URL regexp:</b>
 * </p>
 *
 * <pre>
 * {@code
 * sso(dev|test)\.muenchen\.de
 * }
 * </pre>
 *
 * <p>
 * <b>Usage example:</b>
 * </p>
 *
 * <pre>
 * {@code
 * SingleSignOnPage ssoPage = new SingleSignOnPage(
 *         driver,
 *         "username",
 *         LocatorType.ID,
 *         "password",
 *         LocatorType.ID,
 *         "kc-login",
 *         LocatorType.ID);
 *
 * ssoPage.executeSingleSignOnLogin(
 *         "myUser",
 *         "myPassword",
 *         "sso(dev|test)\\.muenchen\\.de");
 * }
 * </pre>
 */
public class SingleSignOnPage extends BasePage {
    private final String userNameFieldLocator;
    private final LocatorType userNameFieldLocatorType;
    private final String passwordFieldLocator;
    private final LocatorType passwordFieldLocatorType;
    private final String loginButtonLocator;
    private final LocatorType loginButtonLocatorType;

    /**
     * Constructs a new {@link SingleSignOnPage}.
     *
     * @param driver the {@link RemoteWebDriver} instance used to interact with the browser
     * @param userNameFieldLocator the locator value used to identify the username input field
     * @param userNameFieldLocatorType the {@link LocatorType} used to locate the username field
     * @param passwordFieldLocator the locator value used to identify the password input field
     * @param passwordFieldLocatorType the {@link LocatorType} used to locate the password field
     * @param loginButtonLocator the locator value used to identify the login button
     * @param loginButtonLocatorType the {@link LocatorType} used to locate the login button
     */
    public SingleSignOnPage(RemoteWebDriver driver, String userNameFieldLocator, LocatorType userNameFieldLocatorType, String passwordFieldLocator,
            LocatorType passwordFieldLocatorType, String loginButtonLocator, LocatorType loginButtonLocatorType) {
        super(driver);
        this.userNameFieldLocator = userNameFieldLocator;
        this.userNameFieldLocatorType = userNameFieldLocatorType;
        this.passwordFieldLocator = passwordFieldLocator;
        this.passwordFieldLocatorType = passwordFieldLocatorType;
        this.loginButtonLocator = loginButtonLocator;
        this.loginButtonLocatorType = loginButtonLocatorType;
    }

    /**
     * Executes the Single Sign-On login process.
     *
     * <p>
     * The method performs the following steps:
     * </p>
     *
     * <ol>
     * <li>If the configured browser is Chrome, the method waits until the current
     * URL matches the provided {@code urlRegexp}.</li>
     * <li>The credentials are injected into the URL to trigger automatic
     * authentication.</li>
     * <li>The username and password are entered into the corresponding fields.</li>
     * <li>The login button is clicked.</li>
     * </ol>
     *
     * <p>
     * The URL regular expression allows the method to detect when the browser
     * has reached the SSO login page.
     * </p>
     *
     * <p>
     * <b>Example:</b>
     * </p>
     *
     * <pre>
     * {@code
     * sso(dev|test)\.muenchen\.de
     * }
     * </pre>
     *
     * @param userName the username used for authentication
     * @param password the password used for authentication
     * @param urlRegexp a regular expression that matches the SSO login URL
     *            (e.g. {@code sso(dev|test)\\.muenchen\\.de})
     */
    public void executeSingleSignOnLogin(String userName, String password, String urlRegexp) {
        // NOTE: This block is intentionally used as a workaround for Chrome incognito mode,
        // where browser credential popups cannot be automated via Selenium WebDriver.
        // Credentials are embedded in the URL, which may appear in browser history, grid logs,
        // and proxy logs. This approach is NOT intended for production automation environments.
        if ("chrome".equalsIgnoreCase(
                TestProperties.getProperty("browser", true, DefaultValues.BROWSER).orElse(DefaultValues.BROWSER))) {
            WebDriverWait wait = new WebDriverWait(DRIVER, Duration.ofSeconds(DEFAULT_EXPLICIT_WAIT_TIME));
            wait.until(ExpectedConditions.urlMatches(urlRegexp));
            DRIVER.navigate().to(DRIVER.getCurrentUrl().replaceFirst("https://",
                    "https://" + URLEncoder.encode(userName, StandardCharsets.UTF_8) + ":" + URLEncoder.encode(password, StandardCharsets.UTF_8) + "@"));
        }

        ScenarioLogManager.getLogger().info("Trying to enter user name...");
        enterTextInWebElement(DEFAULT_EXPLICIT_WAIT_TIME, userName, userNameFieldLocator, userNameFieldLocatorType);

        ScenarioLogManager.getLogger().info("Trying to enter password...");
        enterTextInWebElement(DEFAULT_EXPLICIT_WAIT_TIME, password, passwordFieldLocator, passwordFieldLocatorType);

        ScenarioLogManager.getLogger().info("Trying to click on \"Login\" button...");
        clickOnWebElement(DEFAULT_EXPLICIT_WAIT_TIME, loginButtonLocator, loginButtonLocatorType, false);
    }
}
