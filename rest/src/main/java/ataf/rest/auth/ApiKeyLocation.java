package ataf.rest.auth;

/**
 * Defines where an API key should be injected into a request.
 */
public enum ApiKeyLocation {
    /** Adds the API key as an HTTP header. */
    HEADER,
    /** Appends the API key as a query parameter to the request URL. */
    QUERY
}
