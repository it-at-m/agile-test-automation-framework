package ataf.core.utils;

import ataf.core.assertions.CustomAssertions;
import ataf.core.assertions.strategy.impl.TestNGAssertionStrategy;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Regression tests for {@link DateUtils#getRandomBirthDate(int, int)}.
 */
public class DateUtilsTest {

    private static final ZoneId BERLIN = ZoneId.of("Europe/Berlin");

    @BeforeClass
    public void setUpAssertions() {
        CustomAssertions.setStrategy(new TestNGAssertionStrategy());
    }

    @Test
    public void getRandomBirthDate_respectsAgeBounds() {
        LocalDate today = LocalDate.now(BERLIN);
        for (int i = 0; i < 200; i++) {
            LocalDate birthDate = DateUtils.getRandomBirthDate(18, 65);
            int yearOffset = today.getYear() - birthDate.getYear();
            CustomAssertions.assertTrue(yearOffset >= 18 && yearOffset <= 65,
                    "Year offset " + yearOffset + " for birth date " + birthDate + " is outside [18, 65]");
            // Random month/day can make chronological age one year younger than the year offset.
            int chronologicalAge = Period.between(birthDate, today).getYears();
            CustomAssertions.assertTrue(chronologicalAge >= 17 && chronologicalAge <= 65,
                    "Chronological age " + chronologicalAge + " for birth date " + birthDate
                            + " is outside the expected [17, 65] window");
        }
    }

    @Test
    public void getRandomBirthDate_producesValidFebruaryDates() {
        Set<Integer> februaryDays = new HashSet<>();
        for (int i = 0; i < 500; i++) {
            LocalDate birthDate = DateUtils.getRandomBirthDate(20, 40);
            if (birthDate.getMonthValue() == 2) {
                februaryDays.add(birthDate.getDayOfMonth());
                CustomAssertions.assertTrue(
                        birthDate.getDayOfMonth() <= birthDate.lengthOfMonth(),
                        "Invalid February date: " + birthDate);
            }
        }
        CustomAssertions.assertFalse(februaryDays.isEmpty(), "Expected at least one February sample");
    }

    @Test
    public void getRandomBirthDate_rejectsInvertedAgeRange() {
        CustomAssertions.assertThrows(
                IllegalArgumentException.class,
                () -> DateUtils.getRandomBirthDate(40, 20));
    }

    @Test
    public void getRandomBirthDate_isSafeUnderConcurrentUse() throws Exception {
        int threads = 8;
        int iterationsPerThread = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicReference<Throwable> failure = new AtomicReference<>();

        for (int t = 0; t < threads; t++) {
            executor.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    for (int i = 0; i < iterationsPerThread; i++) {
                        DateUtils.getRandomBirthDate(18, 65);
                    }
                } catch (Throwable throwable) {
                    failure.compareAndSet(null, throwable);
                } finally {
                    done.countDown();
                }
            });
        }

        CustomAssertions.assertTrue(ready.await(5, TimeUnit.SECONDS));
        start.countDown();
        CustomAssertions.assertTrue(done.await(10, TimeUnit.SECONDS));
        executor.shutdownNow();

        CustomAssertions.assertTrue(failure.get() == null,
                "Concurrent getRandomBirthDate failed: " + failure.get());
    }
}
