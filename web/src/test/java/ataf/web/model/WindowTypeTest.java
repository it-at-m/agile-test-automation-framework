package ataf.web.model;

import ataf.core.assertions.CustomAssertions;
import ataf.core.assertions.strategy.impl.TestNGAssertionStrategy;
import ataf.core.data.System;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class WindowTypeTest {
    System system;
    WindowType windowType;

    @BeforeClass
    public void setUp() {
        CustomAssertions.setStrategy(new TestNGAssertionStrategy());
    }

    @Test
    public void getSystemWindowTypeWithSystemNullTest() {
        CustomAssertions.assertEquals(WindowType.getSystemWindowType((System) null), WindowType.UNKNOWN);
    }

    @Test
    public void getSystemWindowTypeWithSystemNameNullTest() {
        CustomAssertions.assertEquals(WindowType.getSystemWindowType((String) null), WindowType.UNKNOWN);
    }

    @Test
    public void getSystemWindowTypeWithSystemUrlNullTest() {
        CustomAssertions.assertEquals(WindowType.getSystemWindowType((URL) null), WindowType.UNKNOWN);
    }

    @Test
    public void createWindowTypeTest() {
        system = new System("my.test", "http://my.test.de");
        windowType = new WindowType("my.test.windowType", system);
    }

    @Test
    public void getSystemWindowTypeWithSystemTest() {
        CustomAssertions.assertEquals(WindowType.getSystemWindowType(system), windowType);
    }

    @Test
    public void getSystemWindowTypeWithSystemNameTest() {
        CustomAssertions.assertEquals(WindowType.getSystemWindowType(system.NAME), windowType);
    }

    @Test
    public void getSystemWindowTypeWithSystemUrlTest() throws MalformedURLException {
        try {
            CustomAssertions.assertEquals(WindowType.getSystemWindowType(new URI(system.URL).toURL()), windowType);
        } catch (URISyntaxException e) {
            CustomAssertions.fail(e.getMessage(), e);
        }
    }
}
