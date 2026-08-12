package ataf.core.clients;

import ataf.core.assertions.CustomAssertions;
import ataf.core.assertions.strategy.impl.TestNGAssertionStrategy;
import ataf.core.properties.TestProperties;
import ataf.core.properties.TestProperty;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Regression tests for fail-fast proxy port parsing in {@link HttpClient}.
 */
public class HttpClientProxyPortTest {

    private static final List<String> PROXY_PROPERTY_NAMES = List.of(
            "usePac",
            "useProxy",
            "proxyAddress",
            "proxyPort");

    private Map<String, TestProperty<?>> testProperties;
    private Map<String, TestProperty<?>> originalProxyProperties;

    @BeforeClass
    @SuppressWarnings("unchecked")
    public void setUpAssertions() throws ReflectiveOperationException {
        CustomAssertions.setStrategy(new TestNGAssertionStrategy());

        Field testPropertiesField = TestProperties.class.getDeclaredField("TEST_PROPERTIES_MAP");
        testPropertiesField.setAccessible(true);
        testProperties = (Map<String, TestProperty<?>>) testPropertiesField.get(null);
    }

    @BeforeMethod
    public void preserveProxyProperties() {
        originalProxyProperties = new HashMap<>();
        for (String propertyName : PROXY_PROPERTY_NAMES) {
            TestProperty<?> property = testProperties.get(propertyName);
            if (property != null) {
                originalProxyProperties.put(propertyName, property);
            }
        }
    }

    @AfterMethod
    public void restoreProxyProperties() {
        for (String propertyName : PROXY_PROPERTY_NAMES) {
            testProperties.remove(propertyName);
        }
        testProperties.putAll(originalProxyProperties);
    }

    @DataProvider(name = "validProxyPorts")
    public Object[][] validProxyPorts() {
        return new Object[][] {
                { "1", 1 },
                { "80", 80 },
                { "65535", 65535 },
                { " 8080 ", 8080 }
        };
    }

    @DataProvider(name = "invalidProxyPorts")
    public Object[][] invalidProxyPorts() {
        return new Object[][] {
                { null },
                { "" },
                { "   " },
                { "abc" },
                { "0" },
                { "-1" },
                { "65536" },
                { "80.5" }
        };
    }

    @DataProvider(name = "validTypedProxyPorts")
    public Object[][] validTypedProxyPorts() {
        return new Object[][] {
                { 1 },
                { 8080 },
                { 65535 }
        };
    }

    @DataProvider(name = "invalidTypedProxyPorts")
    public Object[][] invalidTypedProxyPorts() {
        return new Object[][] {
                { -1 },
                { 0 },
                { 65536 }
        };
    }

    @Test(dataProvider = "validProxyPorts")
    public void parseProxyPort_acceptsValidValues(String configuredPort, int expected) {
        CustomAssertions.assertEquals(HttpClient.parseProxyPort(configuredPort), expected);
    }

    @Test(dataProvider = "invalidProxyPorts")
    public void parseProxyPort_rejectsInvalidValues(String configuredPort) {
        CustomAssertions.assertThrows(
                IllegalArgumentException.class,
                () -> HttpClient.parseProxyPort(configuredPort));
    }

    @Test(dataProvider = "validTypedProxyPorts")
    public void createForTarget_acceptsDocumentedTypedProxyProperties(int configuredPort) {
        configureProxy(true, configuredPort);
        RecordingClientFactory factory = new RecordingClientFactory();

        HttpClient.createForTarget("https://example.org", factory);

        CustomAssertions.assertTrue(factory.proxyCalled);
        CustomAssertions.assertEquals((Object) factory.proxyHostname, (Object) "proxy.example.org");
        CustomAssertions.assertEquals(factory.proxyPort, configuredPort);
        CustomAssertions.assertFalse(factory.directCalled);
    }

    @Test(dataProvider = "invalidTypedProxyPorts")
    public void createForTarget_rejectsOutOfRangeTypedProxyPorts(int configuredPort) {
        configureProxy(true, configuredPort);
        RecordingClientFactory factory = new RecordingClientFactory();

        CustomAssertions.assertThrows(
                IllegalArgumentException.class,
                () -> HttpClient.createForTarget("https://example.org", factory));
        CustomAssertions.assertFalse(factory.proxyCalled);
        CustomAssertions.assertFalse(factory.directCalled);
    }

    @Test
    public void createForTarget_preservesLegacyStringProxyProperties() {
        configureProxy("true", "8080");
        RecordingClientFactory factory = new RecordingClientFactory();

        HttpClient.createForTarget("https://example.org", factory);

        CustomAssertions.assertTrue(factory.proxyCalled);
        CustomAssertions.assertEquals(factory.proxyPort, 8080);
    }

    private void configureProxy(Object useProxy, Object proxyPort) {
        new TestProperty<>("useProxy", useProxy);
        new TestProperty<>("proxyAddress", "proxy.example.org");
        new TestProperty<>("proxyPort", proxyPort);
    }

    private static final class RecordingClientFactory implements ClientFactory<HttpClient> {
        private boolean directCalled;
        private boolean proxyCalled;
        private String proxyHostname;
        private int proxyPort;

        @Override
        public HttpClient direct() {
            directCalled = true;
            return null;
        }

        @Override
        public HttpClient withProxy(String proxyHostname, int proxyPort) {
            proxyCalled = true;
            this.proxyHostname = proxyHostname;
            this.proxyPort = proxyPort;
            return null;
        }
    }
}
