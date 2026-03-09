package ataf.rest.auth;

import java.net.URI;
import java.time.Duration;

/**
 * Immutable configuration object for all supported authentication types.
 * <p>
 * This record encapsulates all parameters required for various authentication
 * mechanisms such as OAuth2, Bearer token, and API key authentication.
 * It is designed to be thread-safe and easily serializable.
 * <p>
 * The record values should typically be constructed once per test or environment
 * and reused by authentication helpers such as {@link OAuth2Service} or
 * {@link RestAssuredAuthFilter}.
 *
 * @param type the {@link AuthType} defining which authentication mechanism to apply
 * @param tokenEndpoint the OAuth2 token endpoint URI, required for OAuth2-based authentication
 *            types
 * @param clientId the OAuth2 client identifier used in client credentials or password grant flows
 * @param clientSecret the client secret corresponding to the {@code clientId}
 * @param scope an optional space-separated list of OAuth2 scopes to request
 * @param username the username for OAuth2 password grant flow
 * @param password the password for OAuth2 password grant flow
 * @param refreshToken the refresh token used to obtain a new access token via refresh grant
 * @param connectTimeout the connection timeout duration for token requests
 * @param readTimeout the read timeout duration for token requests
 * @param bearerToken the static bearer token used when {@link AuthType#BEARER} is selected
 * @param apiKeyName the name of the API key header or query parameter
 * @param apiKeyValue the value of the API key
 * @param apiKeyLocation the {@link ApiKeyLocation} indicating whether the API key should be sent
 *            in a header or as a query parameter
 */
public record AuthConfig(
        AuthType type,
        URI tokenEndpoint,
        String clientId,
        String clientSecret,
        String scope,
        String username,
        String password,
        String refreshToken,
        Duration connectTimeout,
        Duration readTimeout,
        String bearerToken,
        String apiKeyName,
        String apiKeyValue,
        ApiKeyLocation apiKeyLocation) {
}
