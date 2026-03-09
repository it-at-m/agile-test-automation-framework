package ataf.rest.auth;

import ataf.core.assertions.CustomAssertions;
import ataf.core.assertions.strategy.impl.TestNGAssertionStrategy;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Optional;

public class InMemoryTokenStoreTest {
    @BeforeClass
    public void setUp() {
        CustomAssertions.setStrategy(new TestNGAssertionStrategy());
    }

    @Test
    public void putAndGet_shouldRoundTrip() {
        InMemoryTokenStore inMemoryTokenStore = new InMemoryTokenStore();
        TokenInfo token = new TokenInfo("acc", "Bearer", null, "ref");

        inMemoryTokenStore.put("k1", token);
        Optional<TokenInfo> optionalTokenInfo = inMemoryTokenStore.get("k1");

        CustomAssertions.assertTrue(optionalTokenInfo.isPresent());
        CustomAssertions.assertEquals(optionalTokenInfo.get().accessToken(), "acc");
    }

    @Test
    public void clear_shouldRemoveKey() {
        InMemoryTokenStore inMemoryTokenStore = new InMemoryTokenStore();
        inMemoryTokenStore.put("k2", new TokenInfo("x", "Bearer", null, null));

        inMemoryTokenStore.clear("k2");
        CustomAssertions.assertTrue(inMemoryTokenStore.get("k2").isEmpty());
    }

    @Test
    public void get_shouldReturnEmpty_whenKeyMissing() {
        InMemoryTokenStore inMemoryTokenStore = new InMemoryTokenStore();
        CustomAssertions.assertTrue(inMemoryTokenStore.get("missing").isEmpty());
    }
}
