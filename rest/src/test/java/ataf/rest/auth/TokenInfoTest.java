package ataf.rest.auth;

import ataf.core.assertions.CustomAssertions;
import ataf.core.assertions.strategy.impl.TestNGAssertionStrategy;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TokenInfoTest {
    @BeforeClass
    public void setUp() {
        CustomAssertions.setStrategy(new TestNGAssertionStrategy());
    }

    @Test
    public void isExpiredOrStale_shouldBeFalse_forFutureExpiry() {
        TokenInfo tokenInfo = new TokenInfo("abc", "Bearer",
                Instant.now().plus(5, ChronoUnit.MINUTES), "ref");
        CustomAssertions.assertFalse(tokenInfo.isExpiredOrStale(), "Token should not be considered expired or stale");
    }

    @Test
    public void isExpiredOrStale_shouldBeTrue_forPastExpiry() {
        TokenInfo tokenInfo = new TokenInfo("abc", "Bearer",
                Instant.now().minus(1, ChronoUnit.MINUTES), "ref");
        CustomAssertions.assertTrue(tokenInfo.isExpiredOrStale(), "Token should be considered expired");
    }

    @Test
    public void isExpiredOrStale_shouldBeTrue_whenExpiresAtNull() {
        TokenInfo tokenInfo = new TokenInfo("abc", "Bearer", null, "ref");
        CustomAssertions.assertTrue(tokenInfo.isExpiredOrStale(), "Null expiry must be treated as expired");
    }

    @Test
    public void isExpiredOrStale_shouldBeTrue_forWithin30sWindow() {
        TokenInfo tokenInfo = new TokenInfo("abc", "Bearer",
                Instant.now().plusSeconds(10), "ref");
        CustomAssertions.assertTrue(tokenInfo.isExpiredOrStale(), "Token expiring within 30s should be considered stale");
    }
}
