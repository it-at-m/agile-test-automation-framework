package ataf.core.clients;

import ataf.core.assertions.CustomAssertions;
import ataf.core.assertions.strategy.impl.TestNGAssertionStrategy;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Regression tests for fail-fast proxy port parsing in {@link HttpClient}.
 */
public class HttpClientProxyPortTest {

    @BeforeClass
    public void setUpAssertions() {
        CustomAssertions.setStrategy(new TestNGAssertionStrategy());
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
}
