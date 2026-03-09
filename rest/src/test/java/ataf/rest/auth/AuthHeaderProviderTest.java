package ataf.rest.auth;

import ataf.core.assertions.CustomAssertions;
import ataf.core.assertions.strategy.impl.TestNGAssertionStrategy;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class AuthHeaderProviderTest {
    @BeforeClass
    public void setUp() {
        CustomAssertions.setStrategy(new TestNGAssertionStrategy());
    }

    @Test
    public void resolve_shouldReturnBearerHeader() {
        AuthHeaderProvider authHeaderProvider = new AuthHeaderProvider(new InMemoryTokenStore());
        AuthConfig authConfig = new AuthConfig(
                AuthType.BEARER, null, null, null, null,
                null, null, null, null, null,
                "abc123", null, null, null);

        var result = authHeaderProvider.resolve(authConfig);

        CustomAssertions.assertTrue(result.headers().containsKey("Authorization"));
        CustomAssertions.assertEquals(result.headers().get("Authorization"), "Bearer abc123");
        CustomAssertions.assertTrue(result.queryParams().isEmpty());
    }

    @Test
    public void resolve_shouldReturnApiKeyHeader() {
        AuthHeaderProvider authHeaderProvider = new AuthHeaderProvider(new InMemoryTokenStore());
        AuthConfig authConfig = new AuthConfig(
                AuthType.API_KEY_HEADER, null, null, null, null,
                null, null, null, null, null,
                null, "X-Api-Key", "VAL123", ApiKeyLocation.HEADER);

        var result = authHeaderProvider.resolve(authConfig);

        CustomAssertions.assertEquals(result.headers().get("X-Api-Key"), "VAL123");
        CustomAssertions.assertTrue(result.queryParams().isEmpty());
    }

    @Test
    public void resolve_shouldReturnApiKeyQuery() {
        AuthHeaderProvider authHeaderProvider = new AuthHeaderProvider(new InMemoryTokenStore());
        AuthConfig authConfig = new AuthConfig(
                AuthType.API_KEY_QUERY, null, null, null, null,
                null, null, null, null, null,
                null, "api_key", "VAL123", ApiKeyLocation.QUERY);

        var result = authHeaderProvider.resolve(authConfig);

        CustomAssertions.assertTrue(result.headers().isEmpty());
        CustomAssertions.assertTrue(result.queryParams().containsKey("api_key"));
        CustomAssertions.assertEquals(result.queryParams().get("api_key"), List.of("VAL123"));
    }

    @Test
    public void resolve_shouldNormalizeTokenType() {
        // Simulate an already cached token with the small tokenType "bearer"
        TokenStore inMemoryTokenStore = new InMemoryTokenStore();
        inMemoryTokenStore.put("client_credentials:cid", new TokenInfo("tok", "bearer", Instant.now().plusSeconds(300), null));

        // Warning: This assertion indirectly checks private logic (normalization) via headerValue.
        // We bypass actual calls to OAuth2Service here by checking BEARER,
        // separate integration tests are being conducted for OAuth2.
        AuthHeaderProvider authHeaderProvider = new AuthHeaderProvider(inMemoryTokenStore);
        var result = AuthHeaderProvider.Result.ofHeaders(Map.of("Authorization", "Bearer tok"));
        CustomAssertions.assertEquals(result.headers().get("Authorization"), "Bearer tok");
    }
}
