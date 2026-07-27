package ataf.core.data;

import ataf.core.assertions.CustomAssertions;
import ataf.core.assertions.strategy.impl.TestNGAssertionStrategy;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Regression tests for {@link Environment} equality and ordering contracts.
 */
public class EnvironmentEqualityTest {

    @BeforeClass
    public void setUpAssertions() {
        CustomAssertions.setStrategy(new TestNGAssertionStrategy());
    }

    @Test
    public void equals_sameName_areEqualRegardlessOfInstance() {
        Environment first = new Environment("equality-env-a");
        Environment second = Environment.contains("equality-env-a");

        CustomAssertions.assertNotNull(second);
        CustomAssertions.assertTrue(first.equals(second));
        CustomAssertions.assertEquals(first.hashCode(), second.hashCode());
        CustomAssertions.assertEquals(first.compareTo(second), 0);
    }

    @Test
    public void equals_differentNames_areNotEqual() {
        Environment first = new Environment("equality-env-b");
        Environment second = new Environment("equality-env-c");

        CustomAssertions.assertFalse(first.equals(second));
        CustomAssertions.assertTrue(first.compareTo(second) != 0);
    }

    @Test
    public void contains_findsEnvironmentByName() {
        Environment created = new Environment("equality-env-lookup");
        Environment found = Environment.contains("equality-env-lookup");

        CustomAssertions.assertNotNull(found);
        CustomAssertions.assertTrue(created.equals(found));
        CustomAssertions.assertEquals(found.getName(), "equality-env-lookup");
    }
}
