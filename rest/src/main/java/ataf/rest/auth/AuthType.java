package ataf.rest.auth;

/**
 * Enumeration defining the supported authentication mechanisms for REST requests.
 */
public enum AuthType {
    /** No authentication (anonymous request). */
    NONE,

    /** HTTP Basic Authentication (username + password in Base64). */
    BASIC,

    /** Static Bearer token (provided in configuration). */
    BEARER,

    /** OAuth2 using Client Credentials Grant (machine-to-machine). */
    OAUTH2_CLIENT_CREDENTIALS,

    /** OAuth2 using Resource Owner Password Grant (username + password). */
    OAUTH2_PASSWORD,

    /** OAuth2 using Refresh Token Grant. */
    OAUTH2_REFRESH_TOKEN,

    /** API key provided via HTTP header. */
    API_KEY_HEADER,

    /** API key provided via query parameter in URL. */
    API_KEY_QUERY
}
