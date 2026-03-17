package ataf.rest.auth;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Resolves authentication data (headers and query parameters) for a request
 * according to the provided {@link AuthConfig}.
 * <p>
 * This provider does <b>not</b> alter the request URI. It only returns
 * headers and query parameters to be applied by the caller (e.g. a RestAssured filter).
 */
public class AuthHeaderProvider {

    private final OAuth2Service OAUTH2;

    /**
     * Creates a new provider with the given token store.
     *
     * @param tokenStore Token persistence implementation (e.g. {@link InMemoryTokenStore}).
     */
    public AuthHeaderProvider(TokenStore tokenStore) {
        this.OAUTH2 = new OAuth2Service(tokenStore);
    }

    /**
     * Resolves headers and query parameters to satisfy the configured auth type.
     *
     * @param authConfig Authentication configuration (must not be null).
     * @return A {@link Result} containing headers and query parameters to apply.
     */
    public Result resolve(AuthConfig authConfig) {
        if (authConfig == null || authConfig.type() == null || authConfig.type() == AuthType.NONE) {
            return Result.empty();
        }

        return switch (authConfig.type()) {
            case BEARER -> Result.ofHeaders(Map.of("Authorization", "Bearer " + authConfig.bearerToken()));

            case OAUTH2_CLIENT_CREDENTIALS -> {
                TokenInfo tokenInfo = OAUTH2.getOrFetchClientCredentials(authConfig);
                yield Result.ofHeaders(Map.of("Authorization", headerValue(tokenInfo)));
            }

            case OAUTH2_PASSWORD -> {
                TokenInfo tokenInfo = OAUTH2.getOrFetchPassword(authConfig);
                yield Result.ofHeaders(Map.of("Authorization", headerValue(tokenInfo)));
            }

            case OAUTH2_REFRESH_TOKEN -> {
                TokenInfo tokenInfo = OAUTH2.refresh(authConfig);
                yield Result.ofHeaders(Map.of("Authorization", headerValue(tokenInfo)));
            }

            case API_KEY_HEADER -> Result.ofHeaders(Map.of(authConfig.apiKeyName(), authConfig.apiKeyValue()));

            case API_KEY_QUERY -> Result.ofQueryParams(Map.of(
                    authConfig.apiKeyName(), List.of(authConfig.apiKeyValue())));

            default -> Result.empty();
        };
    }

    private static String headerValue(TokenInfo tokenInfo) {
        String type = (tokenInfo.tokenType() == null || tokenInfo.tokenType().isBlank()) ? "Bearer" : tokenInfo.tokenType();
        // Normalize first letter upper-case (Bearer, not bearer)
        String normalized = type.substring(0, 1).toUpperCase() + type.substring(1);
        return normalized + " " + tokenInfo.accessToken();
    }

    /**
     * Immutable result wrapper containing authentication data (headers and query parameters)
     * to be applied to an outgoing REST request.
     * <p>
     * This record is returned by {@link AuthHeaderProvider#resolve(AuthConfig)} and encapsulates
     * two collections:
     * <ul>
     * <li>A map of HTTP headers (e.g. {@code Authorization}, {@code X-Api-Key}).</li>
     * <li>A map of query parameters that should be appended to the request URI.</li>
     * </ul>
     * Both maps are guaranteed to be non-null and may be empty.
     *
     * <p>
     * Instances of this record are immutable and thread-safe.
     *
     * @param headers a map containing header names and corresponding single values;
     *            represents key–value pairs to be added to the HTTP request header section
     * @param queryParams a map containing query parameter names and their associated value lists;
     *            represents parameters to be added to the request URI
     */
    public record Result(Map<String, String> headers, Map<String, List<String>> queryParams) {

        /**
         * Creates an empty {@link Result} with no headers and no query parameters.
         *
         * @return an empty {@link Result} instance where both {@code headers} and {@code queryParams} are
         *         empty maps
         */
        public static Result empty() {
            return new Result(Collections.emptyMap(), Collections.emptyMap());
        }

        /**
         * Creates a {@link Result} containing only HTTP headers.
         * The {@code queryParams} map will be empty.
         *
         * @param headers a map of header names to header values; must not be {@code null}
         * @return a {@link Result} containing the specified headers and no query parameters
         */
        public static Result ofHeaders(Map<String, String> headers) {
            return new Result(headers, Collections.emptyMap());
        }

        /**
         * Creates a {@link Result} containing only query parameters.
         * The {@code headers} map will be empty.
         *
         * @param queryParams a map of query parameter names to lists of values; must not be {@code null}
         * @return a {@link Result} containing the specified query parameters and no headers
         */
        public static Result ofQueryParams(Map<String, List<String>> queryParams) {
            return new Result(Collections.emptyMap(), queryParams);
        }
    }

}
