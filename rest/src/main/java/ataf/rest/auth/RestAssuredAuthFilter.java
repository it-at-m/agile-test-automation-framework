package ataf.rest.auth;

import ataf.core.logging.ScenarioLogManager;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/**
 * RestAssured filter that injects authentication data (headers and/or query parameters)
 * into the request without changing the request URI.
 * <p>
 * Supported auth types:
 * <ul>
 * <li>{@link AuthType#BEARER}</li>
 * <li>{@link AuthType#OAUTH2_CLIENT_CREDENTIALS}</li>
 * <li>{@link AuthType#OAUTH2_PASSWORD}</li>
 * <li>{@link AuthType#OAUTH2_REFRESH_TOKEN}</li>
 * <li>{@link AuthType#API_KEY_HEADER}</li>
 * <li>{@link AuthType#API_KEY_QUERY}</li>
 * </ul>
 * <p>
 * Apply this filter via:
 *
 * <pre>{@code
 * given()
 *         .filter(new RestAssuredAuthFilter(authConfig))
 *         .when()
 *         .get("/api/users")
 *         .then()
 *         .statusCode(200);
 * }</pre>
 */
public class RestAssuredAuthFilter implements Filter {

    private final AuthHeaderProvider PROVIDER;
    private final AuthConfig AUTH_CONFIG;

    /**
     * Constructs a filter using the provided authentication configuration.
     *
     * @param authConfig Authentication configuration (non-null).
     * @param tokenStore Token store for managing tokens (can be null).
     */
    public RestAssuredAuthFilter(AuthConfig authConfig, TokenStore tokenStore) {
        this.AUTH_CONFIG = authConfig;
        final TokenStore TOKEN_STORE = tokenStore != null ? tokenStore : new InMemoryTokenStore();
        this.PROVIDER = new AuthHeaderProvider(TOKEN_STORE);
    }

    /**
     * Applies headers and query parameters based on {@link AuthConfig}.
     *
     * @param requestSpecification Modifiable request specification.
     * @param responseSpecification Filterable response specification (unused).
     * @param filterContext Execution context.
     * @return The response produced by the next filter or by the HTTP call.
     */
    @Override
    public Response filter(FilterableRequestSpecification requestSpecification, FilterableResponseSpecification responseSpecification,
            FilterContext filterContext) {
        var result = PROVIDER.resolve(AUTH_CONFIG);

        // Headers
        result.headers().forEach(requestSpecification::header);

        // Query parameters
        result.queryParams().forEach((name, values) -> {
            if (values == null || values.isEmpty()) return;
            // add each value; RestAssured will handle multi-value params
            for (String value : values) {
                requestSpecification.queryParam(name, value);
            }
        });

        ScenarioLogManager.getLogger().info("Applied auth: {} (headers={}, query={})", AUTH_CONFIG.type(), result.headers().keySet(),
                result.queryParams().keySet());
        return filterContext.next(requestSpecification, responseSpecification);
    }
}
