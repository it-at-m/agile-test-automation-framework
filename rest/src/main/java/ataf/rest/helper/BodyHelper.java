package ataf.rest.helper;

import io.restassured.specification.MultiPartSpecification;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class handles the body content and multi-part specifications for HTTP requests.
 *
 * <p>
 * Implementation note: body and multipart data are stored in {@link ConcurrentHashMap} variables
 * to support parallel test execution without cross-thread interference.
 * </p>
 */
public class BodyHelper {

    private static final Map<Long, String> BODY_STRING_HOLDER_MAP = new ConcurrentHashMap<>();
    private static final Map<Long, MultiPartSpecification> MULTI_PART_SPECIFICATION_HOLDER_MAP = new ConcurrentHashMap<>();
    private static final Map<Long, RequestHelper> REQUEST_HELPER_HOLDER_MAP = new ConcurrentHashMap<>();

    private BodyHelper() {
        // Utility class
    }

    /**
     * Sets the {@link RequestHelper} instance for this thread.
     * Must be called before using {@link #setMultiPartParameters(MultiPartSpecification)}.
     *
     * @param requestHelper The RequestHelper instance
     */
    public static void setRequestHelper(RequestHelper requestHelper) {
        REQUEST_HELPER_HOLDER_MAP.put(Thread.currentThread().threadId(), requestHelper);
    }

    /**
     * Returns the current body content as a string.
     *
     * @return The body content
     */
    public static String getBodyString() {
        return BODY_STRING_HOLDER_MAP.get(Thread.currentThread().threadId());
    }

    /**
     * Replaces the string in the internal buffer with a new raw string.
     * If UTF-8 encoding is required, use {@link #setBodyStringAndFormatUTF8(String)}.
     *
     * @param newBodyString The new body string to set
     */
    public static void setBodyString(String newBodyString) {
        BODY_STRING_HOLDER_MAP.put(Thread.currentThread().threadId(), newBodyString);
    }

    /**
     * Convenience method to set the body as a UTF-8 encoded string.
     * <p>
     * This is the preferred method for test code and step definitions.
     * </p>
     *
     * @param body The new body string to set
     */
    public static void setBodyFromString(String body) {
        setBodyStringAndFormatUTF8(body);
    }

    /**
     * Returns the current multi-part specification.
     *
     * @return The multi-part specification
     */
    public static MultiPartSpecification getMultiPartSpecification() {
        return MULTI_PART_SPECIFICATION_HOLDER_MAP.get(Thread.currentThread().threadId());
    }

    /**
     * Adds a multi-part specification to the internal body buffer.
     * If no multi-part specification is defined and the value is null,
     * no multi-part will be included in the request.
     *
     * @param multiPartSpecification The multi-part specification to set
     */
    public static void setMultiPartParameters(MultiPartSpecification multiPartSpecification) {
        MULTI_PART_SPECIFICATION_HOLDER_MAP.put(Thread.currentThread().threadId(), multiPartSpecification);

        RequestHelper requestHelper = REQUEST_HELPER_HOLDER_MAP.get(Thread.currentThread().threadId());
        if (requestHelper != null && multiPartSpecification != null) {
            requestHelper.getRequestSpecification().multiPart(multiPartSpecification);
        }
    }

    /**
     * Sets the body content as a UTF-8 encoded string.
     *
     * @param newBodyString The new body string to set, encoded in UTF-8
     */
    public static void setBodyStringAndFormatUTF8(String newBodyString) {
        BODY_STRING_HOLDER_MAP.put(Thread.currentThread().threadId(), encodeStringInUTF8(newBodyString));
    }

    /**
     * Encodes the provided string in UTF-8.
     *
     * @param string The string to encode
     * @return The UTF-8 encoded string
     */
    private static String encodeStringInUTF8(String string) {
        byte[] bytes = string != null
                ? string.getBytes(StandardCharsets.UTF_8)
                : new byte[0];
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Loads the request body from the given path and sets it as UTF-8 encoded string.
     *
     * <p>
     * The method first tries to load the content from the classpath (using the
     * current thread context class loader). If no resource is found, it falls back to
     * loading from the file system using {@link java.nio.file.Files#readString(Path)}.
     * </p>
     *
     * @param path path to the resource or file (e.g. "payloads/user.json" or
     *            "src/test/resources/payloads/user.json")
     * @throws RuntimeException if the content cannot be loaded
     */
    public static void setBodyFromFile(String path) {
        String content = loadResourceOrFile(path);
        setBodyStringAndFormatUTF8(content);
    }

    /**
     * Loads a template from the given path, applies a simple replacement-based templating
     * using the provided variables and sets the result as the request body.
     *
     * <div>
     * Placeholders in the template are expected in the form <code>{{key}}</code>, where
     * <code>key</code> corresponds to an entry in the {@code variables} map.
     * Example:
     *
     * <pre>
     *   {
     *     "name": "{{name}}",
     *     "age": "{{age}}"
     *   }
     * </pre>
     *
     * </div>
     *
     * @param templatePath the path to the template (classpath or file system)
     * @param variables key-value pairs used to replace placeholders in the template
     * @throws RuntimeException if the template cannot be loaded
     */
    public static void setBodyFromTemplate(String templatePath, Map<String, ?> variables) {
        String template = loadResourceOrFile(templatePath);
        String rendered = applySimpleTemplate(template, variables);
        setBodyStringAndFormatUTF8(rendered);
    }

    /**
     * Loads content either from the classpath (preferred) or from the file system.
     *
     * @param path classpath resource or file system path
     * @return the content as UTF-8 string
     */
    private static String loadResourceOrFile(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Path for body content must not be null or blank");
        }

        // Normalize leading slash for classpath lookups
        String normalized = path.startsWith("/") ? path.substring(1) : path;

        // 1) Try classpath
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try (InputStream is = cl.getResourceAsStream(normalized)) {
            if (is != null) {
                byte[] bytes = is.readAllBytes();
                return new String(bytes, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load body from classpath resource: " + path, e);
        }

        // 2) Fallback: file system
        Path filePath = Paths.get(path);
        try {
            return Files.readString(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load body from file system path: " + path, e);
        }
    }

    /**
     * Applies a very simple template mechanism based on string replacement.
     *
     * <p>
     * For each entry in {@code variables}, all occurrences of {@code {{key}}} in the template
     * are replaced with the string representation of the value.
     * </p>
     *
     * @param template the raw template string
     * @param variables replacement variables
     * @return the rendered template
     */
    private static String applySimpleTemplate(String template, Map<String, ?> variables) {
        if (template == null) {
            return "";
        }
        if (variables == null || variables.isEmpty()) {
            return template;
        }

        String result = template;
        for (Map.Entry<String, ?> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() == null ? "" : entry.getValue().toString();
            result = result.replace(placeholder, value);
        }
        return result;
    }

    /**
     * Resets the body content and multi-part specification to their default values
     * for the current thread.
     *
     * <p>
     * This method should be called during test teardown (e.g., per scenario) to avoid
     * leaking state between tests when using parallel execution.
     * </p>
     */
    public static void resetParameters() {
        BODY_STRING_HOLDER_MAP.remove(Thread.currentThread().threadId());
        MULTI_PART_SPECIFICATION_HOLDER_MAP.remove(Thread.currentThread().threadId());
        REQUEST_HELPER_HOLDER_MAP.remove(Thread.currentThread().threadId());
    }
}
