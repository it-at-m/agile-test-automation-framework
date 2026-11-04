package ataf.mobile.utils;

import ataf.core.logging.ScenarioLogManager;
import ataf.mobile.config.MobileConfig;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.SessionNotCreatedException;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.net.http.HttpConnectTimeoutException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * MobileDriverUtil manages the Appium {@link AndroidDriver} sessions of the ATAF mobile module. It
 * builds the UiAutomator2 capabilities from a {@link MobileConfig}, starts and closes sessions and
 * gives step definitions and page objects access to the session of the current scenario.
 * <p>
 * Sessions are stored per thread, like the WebDriver instances of the web module. Because the
 * proof of concept targets a single physical device, only one session may be active at a time:
 * starting a session while another thread still holds one fails with a clear message instead of
 * letting two sessions fight over the same device.
 * </p>
 * <p>
 * Closing a session never throws, so cleanup cannot hide the original failure of a scenario. A JVM
 * shutdown hook closes sessions left open by aborted or cancelled runs.
 * </p>
 */
public final class MobileDriverUtil {
    private static final AndroidDriverFactory DEFAULT_DRIVER_FACTORY = AndroidDriver::new;
    private static final Map<Long, MobileSession> MOBILE_SESSION_MAP = new ConcurrentHashMap<>();
    private static final AtomicBoolean SHUTDOWN_HOOK_REGISTERED = new AtomicBoolean(false);
    private static final int MAX_CAUSE_DEPTH = 20;
    private static volatile AndroidDriverFactory driverFactory = DEFAULT_DRIVER_FACTORY;

    private MobileDriverUtil() {
    }

    /**
     * Starts a new Android session for the current thread.
     * <p>
     * A session that is still open on the current thread is closed first. If another thread holds an
     * active session, no new session is started.
     * </p>
     *
     * @param config the configuration of the session.
     * @return the driver of the started session.
     * @throws MobileDriverInitializationException if another session is active, or if the Appium
     *             server is not reachable or rejects the session.
     */
    public static synchronized AndroidDriver initDriver(@NotNull MobileConfig config) {
        long threadId = Thread.currentThread().threadId();
        if (MOBILE_SESSION_MAP.containsKey(threadId)) {
            ScenarioLogManager.getLogger().warn("Android session of thread [{}] was still open and is closed before a new one is started.", threadId);
            closeDriver();
        }
        if (!MOBILE_SESSION_MAP.isEmpty()) {
            throw new MobileDriverInitializationException("Another Android session is still active (thread " + MOBILE_SESSION_MAP.keySet()
                    + "). The ATAF mobile module supports one device and one session at a time: run mobile scenarios sequentially with"
                    + " BasicTestNGRunner or BasicJUnitRunner instead of ParallelTestNGRunner.");
        }

        UiAutomator2Options options = createOptions(config);
        ScenarioLogManager.getLogger().info("Starting Android session on Appium server [{}] for {}", config.appiumServerUrl(), describeTarget(config));
        if (config.udid() == null) {
            ScenarioLogManager.getLogger().warn("No device UDID configured ([{}]). Appium uses the only connected device; set the UDID when several"
                    + " devices or emulators are attached.", MobileConfig.externalName(MobileConfig.PROPERTY_UDID));
        }
        ScenarioLogManager.getLogger().debug("Requested capabilities: {}", MobileConfig.maskSensitiveCapabilities(options.asMap()));

        AndroidDriver driver;
        try {
            driver = driverFactory.create(config.appiumServerUrl(), options);
        } catch (RuntimeException e) {
            String message = describeFailure(config, e);
            ScenarioLogManager.getLogger().error(message);
            throw new MobileDriverInitializationException(message, e);
        }
        if (driver == null) {
            throw new MobileDriverInitializationException("The registered AndroidDriverFactory returned no driver for [" + config.appiumServerUrl() + "].");
        }

        MOBILE_SESSION_MAP.put(threadId, new MobileSession(driver, config));
        registerShutdownHook();
        ScenarioLogManager.getLogger().info("Android session [{}] started", driver.getSessionId());
        return driver;
    }

    /**
     * Retrieves the {@link AndroidDriver} of the current thread.
     *
     * @return the driver of the current scenario's session.
     * @throws MobileDriverNotInitializedException if no session was started for the current thread,
     *             usually because the scenario is missing the {@code @mobile} tag.
     */
    public static AndroidDriver getDriver() {
        return getSession().driver();
    }

    /**
     * Retrieves the configuration the session of the current thread was started with.
     *
     * @return the configuration of the current scenario's session.
     * @throws MobileDriverNotInitializedException if no session was started for the current thread.
     */
    public static MobileConfig getConfig() {
        return getSession().config();
    }

    /**
     * Checks whether a session is active for the current thread.
     *
     * @return {@code true} if a session was started and not yet closed on the current thread.
     */
    public static boolean hasDriver() {
        return MOBILE_SESSION_MAP.containsKey(Thread.currentThread().threadId());
    }

    /**
     * Closes the session of the current thread, if there is one, and removes it from the tracking
     * map. Errors while quitting are logged and not rethrown, so the original result of the scenario
     * is preserved.
     */
    public static void closeDriver() {
        MobileSession session = MOBILE_SESSION_MAP.remove(Thread.currentThread().threadId());
        if (session != null) {
            quit(session.driver());
        }
    }

    /**
     * Closes the sessions of all threads. Used by the JVM shutdown hook to free the device after
     * aborted runs.
     */
    public static void closeAllDrivers() {
        for (Long threadId : MOBILE_SESSION_MAP.keySet()) {
            MobileSession session = MOBILE_SESSION_MAP.remove(threadId);
            if (session != null) {
                quit(session.driver());
            }
        }
    }

    /**
     * Builds the UiAutomator2 capabilities for the given configuration. Additional capabilities are
     * applied last and therefore override the values derived from the other properties. Capability
     * names without a vendor prefix get the {@code appium:} prefix.
     *
     * @param config the configuration to convert.
     * @return the capabilities of a new session.
     */
    public static UiAutomator2Options createOptions(@NotNull MobileConfig config) {
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName(config.platformName())
                .setAutomationName(config.automationName())
                .setNewCommandTimeout(config.newCommandTimeout());
        if (config.platformVersion() != null) {
            options.setPlatformVersion(config.platformVersion());
        }
        if (config.udid() != null) {
            options.setUdid(config.udid());
        }
        if (config.appPackage() != null) {
            options.setAppPackage(config.appPackage());
        }
        if (config.appActivity() != null) {
            options.setAppActivity(config.appActivity());
        }
        if (config.app() != null) {
            options.setApp(config.app());
        }
        config.additionalCapabilities().forEach(options::setCapability);
        return options;
    }

    /**
     * Registers the factory used to create drivers for new sessions.
     *
     * @param factory the factory to use, or {@code null} to restore the default, which connects to
     *            the configured Appium server.
     */
    public static void setDriverFactory(@Nullable AndroidDriverFactory factory) {
        driverFactory = factory == null ? DEFAULT_DRIVER_FACTORY : factory;
    }

    private static MobileSession getSession() {
        long threadId = Thread.currentThread().threadId();
        MobileSession session = MOBILE_SESSION_MAP.get(threadId);
        if (session == null) {
            ScenarioLogManager.getLogger().error("No Android driver instance for thread [{}]. Possible missing 'mobile' tag in Cucumber scenario.", threadId);
            throw new MobileDriverNotInitializedException("For thread [" + threadId
                    + "] no Android driver instance was created! Did you forget to add the 'mobile' tag to your Cucumber scenario?");
        }
        return session;
    }

    private static void quit(AndroidDriver driver) {
        try {
            Object sessionId = driver.getSessionId();
            driver.quit();
            ScenarioLogManager.getLogger().info("Android session [{}] closed", sessionId);
        } catch (RuntimeException e) {
            ScenarioLogManager.getLogger().warn("Closing the Android session failed. The Appium server ends it after the new command timeout.", e);
        }
    }

    private static void registerShutdownHook() {
        if (SHUTDOWN_HOOK_REGISTERED.compareAndSet(false, true)) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    closeAllDrivers();
                } catch (RuntimeException ignored) {
                    // The JVM is shutting down; the Appium server ends orphaned sessions after the new command timeout
                }
            }, "ataf-mobile-session-cleanup"));
        }
    }

    private static String describeTarget(MobileConfig config) {
        String device = config.udid() == null ? "the only connected device" : "device [" + config.udid() + "]";
        String app;
        if (config.app() != null) {
            app = "app [" + config.app() + "]";
        } else if (config.appActivity() != null) {
            app = "app [" + config.appPackage() + "/" + config.appActivity() + "]";
        } else {
            app = "app [" + config.appPackage() + "]";
        }
        return device + " and " + app;
    }

    static String describeFailure(MobileConfig config, Throwable failure) {
        String appiumServerUrl = config.appiumServerUrl().toString();
        Throwable networkFailure = findCause(failure, ConnectException.class);
        if (networkFailure == null) {
            networkFailure = findCause(failure, UnknownHostException.class);
        }
        if (networkFailure == null) {
            networkFailure = findCause(failure, NoRouteToHostException.class);
        }
        if (networkFailure == null) {
            networkFailure = findCause(failure, HttpConnectTimeoutException.class);
        }
        if (networkFailure != null) {
            return "Appium server is not reachable at [" + appiumServerUrl
                    + "]. Start it on the test host (for example 'appium --address 127.0.0.1 --port 4723')"
                    + " and check [" + MobileConfig.externalName(MobileConfig.PROPERTY_APPIUM_SERVER_URL) + "]. Appium 2 and 3 serve on the root path"
                    + " unless started with --base-path. Cause: " + firstLine(networkFailure.toString());
        }
        SessionNotCreatedException sessionNotCreated = findCause(failure, SessionNotCreatedException.class);
        if (sessionNotCreated != null) {
            return "Appium could not create an Android session for " + describeTarget(config) + " on [" + appiumServerUrl + "]. Check that the device is"
                    + " listed and authorised by 'adb devices', that [" + MobileConfig.externalName(MobileConfig.PROPERTY_UDID)
                    + "] matches it, that the UiAutomator2 driver is installed ('appium driver list --installed') and that the app package,"
                    + " activity or APK path is correct. Appium reported: " + firstLine(String.valueOf(sessionNotCreated.getMessage()));
        }
        return "Starting an Android session on [" + appiumServerUrl + "] for " + describeTarget(config) + " failed: " + firstLine(failure.toString());
    }

    private static <T extends Throwable> @Nullable T findCause(Throwable failure, Class<T> type) {
        Throwable current = failure;
        for (int depth = 0; current != null && depth < MAX_CAUSE_DEPTH; depth++) {
            if (type.isInstance(current)) {
                return type.cast(current);
            }
            current = current.getCause();
        }
        return null;
    }

    private static String firstLine(String text) {
        int lineBreak = text.indexOf('\n');
        return (lineBreak < 0 ? text : text.substring(0, lineBreak)).trim();
    }

    /**
     * An active Appium session and the configuration it was started with.
     *
     * @param driver the driver of the session.
     * @param config the configuration of the session.
     */
    private record MobileSession(AndroidDriver driver, MobileConfig config) {
    }
}
