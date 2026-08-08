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

        CustomAssertions.assertEquals((Object) authConfig.type(), (Object) AuthType.BEARER);
        CustomAssertions.assertEquals((Object) authConfig.tokenEndpoint().toString(), (Object) "https://auth.example.com/token");
        CustomAssertions.assertEquals((Object) authConfig.clientId(), (Object) "cid");
        CustomAssertions.assertEquals((Object) authConfig.clientSecret(), (Object) "sec");
        CustomAssertions.assertEquals((Object) authConfig.scope(), (Object) "read write");
        CustomAssertions.assertEquals((Object) authConfig.username(), (Object) "user");
        CustomAssertions.assertEquals((Object) authConfig.password(), (Object) "pw");
        CustomAssertions.assertEquals((Object) authConfig.refreshToken(), (Object) "rTok");
        CustomAssertions.assertEquals((Object) authConfig.connectTimeout(), (Object) Duration.ofSeconds(5));
        CustomAssertions.assertEquals((Object) authConfig.readTimeout(), (Object) Duration.ofSeconds(30));
        CustomAssertions.assertEquals((Object) authConfig.bearerToken(), (Object) "bearer123");
        CustomAssertions.assertEquals((Object) authConfig.apiKeyName(), (Object) "api_key");
        CustomAssertions.assertEquals((Object) authConfig.apiKeyValue(), (Object) "apikeyvalue");
        CustomAssertions.assertEquals((Object) authConfig.apiKeyLocation(), (Object) ApiKeyLocation.HEADER);
    }
}
