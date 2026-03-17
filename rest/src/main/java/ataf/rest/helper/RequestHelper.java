package ataf.rest.helper;

import ataf.core.logging.ScenarioLogManager;
import ataf.rest.auth.AuthConfig;
import ataf.rest.auth.AuthConfigFactory;
import ataf.rest.auth.RestAssuredAuthFilter;
import ataf.rest.auth.TokenStore;
import ataf.rest.model.Operation;
import ataf.rest.model.RetryConfig;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static io.restassured.RestAssured.given;

/**
 * This class assists in building and sending HTTP requests, managing headers, authentication,
 * and body content. Supports modern authentication schemes (OAuth2, API Keys, Bearer Token)
 * and provides configurable timeouts, retries, and proxy support.
 *
 * @version 2.0 - Enhanced with OAuth2, API Key, and Bearer Token support
 */
public class RequestHelper {

    private static final Map<Long, RequestSpecification> REQUEST_SPECIFICATION_HOLDER_MAP = new ConcurrentHashMap<>();
    private static final Map<Long, String> BASE_URL_HOLDER_MAP = new ConcurrentHashMap<>();
    private static final Map<Long, String> ENDPOINT_HOLDER_MAP = new ConcurrentHashMap<>();
    private static final Map<Long, Integer> TIMEOUT_HOLDER_MAP = new ConcurrentHashMap<>();
    private static final Map<Long, AuthConfig> AUTH_CONFIG_HOLDER_MAP = new ConcurrentHashMap<>();
    private static final Map<Long, TokenStore> TOKEN_STORE_HOLDER_MAP = new ConcurrentHashMap<>();
    private static final Map<Long, RetryConfig> RETRY_CONFIG_HOLDER_MAP = new ConcurrentHashMap<>();

    // Configuration constants
    private static final int DEFAULT_CONNECT_TIMEOUT = 5000;
    private static final int DEFAULT_SOCKET_TIMEOUT = 5000;
    private static final boolean DEFAULT_FOLLOW_REDIRECTS = true;

    /**
     * Constructor that initializes a new RequestSpecification with default timeout configuration.
     */
    public RequestHelper() {
        initializeRequestSpec(DEFAULT_CONNECT_TIMEOUT, DEFAULT_SOCKET_TIMEOUT);
        TIMEOUT_HOLDER_MAP.put(Thread.currentThread().threadId(), 5000);
        RETRY_CONFIG_HOLDER_MAP.put(Thread.currentThread().threadId(), RetryConfig.disabled());
    }

    /**
     * Constructor with custom timeout configuration.
     *
     * @param connectTimeoutMs Connection timeout in milliseconds
     * @param socketTimeoutMs Socket timeout in milliseconds
     */
    public RequestHelper(int connectTimeoutMs, int socketTimeoutMs) {
        initializeRequestSpec(connectTimeoutMs, socketTimeoutMs);
    }

    /**
     * Builds a {@link RequestSpecification} with the provided timeout parameters
     * and all necessary configurations.
     *
     * <p>
     * <strong>Important:</strong> We intentionally do <em>not</em> provide a custom Apache
     * {@code HttpClient}
     * via {@code httpClientFactory}. REST Assured may internally cast the returned client to
     * {@code org.apache.http.impl.client.AbstractHttpClient}. Newer Apache clients (e.g.
     * {@code InternalHttpClient})
     * are not compatible with that cast and would cause a {@code GroovyCastException} at runtime.
     *
     * <p>
     * Instead, we set the Apache HttpClient timeouts via REST Assured's
     * {@link HttpClientConfig#setParam(String, Object)}
     * to stay compatible with REST Assured's internal client handling.
     * </p>
     *
     * @param connectTimeoutMs connection timeout in milliseconds
     * @param socketTimeoutMs socket timeout in milliseconds
     * @return configured {@link RequestSpecification}
     */
    private RequestSpecification buildRequestSpecification(int connectTimeoutMs, int socketTimeoutMs) {
        RequestSpecBuilder builder = new RequestSpecBuilder();

        HttpClientConfig httpClientConfig = HttpClientConfig.httpClientConfig()
                .setParam("http.connection.timeout", connectTimeoutMs)
                .setParam("http.socket.timeout", socketTimeoutMs)
                .setParam("http.connection-manager.timeout", (long) connectTimeoutMs);

        RestAssuredConfig restAssuredConfig = RestAssuredConfig.config()
                .httpClient(httpClientConfig)
                .redirect(io.restassured.config.RedirectConfig.redirectConfig()
                        .followRedirects(DEFAULT_FOLLOW_REDIRECTS));

        builder.setConfig(restAssuredConfig);
        return builder.build();
    }

    /**
     * Initializes the RequestSpecification with HTTP client configuration.
     *
     * @param connectTimeoutMs Connection timeout
     * @param socketTimeoutMs Socket timeout
     */
    private void initializeRequestSpec(int connectTimeoutMs, int socketTimeoutMs) {
        RequestSpecification requestSpecification = buildRequestSpecification(connectTimeoutMs, socketTimeoutMs);

        REQUEST_SPECIFICATION_HOLDER_MAP.put(Thread.currentThread().threadId(), requestSpecification);
        TIMEOUT_HOLDER_MAP.put(Thread.currentThread().threadId(), connectTimeoutMs);

        ScenarioLogManager.getLogger()
                .debug("RequestHelper initialized with {}ms connect timeout, {}ms socket timeout",
                        connectTimeoutMs, socketTimeoutMs);
    }

    /**
     * Sets the target endpoint for the request.
     *
     * @param targetEndpoint The endpoint to be set (e.g., "/users", "/api/v1/orders/{id}")
     */
    public void setEndpoint(String targetEndpoint) {
        ENDPOINT_HOLDER_MAP.put(Thread.currentThread().threadId(), targetEndpoint);
    }

    /**
     * Sets the base URL for the request.
     *
     * @param baseUrl The base URL (e.g., "https://api.example.com")
     */
    public void setBaseURL(String baseUrl) {
        BASE_URL_HOLDER_MAP.put(Thread.currentThread().threadId(), baseUrl);
    }

    /**
     * Sets the connection timeout in milliseconds. This reconfigures the HTTP client.
     *
     * @param connectTimeoutMs Connection timeout in milliseconds
     * @param socketTimeoutMs Socket timeout in milliseconds
     */
    public void setTimeouts(int connectTimeoutMs, int socketTimeoutMs) {
        TIMEOUT_HOLDER_MAP.put(Thread.currentThread().threadId(), connectTimeoutMs);
        reconfigureTimeout(connectTimeoutMs, socketTimeoutMs);
    }

    /**
     * Sets the content type for the request using a string value.
     *
     * @param contentType The content type as a string (e.g., "application/json")
     */
    public void setContentType(String contentType) {
        RequestSpecification requestSpecification = REQUEST_SPECIFICATION_HOLDER_MAP.get(Thread.currentThread().threadId());
        if (requestSpecification != null) {
            requestSpecification.contentType(contentType);
            REQUEST_SPECIFICATION_HOLDER_MAP.put(Thread.currentThread().threadId(), requestSpecification);
        }
    }

    /**
     * Sets the content type for the request using a {@link ContentType} enum.
     *
     * @param contentType The content type as a {@link ContentType}
     */
    public void setContentType(ContentType contentType) {
        RequestSpecification requestSpecification = REQUEST_SPECIFICATION_HOLDER_MAP.get(Thread.currentThread().threadId());
        if (requestSpecification != null) {
            requestSpecification.contentType(contentType);
            REQUEST_SPECIFICATION_HOLDER_MAP.put(Thread.currentThread().threadId(), requestSpecification);
        }
    }

    /**
     * Configures authentication for the request. Supports OAuth2, Bearer Token, API Key, and Basic
     * Auth.
     *
     * @param authConfig The authentication configuration
     * @param tokenStore Optional token store for managing tokens (e.g., for OAuth2 refresh)
     */
    public void setAuthentication(AuthConfig authConfig, TokenStore tokenStore) {
        if (authConfig != null) {
            AUTH_CONFIG_HOLDER_MAP.put(Thread.currentThread().threadId(), authConfig);
        }
        if (tokenStore != null) {
            TOKEN_STORE_HOLDER_MAP.put(Thread.currentThread().threadId(), tokenStore);
        }

        RequestSpecification requestSpecification = REQUEST_SPECIFICATION_HOLDER_MAP.get(Thread.currentThread().threadId());
        if (requestSpecification != null && authConfig != null) {
            // Register the filter that handles token injection and refresh
            RestAssuredAuthFilter authFilter = new RestAssuredAuthFilter(authConfig, tokenStore);
            requestSpecification.filter(authFilter);

            ScenarioLogManager.getLogger()
                    .debug("Authentication configured: {}", authConfig.type());
            REQUEST_SPECIFICATION_HOLDER_MAP.put(Thread.currentThread().threadId(), requestSpecification);
        }
    }

    /**
     * Legacy method for backward compatibility with BASIC_AUTH.
     * Consider using {@link #setAuthentication(AuthConfig, TokenStore)} instead.
     *
     * @param user Username
     * @param password Password
     * @deprecated Use {@link #setAuthentication(AuthConfig, TokenStore)} with
     *             {@link AuthConfigFactory#basicAuth(String, String)}
     */
    @Deprecated(since = "2.0", forRemoval = false)
    public void setBasicAuth(String user, String password) {
        AuthConfig basicAuthConfig = AuthConfigFactory.basicAuth(user, password);
        setAuthentication(basicAuthConfig, null);
        ScenarioLogManager.getLogger()
                .warn("Using deprecated setBasicAuth(). Please use setAuthentication() instead.");
    }

    /**
     * Configures OAuth2 authentication with client credentials flow.
     *
     * @param clientId OAuth2 client ID
     * @param clientSecret OAuth2 client secret
     * @param tokenEndpoint Token endpoint URL
     * @param tokenStore Token store for managing tokens
     */
    public void setOAuth2(String clientId, String clientSecret, String tokenEndpoint, TokenStore tokenStore) {
        AuthConfig oauth2Config = AuthConfigFactory.oauth2ClientCredentials(clientId, clientSecret, tokenEndpoint);
        setAuthentication(oauth2Config, tokenStore);
    }

    /**
     * Configures Bearer Token authentication.
     *
     * @param token The bearer token
     */
    public void setBearerToken(String token) {
        AuthConfig bearerConfig = AuthConfigFactory.bearerToken(token);
        setAuthentication(bearerConfig, null);
    }

    /**
     * Configures API Key authentication.
     *
     * @param apiKey The API key
     * @param location Where to place the key (HEADER, QUERY, COOKIE)
     * @param paramName Parameter name (e.g., "X-API-Key", "api_key")
     */
    public void setApiKey(String apiKey, String location, String paramName) {
        AuthConfig apiKeyConfig = AuthConfigFactory.apiKey(apiKey, location, paramName);
        setAuthentication(apiKeyConfig, null);
    }

    /**
     * Formats the request by applying previously set parameters, including headers, multi-part data,
     * and the request body. Authentication filters are applied automatically.
     *
     * @param operation The operation type for the request (GET, POST, PUT, DELETE, PATCH)
     */
    public void formatRequest(Operation operation) {
        RequestSpecification requestSpecification = REQUEST_SPECIFICATION_HOLDER_MAP.get(Thread.currentThread().threadId());
        if (requestSpecification == null) {
            return;
        }

        // Add headers
        requestSpecification.headers(HeaderHelper.getHeaderList());

        // Add multi-part data if present
        if (Objects.nonNull(BodyHelper.getMultiPartSpecification())) {
            requestSpecification.multiPart(BodyHelper.getMultiPartSpecification());
        }

        // Add body if present
        String bodyString = BodyHelper.getBodyString();
        if (Objects.nonNull(bodyString) && !bodyString.isEmpty()) {
            requestSpecification.body(bodyString);
        }

        // Set base URI
        String baseUrl = BASE_URL_HOLDER_MAP.get(Thread.currentThread().threadId());
        if (Objects.nonNull(baseUrl)) {
            requestSpecification.baseUri(baseUrl);
        }

        ScenarioLogManager.getLogger()
                .debug("Request formatted: HTTP {} {}",
                        operation,
                        ENDPOINT_HOLDER_MAP.get(Thread.currentThread().threadId()));
        REQUEST_SPECIFICATION_HOLDER_MAP.put(Thread.currentThread().threadId(), requestSpecification);
    }

    /**
     * Sends a request with the previously set parameters, optionally using the configured
     * retry policy.
     *
     * @param operation The operation type for the request (GET, POST, PUT, DELETE, PATCH)
     * @return The response of the request
     * @throws RuntimeException if the request specification or endpoint is null
     */
    public Response sendRequest(Operation operation) {
        RetryConfig retryConfig = RETRY_CONFIG_HOLDER_MAP.get(Thread.currentThread().threadId());

        if (retryConfig != null && retryConfig.isEnabled()) {
            return sendRequestWithRetry(operation, retryConfig);
        }

        return sendRequestOnce(operation);
    }

    /**
     * Sends a single HTTP request without any retry logic.
     *
     * @param operation The operation type for the request
     * @return The response of the request
     */
    private Response sendRequestOnce(Operation operation) {
        formatRequest(operation);

        RequestSpecification requestSpecification = REQUEST_SPECIFICATION_HOLDER_MAP.get(Thread.currentThread().threadId());
        String endpoint = ENDPOINT_HOLDER_MAP.get(Thread.currentThread().threadId());

        Objects.requireNonNull(requestSpecification, "RequestSpecification cannot be null");
        Objects.requireNonNull(endpoint, "Endpoint must be set before sending request");

        ScenarioLogManager.getLogger()
                .info("Sending {} request to endpoint: {}", operation, endpoint);

        Response response;
        try {
            response = switch (operation) {
                case POST -> given().spec(requestSpecification).when().post(endpoint);
                case PUT -> given().spec(requestSpecification).when().put(endpoint);
                case DELETE -> given().spec(requestSpecification).when().delete(endpoint);
                case PATCH -> given().spec(requestSpecification).when().patch(endpoint);
                default -> given().spec(requestSpecification).when().get(endpoint);
            };
        } catch (Exception e) {
            ScenarioLogManager.getLogger()
                    .error("Error sending {} request to {}: {}", operation, endpoint, e.getMessage(), e);
            throw new RuntimeException("Failed to send " + operation + " request", e);
        }

        ScenarioLogManager.getLogger()
                .debug("Response received with status code: {}", response.getStatusCode());

        return response;
    }

    /**
     * Sends a request with retries according to the given {@link RetryConfig}.
     *
     * <p>
     * Only idempotent operations are retried if {@link RetryConfig#isIdempotentMethodsOnly()}
     * is {@code true}. Currently, this includes GET, PUT and DELETE.
     * </p>
     *
     * @param operation HTTP operation to execute
     * @param retryConfig retry configuration
     * @return the last response received (successful or final failed attempt)
     */
    private Response sendRequestWithRetry(Operation operation, RetryConfig retryConfig) {
        if (retryConfig.isIdempotentMethodsOnly() && !isIdempotent(operation)) {
            ScenarioLogManager.getLogger()
                    .debug("Retry is enabled but operation {} is not idempotent. Sending once.", operation);
            return sendRequestOnce(operation);
        }

        int attempts = 0;
        RuntimeException lastException = null;
        Response lastResponse = null;

        while (attempts < retryConfig.getMaxAttempts()) {
            attempts++;

            try {
                lastResponse = sendRequestOnce(operation);

                int statusCode = lastResponse.getStatusCode();
                if (!retryConfig.getRetryStatusCodes().contains(statusCode)) {
                    // Successful or non-retriable status -> return immediately
                    return lastResponse;
                }

                ScenarioLogManager.getLogger()
                        .warn("Request attempt {} for {} returned retriable status {}. Retrying...",
                                attempts, operation, statusCode);
            } catch (RuntimeException ex) {
                lastException = ex;

                if (!retryConfig.isRetryOnNetworkErrors() || attempts >= retryConfig.getMaxAttempts()) {
                    ScenarioLogManager.getLogger()
                            .error("Request attempt {} for {} failed and will not be retried. Error: {}",
                                    attempts, operation, ex.getMessage(), ex);
                    throw ex;
                }

                ScenarioLogManager.getLogger()
                        .warn("Request attempt {} for {} failed with exception: {}. Retrying...",
                                attempts, operation, ex.getMessage());
            }

            if (attempts < retryConfig.getMaxAttempts()) {
                sleepWithBackoff(attempts, retryConfig);
            }
        }

        // maxAttempts reached: if we have a response with retriable status, return it,
        // otherwise rethrow the last exception
        if (lastResponse != null) {
            return lastResponse;
        }
        if (lastException != null) {
            throw lastException;
        }

        throw new IllegalStateException("Retry loop exited without response or exception for operation " + operation);
    }

    /**
     * Sleeps according to the configured exponential backoff strategy.
     *
     * @param attempt current attempt number (1-based)
     * @param retryConfig retry configuration
     */
    private void sleepWithBackoff(int attempt, RetryConfig retryConfig) {
        long delay = calculateBackoffDelay(attempt, retryConfig);
        ScenarioLogManager.getLogger()
                .debug("Sleeping for {} ms before next retry attempt {}", delay, attempt + 1);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            ScenarioLogManager.getLogger()
                    .warn("Retry backoff sleep interrupted. Continuing without further delay.");
        }
    }

    /**
     * Calculates the delay for the given attempt using exponential backoff.
     *
     * @param attempt current attempt number (1-based)
     * @param retryConfig retry configuration
     * @return delay in milliseconds
     */
    private long calculateBackoffDelay(int attempt, RetryConfig retryConfig) {
        if (attempt <= 1) {
            return retryConfig.getInitialDelayMs();
        }
        double factor = Math.pow(retryConfig.getBackoffMultiplier(), attempt - 1);
        long delay = (long) (retryConfig.getInitialDelayMs() * factor);
        return Math.min(delay, retryConfig.getMaxDelayMs());
    }

    /**
     * Returns the current {@link RequestSpecification}.
     *
     * @return The current request specification
     */
    public RequestSpecification getRequestSpecification() {
        return REQUEST_SPECIFICATION_HOLDER_MAP.get(Thread.currentThread().threadId());
    }

    /**
     * Sets a custom {@link RequestSpecification} for future requests.
     *
     * @param requestSpecification The request specification to set
     */
    public void setRequestSpecification(RequestSpecification requestSpecification) {
        REQUEST_SPECIFICATION_HOLDER_MAP.put(Thread.currentThread().threadId(), requestSpecification);
    }

    /**
     * Reconfigures the HTTP client timeout. Rebuilds the RequestSpecification with new timeout values.
     *
     * @param connectTimeoutMs Connection timeout in milliseconds
     * @param socketTimeoutMs Socket timeout in milliseconds
     */
    private void reconfigureTimeout(int connectTimeoutMs, int socketTimeoutMs) {
        RequestSpecification requestSpecification = buildRequestSpecification(connectTimeoutMs, socketTimeoutMs);

        REQUEST_SPECIFICATION_HOLDER_MAP.put(Thread.currentThread().threadId(), requestSpecification);

        ScenarioLogManager.getLogger()
                .debug("HTTP client timeout reconfigured to {}ms connect timeout, {}ms socket timeout", connectTimeoutMs, socketTimeoutMs);
    }

    /**
     * Clears all thread specific parameters. Must be called in test teardown to prevent memory leaks.
     */
    public void resetParameters() {
        AuthenticationHelper.resetParameters();
        HeaderHelper.resetParameters();
        BodyHelper.resetParameters();

        REQUEST_SPECIFICATION_HOLDER_MAP.remove(Thread.currentThread().threadId());
        BASE_URL_HOLDER_MAP.remove(Thread.currentThread().threadId());
        ENDPOINT_HOLDER_MAP.remove(Thread.currentThread().threadId());
        TIMEOUT_HOLDER_MAP.remove(Thread.currentThread().threadId());
        AUTH_CONFIG_HOLDER_MAP.remove(Thread.currentThread().threadId());
        TOKEN_STORE_HOLDER_MAP.remove(Thread.currentThread().threadId());

        ScenarioLogManager.getLogger().debug("RequestHelper parameters reset");
    }

    /**
     * Static utility method for one-off resets (supports legacy code).
     * Deprecated - use instance method instead.
     */
    @Deprecated(since = "2.0")
    public static void resetStaticParameters() {
        new RequestHelper().resetParameters();
    }

    /**
     * Returns the current authentication configuration.
     *
     * @return The current AuthConfig, or null if not set
     */
    public AuthConfig getAuthConfig() {
        return AUTH_CONFIG_HOLDER_MAP.get(Thread.currentThread().threadId());
    }

    /**
     * Returns the current token store.
     *
     * @return The current TokenStore, or null if not set
     */
    public TokenStore getTokenStore() {
        return TOKEN_STORE_HOLDER_MAP.get(Thread.currentThread().threadId());
    }

    /**
     * Returns the current timeout in milliseconds.
     *
     * @return Current timeout value
     */
    public int getTimeout() {
        return TIMEOUT_HOLDER_MAP.get(Thread.currentThread().threadId());
    }

    /**
     * Sets the retry configuration for this thread.
     *
     * <p>
     * If {@code null} is provided, retries are effectively disabled.
     * </p>
     *
     * @param retryConfig the retry configuration to use
     */
    public void setRetryConfig(RetryConfig retryConfig) {
        if (retryConfig == null) {
            RETRY_CONFIG_HOLDER_MAP.put(Thread.currentThread().threadId(), RetryConfig.disabled());
        } else {
            RETRY_CONFIG_HOLDER_MAP.put(Thread.currentThread().threadId(), retryConfig);
        }
    }

    /**
     * Returns the current retry configuration for this thread.
     *
     * @return the current retry configuration, never null
     */
    public RetryConfig getRetryConfig() {
        return RETRY_CONFIG_HOLDER_MAP.get(Thread.currentThread().threadId());
    }

    /**
     * Checks whether the given HTTP operation is considered idempotent.
     *
     * <p>
     * Note: Currently, only GET, PUT and DELETE are treated as idempotent.
     * HEAD and OPTIONS are not implemented in {@link Operation} and therefore
     * not considered here.
     * </p>
     *
     * @param operation the HTTP operation
     * @return {@code true} if the operation is considered idempotent
     */
    private boolean isIdempotent(Operation operation) {
        return operation == Operation.GET
                || operation == Operation.PUT
                || operation == Operation.DELETE;
    }
}
