package ataf.rest.auth;

import ataf.core.assertions.CustomAssertions;
import ataf.core.assertions.strategy.impl.TestNGAssertionStrategy;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;
import java.time.Instant;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class OAuth2ServiceWireMockTest {
    private WireMockServer wire;

    @BeforeClass
    public void setUp() {
        CustomAssertions.setStrategy(new TestNGAssertionStrategy());
    }

    @BeforeClass
    public void startWireMock() {
        wire = new WireMockServer(0); // random free port
        wire.start();
        configureFor("localhost", wire.port());
    }

    @BeforeMethod
    public void resetWireMockMappingsAndRequests() {
        wire.resetAll();
    }

    @AfterClass(alwaysRun = true)
    public void stopWireMock() {
        if (wire != null) wire.stop();
    }

    private String tokenUrl() {
        return "http://localhost:" + wire.port() + "/oauth/token";
    }

    @Test
    public void clientCredentials_shouldFetchAndCacheToken() {
        // Arrange
        stubFor(post(urlPathEqualTo("/oauth/token"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"access_token\":\"acc1\",\"token_type\":\"bearer\",\"expires_in\":300}")));

        TokenStore inMemoryTokenStore = new InMemoryTokenStore();
        OAuth2Service oAuth2Service = new OAuth2Service(inMemoryTokenStore);

        AuthConfig authConfig = new AuthConfig(
                AuthType.OAUTH2_CLIENT_CREDENTIALS,
                URI.create(tokenUrl()),
                "cid", "sec", "scope1",
                null, null, null,
                null, null,
                null, null, null, null);

        // Act
        TokenInfo tokenInfo1 = oAuth2Service.getOrFetchClientCredentials(authConfig);
        TokenInfo tokenInfo2 = oAuth2Service.getOrFetchClientCredentials(authConfig); // should reuse

        // Assert
        CustomAssertions.assertNotNull(tokenInfo1);
        CustomAssertions.assertEquals(tokenInfo1.accessToken(), "acc1");
        CustomAssertions.assertEquals(tokenInfo1.tokenType().toLowerCase(), "bearer");
        CustomAssertions.assertFalse(tokenInfo1.isExpiredOrStale());

        // second call should come from cache (no new stub call required)
        CustomAssertions.assertEquals(tokenInfo2.accessToken(), "acc1");
        CustomAssertions.assertFalse(tokenInfo2.isExpiredOrStale());

        // Verify exactly one call to the endpoint
        verify(1, postRequestedFor(urlPathEqualTo("/oauth/token")));
    }

    @Test
    public void passwordGrant_shouldFetchToken() {
        stubFor(post(urlPathEqualTo("/oauth/token"))
                .withRequestBody(containing("grant_type=password"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"access_token\":\"pTok\",\"token_type\":\"Bearer\",\"expires_in\":120,\"refresh_token\":\"r1\"}")));

        OAuth2Service oAuth2Service = new OAuth2Service(new InMemoryTokenStore());

        AuthConfig authConfig = new AuthConfig(
                AuthType.OAUTH2_PASSWORD,
                URI.create(tokenUrl()),
                "cid", "sec", "read",
                "alice", "secret", null,
                null, null,
                null, null, null, null);

        TokenInfo tokenInfo = oAuth2Service.getOrFetchPassword(authConfig);
        CustomAssertions.assertEquals(tokenInfo.accessToken(), "pTok");
        CustomAssertions.assertEquals(tokenInfo.refreshToken(), "r1");
        CustomAssertions.assertFalse(tokenInfo.isExpiredOrStale());
    }

    @Test
    public void refreshGrant_shouldRefreshToken() {
        stubFor(post(urlPathEqualTo("/oauth/token"))
                .withRequestBody(containing("grant_type=refresh_token"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"access_token\":\"newAcc\",\"token_type\":\"Bearer\",\"expires_in\":60}")));

        OAuth2Service oAuth2Service = new OAuth2Service(new InMemoryTokenStore());

        AuthConfig authConfig = new AuthConfig(
                AuthType.OAUTH2_REFRESH_TOKEN,
                URI.create(tokenUrl()),
                "cid", "sec", null,
                null, null, "rTok123",
                null, null,
                null, null, null, null);

        TokenInfo tokenInfo = oAuth2Service.refresh(authConfig);
        CustomAssertions.assertEquals(tokenInfo.accessToken(), "newAcc");
        CustomAssertions.assertFalse(tokenInfo.isExpiredOrStale());
        CustomAssertions.assertTrue(tokenInfo.expiresAt().isAfter(Instant.now()));
    }

    @Test(expectedExceptions = AssertionError.class)
    public void tokenEndpoint_non200_shouldAssert() {
        stubFor(post(urlPathEqualTo("/oauth/token"))
                .willReturn(aResponse().withStatus(400).withBody("{\"error\":\"invalid_request\"}")));

        OAuth2Service oAuth2Service = new OAuth2Service(new InMemoryTokenStore());

        AuthConfig authConfig = new AuthConfig(
                AuthType.OAUTH2_CLIENT_CREDENTIALS,
                URI.create(tokenUrl()),
                "cid", "sec", null,
                null, null, null,
                null, null,
                null, null, null, null);

        // Should trigger CustomAssertions.assertTrue(status==200) in fetch logic
        oAuth2Service.getOrFetchClientCredentials(authConfig);
    }
}
