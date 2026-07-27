package ataf.core.xray;

import ataf.core.assertions.CustomAssertions;
import ataf.core.assertions.strategy.impl.TestNGAssertionStrategy;
import org.testng.annotations.BeforeClass;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Regression tests for {@link ataf.core.xray.Test} equality and ordering contracts.
 */
public class TestEqualityTest {

    @BeforeClass
    public void setUpAssertions() {
        CustomAssertions.setStrategy(new TestNGAssertionStrategy());
    }

    @org.testng.annotations.Test
    public void equals_sameRankDifferentId_areNotEqual() {
        Test first = new Test(101, "ATAF-101", 1, TestStatus.TODO);
        Test second = new Test(205, "ATAF-205", 1, TestStatus.TODO);

        CustomAssertions.assertFalse(first.equals(second));
        CustomAssertions.assertFalse(first.hashCode() == second.hashCode() && first.equals(second));
    }

    @org.testng.annotations.Test
    public void equals_sameId_areEqual() {
        Test first = new Test(101, "ATAF-101", 1, TestStatus.TODO);
        Test second = new Test(101, "ATAF-101", 9, TestStatus.PASS);

        CustomAssertions.assertTrue(first.equals(second));
        CustomAssertions.assertEquals(first.hashCode(), second.hashCode());
    }

    @org.testng.annotations.Test
    public void hashSet_keepsDistinctIdsWithSameRank() {
        Test first = new Test(101, "ATAF-101", 1, TestStatus.TODO);
        Test second = new Test(205, "ATAF-205", 1, TestStatus.TODO);

        HashSet<Test> tests = new HashSet<>();
        tests.add(first);
        tests.add(second);

        CustomAssertions.assertEquals(tests.size(), 2);
    }

    @org.testng.annotations.Test
    public void hashMap_doesNotOverwriteDistinctIdsWithSameRank() {
        Test first = new Test(101, "ATAF-101", 1, TestStatus.TODO);
        Test second = new Test(205, "ATAF-205", 1, TestStatus.TODO);

        HashMap<Test, String> map = new HashMap<>();
        map.put(first, "first");
        map.put(second, "second");

        CustomAssertions.assertEquals(map.get(first), "first");
        CustomAssertions.assertEquals(map.get(second), "second");
    }

    @org.testng.annotations.Test
    public void concurrentSkipListSet_keepsDistinctIdsWithSameRank() {
        Test first = new Test(101, "ATAF-101", 1, TestStatus.TODO);
        Test second = new Test(205, "ATAF-205", 1, TestStatus.TODO);

        ConcurrentSkipListSet<Test> tests = new ConcurrentSkipListSet<>();
        tests.add(first);
        tests.add(second);

        CustomAssertions.assertEquals(tests.size(), 2);
        CustomAssertions.assertTrue(first.compareTo(second) < 0);
    }

    @org.testng.annotations.Test
    public void compareTo_andEquals_areConsistentForSameIdAndRank() {
        Test first = new Test(101, "ATAF-101", 1, TestStatus.TODO);
        Test second = new Test(101, "ATAF-101", 1, TestStatus.FAIL);

        CustomAssertions.assertEquals(first.compareTo(second), 0);
        CustomAssertions.assertTrue(first.equals(second));
    }
}
