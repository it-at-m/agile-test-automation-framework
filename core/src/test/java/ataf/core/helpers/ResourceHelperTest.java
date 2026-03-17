package ataf.core.helpers;

import ataf.core.assertions.CustomAssertions;
import ataf.core.assertions.strategy.impl.TestNGAssertionStrategy;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Tests for {@link ResourceHelper}.
 *
 * <p>
 * Hinweise zu Test-Ressourcen:
 * </p>
 * <ul>
 * <li>Text-Datei: <code>src/test/resources/testdata/sample.txt</code></li>
 * </ul>
 *
 * <p>
 * Beispiel-Inhalt für <code>sample.txt</code>:
 * </p>
 *
 * <pre>
 * sample - content
 * </pre>
 */
public class ResourceHelperTest {

    @BeforeClass
    public void setUpAssertions() {
        CustomAssertions.setStrategy(new TestNGAssertionStrategy());
    }

    @Test
    public void loadAsString_fromClasspath_succeeds() {
        // Voraussetzung: src/test/resources/testdata/sample.txt mit Inhalt "sample-content"
        String content = ResourceHelper.loadAsString("testdata/sample.txt");

        CustomAssertions.assertEquals(content.trim(), "sample-content");
    }

    @Test
    public void loadAsStream_fromFileSystem_succeeds() throws Exception {
        // Temp-Datei im Filesystem erstellen
        Path tempFile = Files.createTempFile("resource-helper-test", ".txt");
        String expected = "file-system-content";
        Files.writeString(tempFile, expected, StandardCharsets.UTF_8);

        try (InputStream is = ResourceHelper.loadAsStream(tempFile.toString())) {
            String loaded = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            CustomAssertions.assertEquals(loaded, expected);
        } finally {
            // Aufräumen (optional, aber höflich)
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    public void loadAsString_missingResource_throwsRuntimeException() {
        CustomAssertions.assertThrows(
                RuntimeException.class,
                () -> ResourceHelper.loadAsString("does/not/exist.txt"));
    }
}
