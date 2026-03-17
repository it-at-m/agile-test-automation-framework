package ataf.rest.auth;

import ataf.core.assertions.CustomAssertions;
import ataf.core.assertions.strategy.impl.TestNGAssertionStrategy;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URI;
import java.time.Duration;

public class AuthConfigTest {
    @BeforeClass
    public void setUp() {
        CustomAssertions.setStrategy(new TestNGAssertionStrategy());
    }

    @Test
    public void record_shouldStoreValues() {
        AuthConfig authConfig = new AuthConfig(
                AuthType.BEARER,
                URI.create("https://auth.example.com/token"),
                "cid", "sec", "read write",
                "user", "pw", "rTok",
                Duration.ofSeconds(5), Duration.ofSeconds(30),
                "bearer123",
                "api_key", "apikeyvalue",
                ApiKeyLocation.HEADER);

        CustomAssertions.assertEquals(authConfig.type(), AuthType.BEARER);
        CustomAssertions.assertEquals(authConfig.tokenEndpoint().toString(), "https://auth.example.com/token");
        CustomAssertions.assertEquals(authConfig.clientId(), "cid");
        CustomAssertions.assertEquals(authConfig.clientSecret(), "sec");
        CustomAssertions.assertEquals(authConfig.scope(), "read write");
        CustomAssertions.assertEquals(authConfig.username(), "user");
        CustomAssertions.assertEquals(authConfig.password(), "pw");
        CustomAssertions.assertEquals(authConfig.refreshToken(), "rTok");
        CustomAssertions.assertEquals(authConfig.connectTimeout(), Duration.ofSeconds(5));
        CustomAssertions.assertEquals(authConfig.readTimeout(), Duration.ofSeconds(30));
        CustomAssertions.assertEquals(authConfig.bearerToken(), "bearer123");
        CustomAssertions.assertEquals(authConfig.apiKeyName(), "api_key");
        CustomAssertions.assertEquals(authConfig.apiKeyValue(), "apikeyvalue");
        CustomAssertions.assertEquals(authConfig.apiKeyLocation(), ApiKeyLocation.HEADER);
    }
}
