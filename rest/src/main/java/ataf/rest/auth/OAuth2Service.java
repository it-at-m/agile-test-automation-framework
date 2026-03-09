package ataf.rest.auth;

import ataf.core.assertions.CustomAssertions;
import ataf.core.logging.ScenarioLogManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides OAuth2 token acquisition and refresh logic using RestAssured.
 */
public class OAuth2Service {

    private final TokenStore TOKEN_STORE;

    /**
     * Constructs an OAuth2 service using a given token store.
     *
     * @param tokenStore Token persistence provider.
     */
    public OAuth2Service(TokenStore tokenStore) {
        this.TOKEN_STORE = tokenStore;
    }

    private static boolean notBlank(String string) {
        return string != null && !string.isBlank();
    }

    /**
     * Fetches or reuses a Client Credentials token.
     *
     * @param authConfig The authentication configuration.
     * @return Valid {@link TokenInfo}.
     */
    public TokenInfo getOrFetchClientCredentials(AuthConfig authConfig) {
        final String key = "client_credentials:" + authConfig.clientId();
        return TOKEN_STORE.get(key)
                .filter(t -> !t.isExpiredOrStale())
                .orElseGet(() -> {
                    ScenarioLogManager.getLogger().info("Fetching new OAuth2 client credentials token");
                    Map<String, String> params = new HashMap<>();
                    params.put("grant_type", "client_credentials");
                    params.put("client_id", authConfig.clientId());
                    params.put("client_secret", authConfig.clientSecret());
                    params.put("scope", authConfig.scope());

                    TokenInfo tokenInfo = fetchToken(authConfig, params);
                    TOKEN_STORE.put(key, tokenInfo);
                    return tokenInfo;
                });
    }

    /**
     * Fetches or reuses a Password Grant token.
     *
     * @param authConfig The authentication configuration.
     * @return Valid {@link TokenInfo}.
     */
    public TokenInfo getOrFetchPassword(AuthConfig authConfig) {
        final String key = "password:" + authConfig.clientId() + ":" + authConfig.username();
        return TOKEN_STORE.get(key)
                .filter(t -> !t.isExpiredOrStale())
                .orElseGet(() -> {
                    ScenarioLogManager.getLogger().info("Fetching new OAuth2 password token for {}", authConfig.username());
                    Map<String, String> params = new HashMap<>();
                    params.put("grant_type", "password");
                    params.put("client_id", authConfig.clientId());
                    params.put("client_secret", authConfig.clientSecret());
                    params.put("username", authConfig.username());
                    params.put("password", authConfig.password());
                    params.put("scope", authConfig.scope());

                    TokenInfo tokenInfo = fetchToken(authConfig, params);
                    TOKEN_STORE.put(key, tokenInfo);
                    return tokenInfo;
                });
    }

    /**
     * Performs an OAuth2 token refresh.
     *
     * @param authConfig The authentication configuration.
     * @return Refreshed {@link TokenInfo}.
     */
    public TokenInfo refresh(AuthConfig authConfig) {
        final String key = "refresh:" + authConfig.clientId();
        ScenarioLogManager.getLogger().info("Refreshing OAuth2 token");
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "refresh_token");
        params.put("client_id", authConfig.clientId());
        params.put("client_secret", authConfig.clientSecret());
        params.put("refresh_token", authConfig.refreshToken());

        TokenInfo tokenInfo = fetchToken(authConfig, params);
        TOKEN_STORE.put(key, tokenInfo);
        return tokenInfo;
    }

    /**
     * Executes the token endpoint using RestAssured.
     */
    private TokenInfo fetchToken(AuthConfig authConfig, Map<String, String> params) {
        Response response = RestAssured
                .given()
                .contentType("application/x-www-form-urlencoded")
                .formParams(params)
                .post(authConfig.tokenEndpoint());

        ScenarioLogManager.getLogger().info("OAuth2 token endpoint responded with status: {}", response.statusCode());
        CustomAssertions.assertTrue(response.statusCode() == 200, "Unexpected OAuth2 response: " + response.asString());

        String access = response.jsonPath().getString("access_token");
        String type = response.jsonPath().getString("token_type");
        String refresh = response.jsonPath().getString("refresh_token");
        long expiresIn = response.jsonPath().getLong("expires_in");

        CustomAssertions.assertNotNull(access, "OAuth2: Missing access_token");

        return new TokenInfo(
                access,
                type != null ? type : "Bearer",
                Instant.now().plusSeconds(expiresIn > 0L ? expiresIn : 300L),
                refresh);
    }
}
