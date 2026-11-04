package ataf.mobile.utils;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.net.URL;

/**
 * Creates the {@link AndroidDriver} for a new Appium session.
 * <p>
 * The default implementation simply calls {@code new AndroidDriver(appiumServerUrl, options)}. A
 * custom factory can be registered with
 * {@link MobileDriverUtil#setDriverFactory(AndroidDriverFactory)},
 * for example to replace the Appium server with a test double in unit tests.
 * </p>
 */
@FunctionalInterface
public interface AndroidDriverFactory {
    /**
     * Starts a new Android session.
     *
     * @param appiumServerUrl the URL of the Appium server, including an optional base path.
     * @param options the capabilities of the session.
     * @return the driver of the started session.
     */
    AndroidDriver create(URL appiumServerUrl, UiAutomator2Options options);
}
