package ataf.core.helpers;

import ataf.core.logging.ScenarioLogManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for loading resources (JSON, XML, CSV, etc.) from either the classpath
 * or the file system.
 *
 * <p>
 * The lookup order is:
 * </p>
 * <ol>
 * <li>Classpath (using the current thread context class loader)</li>
 * <li>File system (using {@link java.nio.file.Files})</li>
 * </ol>
 *
 * <p>
 * Paths may be given with or without a leading slash. For classpath lookups, the leading
 * slash is removed automatically.
 * </p>
 */
public final class ResourceHelper {

    private ResourceHelper() {
        // Utility class
    }

    /**
     * Loads the given path as an {@link InputStream}. The caller is responsible for
     * closing the returned stream.
     *
     * @param path classpath resource or file system path
     * @return input stream for the resource
     * @throws RuntimeException if the resource cannot be found or read
     */
    public static InputStream loadAsStream(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Resource path must not be null or blank");
        }

        String normalized = path.startsWith("/") ? path.substring(1) : path;

        // 1) Try classpath
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        InputStream resourceAsStream = contextClassLoader.getResourceAsStream(normalized);
        if (resourceAsStream != null) {
            ScenarioLogManager.getLogger()
                    .debug("Loaded resource [{}] from classpath", normalized);
            return resourceAsStream;
        }

        // 2) Fall back to file system
        Path filePath = Paths.get(path);
        try {
            ScenarioLogManager.getLogger()
                    .debug("Loading resource [{}] from file system path {}", normalized, filePath.toAbsolutePath());
            return Files.newInputStream(filePath);
        } catch (IOException e) {
            ScenarioLogManager.getLogger()
                    .error("Failed to load resource from path: {}", path, e);
            throw new RuntimeException("Failed to load resource from path: " + path, e);
        }
    }

    /**
     * Loads the given path as a UTF-8 string.
     *
     * @param path classpath resource or file system path
     * @return resource content as UTF-8 string
     * @throws RuntimeException if the resource cannot be found or read
     */
    public static String loadAsString(String path) {
        try (InputStream inputStream = loadAsStream(path)) {
            byte[] bytes = inputStream.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            ScenarioLogManager.getLogger()
                    .error("Failed to read resource [{}] as string", path, e);
            throw new RuntimeException("Failed to read resource as string: " + path, e);
        }
    }
}
