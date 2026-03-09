package ataf.rest.auth;

import java.net.URI;
import java.time.Duration;

/**
 * Factory class for creating AuthConfig instances.
 * Provides convenient static methods for constructing different authentication types.
 */
public class AuthConfigFactory {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    /**
     * Creates a Bearer Token authentication configuration.
     *
     * @param token The bearer token
     * @return AuthConfig with BEARER type
     */
    public static AuthConfig bearerToken(String token) {
        return new AuthConfig(
                AuthType.BEARER,
                null, null, null, null, null, null, null,
                DEFAULT_TIMEOUT, DEFAULT_TIMEOUT,
                token, null, null, null);
    }

    /**
     * Creates a Basic Auth configuration.
     *
     * @param username Username
     * @param password Password
     * @return AuthConfig with BASIC type
     */
    public static AuthConfig basicAuth(String username, String password) {
        return new AuthConfig(
                AuthType.BASIC,
                null, null, null, null,
                username, password, null,
                DEFAULT_TIMEOUT, DEFAULT_TIMEOUT,
                null, null, null, null);
    }

    /**
     * Creates an OAuth2 Client Credentials configuration.
     *
     * @param clientId Client ID
     * @param clientSecret Client Secret
     * @param tokenEndpoint Token endpoint URI
     * @return AuthConfig with OAUTH2_CLIENT_CREDENTIALS type
     */
    public static AuthConfig oauth2ClientCredentials(String clientId, String clientSecret, String tokenEndpoint) {
        return new AuthConfig(
                AuthType.OAUTH2_CLIENT_CREDENTIALS,
                URI.create(tokenEndpoint),
                clientId, clientSecret, null, null, null, null,
                DEFAULT_TIMEOUT, DEFAULT_TIMEOUT,
                null, null, null, null);
    }

    /**
     * Creates an API Key authentication configuration.
     *
     * @param apiKey The API key value
     * @param location Where to place the key (HEADER or QUERY)
     * @param paramName Parameter name (e.g., "X-API-Key")
     * @return AuthConfig with appropriate API_KEY type
     */
    public static AuthConfig apiKey(String apiKey, String location, String paramName) {
        ApiKeyLocation apiKeyLocation = "QUERY".equalsIgnoreCase(location)
                ? ApiKeyLocation.QUERY
                : ApiKeyLocation.HEADER;

        AuthType authType = ApiKeyLocation.QUERY.equals(apiKeyLocation)
                ? AuthType.API_KEY_QUERY
                : AuthType.API_KEY_HEADER;

        return new AuthConfig(
                authType,
                null, null, null, null, null, null, null,
                DEFAULT_TIMEOUT, DEFAULT_TIMEOUT,
                null, paramName, apiKey, apiKeyLocation);
    }

    /**
     * Creates an OAuth2 Password Grant configuration.
     *
     * @param clientId Client ID
     * @param clientSecret Client Secret
     * @param username Username
     * @param password Password
     * @param tokenEndpoint Token endpoint URI
     * @return AuthConfig with OAUTH2_PASSWORD type
     */
    public static AuthConfig oauth2Password(String clientId, String clientSecret, String username,
            String password, String tokenEndpoint) {
        return new AuthConfig(
                AuthType.OAUTH2_PASSWORD,
                URI.create(tokenEndpoint),
                clientId, clientSecret, null,
                username, password, null,
                DEFAULT_TIMEOUT, DEFAULT_TIMEOUT,
                null, null, null, null);
    }
}
