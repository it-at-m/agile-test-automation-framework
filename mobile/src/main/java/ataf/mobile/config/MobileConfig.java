package ataf.mobile.config;

import ataf.core.properties.TestProperties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Immutable configuration of an Android session driven by Appium and the UiAutomator2 driver.
 * <p>
 * The values are read from {@link TestProperties}, so they can be defined in
 * {@code testautomation.properties} and overridden with JVM system properties
 * ({@code -Dtestautomation.mobile.udid=...}). Property files use the prefix
 * {@code testautomation.mobile.}; {@link TestProperties} stores them without the
 * {@code testautomation.} part, which is why the property name constants of this class start with
 * {@code mobile.}.
 * </p>
 * <p>
 * Blank values are treated as unset, so empty CI variables passed through {@code -D} fall back to
 * the defaults. All problems are collected and reported together in a
 * {@link MobileConfigException}.
 * </p>
 *
 * @param appiumServerUrl the full URL of the Appium server, including an optional base path.
 * @param platformName the platform name, always {@code Android} in this version.
 * @param platformVersion the expected Android version of the device, or {@code null} for any.
 * @param automationName the Appium automation driver, {@code UiAutomator2} by default.
 * @param udid the serial of the device as listed by {@code adb devices}, or {@code null} to let
 *            Appium pick the only connected device.
 * @param appPackage the package of the app under test, or {@code null} if only {@code app} is
 *            given.
 * @param appActivity the activity to launch, or {@code null} to let Appium determine it.
 * @param app the absolute path or http(s) URL of an APK to install, or {@code null} to use the
 *            installed app.
 * @param newCommandTimeout how long the Appium server keeps an idle session alive.
 * @param explicitWaitTimeout the default timeout of explicit waits in page objects.
 * @param screenshotOnFailure whether a screenshot is attached to failed scenarios.
 * @param pageSourceOnFailure whether the UI hierarchy (XML page source) is attached to failed
 *            scenarios.
 * @param additionalCapabilities further Appium capabilities, applied after all other values.
 */
public record MobileConfig(
        @NotNull URL appiumServerUrl,
        @NotNull String platformName,
        @Nullable String platformVersion,
        @NotNull String automationName,
        @Nullable String udid,
        @Nullable String appPackage,
        @Nullable String appActivity,
        @Nullable String app,
        @NotNull Duration newCommandTimeout,
        @NotNull Duration explicitWaitTimeout,
        boolean screenshotOnFailure,
        boolean pageSourceOnFailure,
        @NotNull Map<String, Object> additionalCapabilities) {

    /**
     * Property name of the Appium server URL ({@code testautomation.mobile.appiumServerUrl}).
     */
    public static final String PROPERTY_APPIUM_SERVER_URL = "mobile.appiumServerUrl";

    /**
     * Property name of the platform name ({@code testautomation.mobile.platformName}).
     */
    public static final String PROPERTY_PLATFORM_NAME = "mobile.platformName";

    /**
     * Property name of the platform version ({@code testautomation.mobile.platformVersion}).
     */
    public static final String PROPERTY_PLATFORM_VERSION = "mobile.platformVersion";

    /**
     * Property name of the automation driver ({@code testautomation.mobile.automationName}).
     */
    public static final String PROPERTY_AUTOMATION_NAME = "mobile.automationName";

    /**
     * Property name of the device serial ({@code testautomation.mobile.udid}).
     */
    public static final String PROPERTY_UDID = "mobile.udid";

    /**
     * Property name of the app package ({@code testautomation.mobile.appPackage}).
     */
    public static final String PROPERTY_APP_PACKAGE = "mobile.appPackage";

    /**
     * Property name of the app activity ({@code testautomation.mobile.appActivity}).
     */
    public static final String PROPERTY_APP_ACTIVITY = "mobile.appActivity";

    /**
     * Property name of the APK path or URL ({@code testautomation.mobile.app}).
     */
    public static final String PROPERTY_APP = "mobile.app";

    /**
     * Property name of the session idle timeout in seconds
     * ({@code testautomation.mobile.newCommandTimeoutSeconds}).
     */
    public static final String PROPERTY_NEW_COMMAND_TIMEOUT_SECONDS = "mobile.newCommandTimeoutSeconds";

    /**
     * Property name of the explicit wait timeout in seconds
     * ({@code testautomation.mobile.explicitWaitSeconds}).
     */
    public static final String PROPERTY_EXPLICIT_WAIT_SECONDS = "mobile.explicitWaitSeconds";

    /**
     * Property name of the screenshot-on-failure switch
     * ({@code testautomation.mobile.screenshotOnFailure}).
     */
    public static final String PROPERTY_SCREENSHOT_ON_FAILURE = "mobile.screenshotOnFailure";

    /**
     * Property name of the page-source-on-failure switch
     * ({@code testautomation.mobile.pageSourceOnFailure}).
     */
    public static final String PROPERTY_PAGE_SOURCE_ON_FAILURE = "mobile.pageSourceOnFailure";

    /**
     * Property name of the additional capabilities as a JSON object
     * ({@code testautomation.mobile.additionalCapabilities}).
     */
    public static final String PROPERTY_ADDITIONAL_CAPABILITIES = "mobile.additionalCapabilities";

    /**
     * Default Appium server URL. Appium 2 and 3 serve on the root path unless started with
     * {@code --base-path}.
     */
    public static final String DEFAULT_APPIUM_SERVER_URL = "http://127.0.0.1:4723";

    /**
     * Default and only supported platform name.
     */
    public static final String DEFAULT_PLATFORM_NAME = "Android";

    /**
     * Default automation driver.
     */
    public static final String DEFAULT_AUTOMATION_NAME = "UiAutomator2";

    /**
     * Default session idle timeout in seconds.
     */
    public static final long DEFAULT_NEW_COMMAND_TIMEOUT_SECONDS = 120L;

    /**
     * Default explicit wait timeout in seconds.
     */
    public static final long DEFAULT_EXPLICIT_WAIT_SECONDS = 20L;

    /**
     * Default of the screenshot-on-failure switch.
     */
    public static final boolean DEFAULT_SCREENSHOT_ON_FAILURE = true;

    /**
     * Default of the page-source-on-failure switch. The UI hierarchy may contain test data, so it is
     * only attached on request.
     */
    public static final boolean DEFAULT_PAGE_SOURCE_ON_FAILURE = false;

    private static final String EXTERNAL_PROPERTY_PREFIX = "testautomation.";
    private static final String MASKED_VALUE = "******";
    private static final Pattern SENSITIVE_NAME_PATTERN = Pattern.compile("(?i).*(passw|secret|token|credential|unlockkey|apikey|api_key).*");
    private static final Gson CAPABILITIES_GSON = new GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create();
    private static final Type CAPABILITIES_TYPE = new TypeToken<Map<String, Object>>() {
    }.getType();

    /**
     * Creates a new configuration and copies the additional capabilities into an unmodifiable map.
     * Use {@link #load()} or {@link #fromProperties(Map)} to get a validated configuration.
     */
    public MobileConfig {
        Objects.requireNonNull(appiumServerUrl, "appiumServerUrl");
        Objects.requireNonNull(platformName, "platformName");
        Objects.requireNonNull(automationName, "automationName");
        Objects.requireNonNull(newCommandTimeout, "newCommandTimeout");
        Objects.requireNonNull(explicitWaitTimeout, "explicitWaitTimeout");
        Objects.requireNonNull(additionalCapabilities, "additionalCapabilities");
        additionalCapabilities = Collections.unmodifiableMap(new LinkedHashMap<>(additionalCapabilities));
    }

    /**
     * Reads and validates the mobile configuration from {@link TestProperties}.
     *
     * @return the validated configuration.
     * @throws MobileConfigException if required values are missing or values are invalid.
     */
    public static MobileConfig load() {
        return from(propertyName -> TestProperties.getProperty(propertyName).orElse(null));
    }

    /**
     * Reads and validates the mobile configuration from a map of property values. The keys are the
     * property names without the {@code testautomation.} prefix, for example {@link #PROPERTY_UDID}.
     * Values may be strings or already typed values such as {@link Long} and {@link Boolean}.
     *
     * @param properties the property values.
     * @return the validated configuration.
     * @throws MobileConfigException if required values are missing or values are invalid.
     */
    public static MobileConfig fromProperties(@NotNull Map<String, ?> properties) {
        return from(properties::get);
    }

    /**
     * Returns a copy of the given capabilities in which the values of sensitive entries (passwords,
     * secrets, tokens, unlock keys) are masked, so they can be logged safely.
     *
     * @param capabilities the capabilities to mask.
     * @return a new map with the same keys and masked sensitive values.
     */
    public static Map<String, Object> maskSensitiveCapabilities(@NotNull Map<String, ?> capabilities) {
        Map<String, Object> masked = new LinkedHashMap<>();
        capabilities.forEach((name, value) -> masked.put(name, SENSITIVE_NAME_PATTERN.matcher(name).matches() ? MASKED_VALUE : value));
        return masked;
    }

    /**
     * Returns the name of a property as it is written in property files and on the command line.
     *
     * @param propertyName the stored property name, for example {@link #PROPERTY_UDID}.
     * @return the external name, for example {@code testautomation.mobile.udid}.
     */
    public static String externalName(@NotNull String propertyName) {
        return EXTERNAL_PROPERTY_PREFIX + propertyName;
    }

    @Override
    public String toString() {
        return "MobileConfig[appiumServerUrl=" + appiumServerUrl
                + ", platformName=" + platformName
                + ", platformVersion=" + platformVersion
                + ", automationName=" + automationName
                + ", udid=" + udid
                + ", appPackage=" + appPackage
                + ", appActivity=" + appActivity
                + ", app=" + app
                + ", newCommandTimeout=" + newCommandTimeout
                + ", explicitWaitTimeout=" + explicitWaitTimeout
                + ", screenshotOnFailure=" + screenshotOnFailure
                + ", pageSourceOnFailure=" + pageSourceOnFailure
                + ", additionalCapabilities=" + maskSensitiveCapabilities(additionalCapabilities) + "]";
    }

    private static MobileConfig from(Function<String, Object> source) {
        List<String> problems = new ArrayList<>();

        URL appiumServerUrl = readUrl(source, problems);
        String platformName = readPlatformName(source, problems);
        String platformVersion = readText(source, PROPERTY_PLATFORM_VERSION);
        String automationName = Objects.requireNonNullElse(readText(source, PROPERTY_AUTOMATION_NAME), DEFAULT_AUTOMATION_NAME);
        String udid = readText(source, PROPERTY_UDID);
        String appPackage = readText(source, PROPERTY_APP_PACKAGE);
        String appActivity = readText(source, PROPERTY_APP_ACTIVITY);
        String app = readApp(source, problems);
        if (appPackage == null && readText(source, PROPERTY_APP) == null) {
            problems.add("Neither [" + externalName(PROPERTY_APP_PACKAGE) + "] nor [" + externalName(PROPERTY_APP)
                    + "] is set. Configure the package (and optionally the activity) of an installed app, or the path or URL of an APK to install.");
        }
        Duration newCommandTimeout = readSeconds(source, PROPERTY_NEW_COMMAND_TIMEOUT_SECONDS, DEFAULT_NEW_COMMAND_TIMEOUT_SECONDS, problems);
        Duration explicitWaitTimeout = readSeconds(source, PROPERTY_EXPLICIT_WAIT_SECONDS, DEFAULT_EXPLICIT_WAIT_SECONDS, problems);
        boolean screenshotOnFailure = readFlag(source, PROPERTY_SCREENSHOT_ON_FAILURE, DEFAULT_SCREENSHOT_ON_FAILURE, problems);
        boolean pageSourceOnFailure = readFlag(source, PROPERTY_PAGE_SOURCE_ON_FAILURE, DEFAULT_PAGE_SOURCE_ON_FAILURE, problems);
        Map<String, Object> additionalCapabilities = readCapabilities(source, problems);

        if (!problems.isEmpty()) {
            throw new MobileConfigException(problems);
        }
        return new MobileConfig(appiumServerUrl, platformName, platformVersion, automationName, udid, appPackage, appActivity, app,
                newCommandTimeout, explicitWaitTimeout, screenshotOnFailure, pageSourceOnFailure, additionalCapabilities);
    }

    private static @Nullable
    String readText(Function<String, Object> source, String propertyName) {
        Object value = source.apply(propertyName);
        if (value == null) {
            return null;
        }
        String text = value.toString().trim();
        return text.isEmpty() ? null : text;
    }

    private static @Nullable
    URL readUrl(Function<String, Object> source, List<String> problems) {
        String value = Objects.requireNonNullElse(readText(source, PROPERTY_APPIUM_SERVER_URL), DEFAULT_APPIUM_SERVER_URL);
        String candidate = value;
        while (candidate.endsWith("/")) {
            candidate = candidate.substring(0, candidate.length() - 1);
        }
        try {
            URI uri = new URI(candidate);
            String scheme = uri.getScheme();
            if (("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) && uri.getHost() != null) {
                return uri.toURL();
            }
        } catch (URISyntaxException | MalformedURLException | IllegalArgumentException ignored) {
            // Reported below together with all other configuration problems
        }
        problems.add("[" + externalName(PROPERTY_APPIUM_SERVER_URL) + "] must be an http(s) URL such as " + DEFAULT_APPIUM_SERVER_URL
                + " (append the base path if the server was started with --base-path), but was [" + value + "].");
        return null;
    }

    private static String readPlatformName(Function<String, Object> source, List<String> problems) {
        String value = readText(source, PROPERTY_PLATFORM_NAME);
        if (value == null || DEFAULT_PLATFORM_NAME.equalsIgnoreCase(value)) {
            return DEFAULT_PLATFORM_NAME;
        }
        problems.add("[" + externalName(PROPERTY_PLATFORM_NAME) + "] must be " + DEFAULT_PLATFORM_NAME
                + ", the only platform supported by the ATAF mobile module, but was [" + value + "].");
        return value;
    }

    private static @Nullable
    String readApp(Function<String, Object> source, List<String> problems) {
        String value = readText(source, PROPERTY_APP);
        if (value == null) {
            return null;
        }
        String lowerCaseValue = value.toLowerCase(Locale.ROOT);
        if (lowerCaseValue.startsWith("http://") || lowerCaseValue.startsWith("https://")) {
            return value;
        }
        try {
            Path appPath = Path.of(value).toAbsolutePath().normalize();
            if (Files.isRegularFile(appPath)) {
                return appPath.toString();
            }
            problems.add("[" + externalName(PROPERTY_APP) + "] points to [" + appPath
                    + "], which is not an existing file. The APK must be readable on the machine that runs the tests and the Appium server.");
        } catch (InvalidPathException e) {
            problems.add("[" + externalName(PROPERTY_APP) + "] is neither a valid file path nor an http(s) URL: [" + value + "].");
        }
        return null;
    }

    private static Duration readSeconds(Function<String, Object> source, String propertyName, long defaultSeconds, List<String> problems) {
        String value = readText(source, propertyName);
        if (value == null) {
            return Duration.ofSeconds(defaultSeconds);
        }
        try {
            long seconds = Long.parseLong(value);
            if (seconds > 0) {
                return Duration.ofSeconds(seconds);
            }
        } catch (NumberFormatException ignored) {
            // Reported below together with all other configuration problems
        }
        problems.add("[" + externalName(propertyName) + "] must be a positive whole number of seconds, but was [" + value + "].");
        return Duration.ofSeconds(defaultSeconds);
    }

    private static boolean readFlag(Function<String, Object> source, String propertyName, boolean defaultValue, List<String> problems) {
        String value = readText(source, propertyName);
        if (value == null) {
            return defaultValue;
        }
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        problems.add("[" + externalName(propertyName) + "] must be true or false, but was [" + value + "].");
        return defaultValue;
    }

    private static Map<String, Object> readCapabilities(Function<String, Object> source, List<String> problems) {
        String value = readText(source, PROPERTY_ADDITIONAL_CAPABILITIES);
        if (value == null) {
            return Map.of();
        }
        try {
            JsonElement json = JsonParser.parseString(value);
            if (!json.isJsonObject()) {
                problems.add("[" + externalName(PROPERTY_ADDITIONAL_CAPABILITIES) + "] must be a JSON object such as {\"appium:noReset\":true}.");
                return Map.of();
            }
            Map<String, Object> parsedCapabilities = CAPABILITIES_GSON.fromJson(json, CAPABILITIES_TYPE);
            Map<String, Object> capabilities = new LinkedHashMap<>();
            parsedCapabilities.forEach((name, capabilityValue) -> {
                if (name.isBlank()) {
                    problems.add("[" + externalName(PROPERTY_ADDITIONAL_CAPABILITIES) + "] contains a capability without a name.");
                } else if (capabilityValue == null) {
                    problems.add("[" + externalName(PROPERTY_ADDITIONAL_CAPABILITIES) + "] contains the capability [" + name + "] without a value.");
                } else {
                    capabilities.put(name, capabilityValue);
                }
            });
            return capabilities;
        } catch (JsonParseException e) {
            problems.add("[" + externalName(PROPERTY_ADDITIONAL_CAPABILITIES) + "] is not valid JSON: " + e.getMessage());
            return Map.of();
        }
    }
}
