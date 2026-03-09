package ataf.rest.model;

/**
 * Enum representing the types of HTTP operations that can be performed.
 *
 * <ul>
 * <li>{@link #POST} - Represents the HTTP POST method, used to submit data to be processed.</li>
 * <li>{@link #GET} - Represents the HTTP GET method, used to retrieve data from a server.</li>
 * <li>{@link #PUT} - Represents the HTTP PUT method, used to update or replace data on a
 * server.</li>
 * <li>{@link #DELETE} - Represents the HTTP DELETE method, used to delete data from a server.</li>
 * <li>{@link #PATCH} - Represents the HTTP PATCH method, used to apply partial modifications to
 * existing data on a server.</li>
 * <li>{@link #SEARCH} - Represents a custom or additional HTTP method for search operations.</li>
 * </ul>
 */
public enum Operation {
    /**
     * HTTP POST method, used to submit data to be processed by a server.
     */
    POST,

    /**
     * HTTP GET method, used to retrieve data from a server.
     */
    GET,

    /**
     * HTTP PUT method, used to update or replace existing data on a server.
     */
    PUT,

    /**
     * HTTP DELETE method, used to remove data from a server.
     */
    DELETE,

    /**
     * HTTP PATCH method, used to apply partial modifications to existing data on a server.
     */
    PATCH,

    /**
     * Custom or additional HTTP method for performing search operations.
     */
    SEARCH
}
