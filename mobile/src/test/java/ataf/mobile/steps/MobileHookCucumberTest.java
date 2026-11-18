package ataf.mobile.steps;

import ataf.mobile.utils.MobileDriverUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.appium.java_client.android.AndroidDriver;
import io.cucumber.core.cli.Main;
import org.openqa.selenium.OutputType;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Runs Cucumber in-process on {@code src/test/resources/ataf/mobile/lifecycle} with the real mobile
 * hook and a fake driver factory. Verifies that only {@code @mobile} scenarios start sessions, that
 * every session is closed, that failures reach the exit status, the JSON report and the JUnit XML
 * report, and that failed scenarios carry a screenshot.
 */
public class MobileHookCucumberTest {
    private static final Path REPORT_DIRECTORY = Path.of("target", "mobile-hook-cucumber-test");
    private static final byte[] SCREENSHOT = { (byte) 0x89, 'P', 'N', 'G' };

    @Test(timeOut = 120000)
    public void shouldManageSessionsAndReportFailuresOfMobileScenarios() throws IOException {
        List<AndroidDriver> drivers = new CopyOnWriteArrayList<>();
        MobileDriverUtil.setDriverFactory((appiumServerUrl, options) -> {
            AndroidDriver driver = mock(AndroidDriver.class);
            when(driver.getScreenshotAs(OutputType.BYTES)).thenReturn(SCREENSHOT);
            drivers.add(driver);
            return driver;
        });
        Path jsonReport = REPORT_DIRECTORY.resolve("cucumber.json");
        Path junitReport = REPORT_DIRECTORY.resolve("cucumber.xml");
        Files.createDirectories(REPORT_DIRECTORY);
        Files.deleteIfExists(jsonReport);
        Files.deleteIfExists(junitReport);

        byte exitStatus;
        try {
            exitStatus = Main.run(new String[] {
                    "--glue", "ataf.mobile.steps",
                    "--glue", "ataf.mobile.testsupport.glue",
                    "--plugin", "json:" + jsonReport,
                    "--plugin", "junit:" + junitReport,
                    "--monochrome",
                    "classpath:ataf/mobile/lifecycle"
            }, Thread.currentThread().getContextClassLoader());
        } finally {
            MobileDriverUtil.setDriverFactory(null);
        }

        Assert.assertEquals(exitStatus, (byte) 1, "A failed scenario must lead to a non-zero exit status");
        Assert.assertEquals(drivers.size(), 2, "Only the two @mobile scenarios may start a session");
        for (AndroidDriver driver : drivers) {
            verify(driver).quit();
        }
        Assert.assertFalse(MobileDriverUtil.hasDriver(), "All sessions must be closed after the run");

        Map<String, JsonObject> scenarios = readScenarios(jsonReport);
        Assert.assertEquals(statusOf(scenarios.get("A mobile scenario gets an Android session")), "passed");
        Assert.assertEquals(statusOf(scenarios.get("A failing mobile scenario keeps its failure")), "failed");
        Assert.assertEquals(statusOf(scenarios.get("A web scenario does not start an Android session")), "passed");
        Assert.assertEquals(statusOf(scenarios.get("An untagged scenario does not start an Android session")), "passed");
        Assert.assertTrue(hasPngAttachment(scenarios.get("A failing mobile scenario keeps its failure")),
                "The failed scenario must carry a screenshot");
        Assert.assertFalse(hasPngAttachment(scenarios.get("A mobile scenario gets an Android session")),
                "Passed scenarios get no screenshot");

        String junitXml = Files.readString(junitReport);
        Assert.assertTrue(junitXml.contains("tests=\"4\""), junitXml);
        Assert.assertTrue(junitXml.contains("failures=\"1\""), junitXml);
    }

    private static Map<String, JsonObject> readScenarios(Path jsonReport) throws IOException {
        Map<String, JsonObject> scenarios = new LinkedHashMap<>();
        for (JsonElement feature : JsonParser.parseString(Files.readString(jsonReport)).getAsJsonArray()) {
            for (JsonElement element : feature.getAsJsonObject().getAsJsonArray("elements")) {
                scenarios.put(element.getAsJsonObject().get("name").getAsString(), element.getAsJsonObject());
            }
        }
        Assert.assertEquals(scenarios.size(), 4, "Unexpected scenarios in the JSON report: " + scenarios.keySet());
        return scenarios;
    }

    private static String statusOf(JsonObject scenario) {
        for (String section : List.of("before", "steps", "after")) {
            if (scenario.has(section)) {
                for (JsonElement result : scenario.getAsJsonArray(section)) {
                    if (!"passed".equals(result.getAsJsonObject().getAsJsonObject("result").get("status").getAsString())) {
                        return "failed";
                    }
                }
            }
        }
        return "passed";
    }

    private static boolean hasPngAttachment(JsonObject scenario) {
        if (!scenario.has("after")) {
            return false;
        }
        for (JsonElement hook : scenario.getAsJsonArray("after")) {
            JsonArray embeddings = hook.getAsJsonObject().getAsJsonArray("embeddings");
            if (embeddings != null) {
                for (JsonElement embedding : embeddings) {
                    if ("image/png".equals(embedding.getAsJsonObject().get("mime_type").getAsString())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
