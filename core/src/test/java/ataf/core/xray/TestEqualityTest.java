package ataf.core.xray;

import ataf.core.assertions.CustomAssertions;
import ataf.core.assertions.strategy.impl.TestNGAssertionStrategy;
import org.testng.annotations.BeforeClass;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
        CustomAssertions.assertEquals(first.compareTo(second), 0);
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

        CustomAssertions.assertEquals((Object) map.get(first), (Object) "first");
        CustomAssertions.assertEquals((Object) map.get(second), (Object) "second");
    }

    @org.testng.annotations.Test
    public void hashCollections_treatSameIdWithDifferentRankAsOneTest() {
        Test first = new Test(101, "ATAF-101", 1, TestStatus.TODO);
        Test second = new Test(101, "RENAMED-101", 9, TestStatus.PASS);

        HashSet<Test> set = new HashSet<>();
        set.add(first);
        set.add(second);

        HashMap<Test, String> map = new HashMap<>();
        map.put(first, "first");
        map.put(second, "second");

        CustomAssertions.assertEquals(set.size(), 1);
        CustomAssertions.assertEquals(map.size(), 1);
        CustomAssertions.assertEquals((Object) map.get(first), (Object) "second");
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
    public void concurrentSkipListSet_treatsSameIdWithDifferentRankAsOneTest() {
        Test first = new Test(101, "ATAF-101", 1, TestStatus.TODO);
        Test second = new Test(101, "RENAMED-101", 9, TestStatus.FAIL);

        ConcurrentSkipListSet<Test> tests = new ConcurrentSkipListSet<>();
        tests.add(first);
        tests.add(second);

        CustomAssertions.assertEquals(tests.size(), 1);
        CustomAssertions.assertEquals(first.compareTo(second), 0);
        CustomAssertions.assertTrue(first.equals(second));
    }

    @org.testng.annotations.Test
    public void compareTo_obeysEqualityAndOrderingContracts() {
        List<Test> tests = List.of(
                new Test(205, "ATAF-205", 1, TestStatus.TODO),
                new Test(101, "ATAF-101", 9, TestStatus.PASS),
                new Test(205, "RENAMED-205", 7, TestStatus.FAIL),
                new Test(333, "ATAF-333", 1, TestStatus.TODO));

        for (Test first : tests) {
            for (Test second : tests) {
                int comparison = first.compareTo(second);
                int reverseComparison = second.compareTo(first);

                CustomAssertions.assertEquals(comparison == 0, first.equals(second),
                        "compareTo must return zero exactly when equals returns true");
                CustomAssertions.assertEquals(Integer.signum(comparison), -Integer.signum(reverseComparison),
                        "compareTo must be antisymmetric");

                for (Test third : tests) {
                    if (comparison > 0 && second.compareTo(third) > 0) {
                        CustomAssertions.assertTrue(first.compareTo(third) > 0,
                                "compareTo must be transitive");
                    }
                }
            }
        }
    }
}
