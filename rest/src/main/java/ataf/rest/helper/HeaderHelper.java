package ataf.rest.helper;

import ataf.core.logging.ScenarioLogManager;
import io.cucumber.datatable.DataTable;
import io.restassured.http.Header;
import io.restassured.http.Headers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class provides utility methods for managing HTTP headers in the context of API requests.
 *
 * <p>
 * Implementation note: headers are stored in a {@link ConcurrentHashMap} so that parallel
 * test execution does not lead to cross-thread contamination of header state.
 * </p>
 */
public class HeaderHelper {

    private HeaderHelper() {
        // Private constructor to prevent instantiation
    }

    private static final Map<Long, Map<String, String>> HEADER_HOLDER_MAP = new ConcurrentHashMap<>();

    /**
     * Adds a header to the internal list of headers. If a header with the same name already exists, its
     * value is overwritten.
     *
     * @param headerName The name of the header to be added
     * @param headerValue The value of the header to be added
     * @return The updated list of headers including the newly added header
     */
    public static Map<String, String> addHeader(String headerName, String headerValue) {
        Map<String, String> headers = HEADER_HOLDER_MAP.computeIfAbsent(Thread.currentThread().threadId(), k -> new HashMap<>());
        findHeaderAndOverwrite(headers, headerName, headerValue);
        HEADER_HOLDER_MAP.put(Thread.currentThread().threadId(), headers);
        return headers;
    }

    /**
     * Similar to {@link #addHeader(String, String)}, but allows adding multiple headers at once.
     *
     * @param headers A map containing header names and their values
     * @return The updated list of headers including the newly added headers
     */
    public static Map<String, String> addMultipleHeaders(Map<String, String> headers) {
        Map<String, String> currentHeaders = HEADER_HOLDER_MAP.computeIfAbsent(Thread.currentThread().threadId(), k -> new HashMap<>());
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            findHeaderAndOverwrite(currentHeaders, entry.getKey(), entry.getValue());
        }
        HEADER_HOLDER_MAP.put(Thread.currentThread().threadId(), headers);
        return currentHeaders;
    }

    /**
     * Adds headers from a {@link DataTable}. Useful for defining headers through Gherkin tables.
     *
     * <div>
     * The table may either be a "simple" key-value table:
     *
     * <pre>
     *   | Content-Type | application/json |
     *   | Accept       | application/json |
     * </pre>
     *
     * or use a header row like:
     *
     * <pre>
     *   | Attribut     | Wert             |
     *   | Content-Type | application/json |
     * </pre>
     *
     * In the latter case, the entry "Attribut" - "Wert" is removed before adding headers.
     * </div>
     *
     * @param headerInformation A DataTable containing header information
     */
    public static void addHeadersFromDataTable(DataTable headerInformation) {
        Map<String, String> dt = headerInformation.asMap(String.class, String.class);

        // Remove common header-row labels if present
        dt.remove("Attribut");
        dt.remove("Header");
        dt.remove("Wert");

        addMultipleHeaders(dt);
    }

    /**
     * Legacy instance method kept for backward compatibility.
     * Delegates to {@link #addHeadersFromDataTable(DataTable)}.
     *
     * @param headerInformation A DataTable containing header information
     * @deprecated Use {@link #addHeadersFromDataTable(DataTable)} instead.
     */
    @Deprecated
    public void headerInformationSetTable(DataTable headerInformation) {
        addHeadersFromDataTable(headerInformation);
    }

    /**
     * Searches for a header by name, and if found, replaces its value. If not found, adds it as a new
     * header.
     *
     * @param headers The header map to update
     * @param headerName The name of the header to find and update
     * @param headerValue The new value of the header
     */
    private static void findHeaderAndOverwrite(Map<String, String> headers,
            String headerName,
            String headerValue) {
        ScenarioLogManager.getLogger().debug("Adding header: {}={}", headerName, headerValue);
        headers.put(headerName, headerValue);
    }

    /**
     * Returns the current list of headers as a map.
     *
     * @return A map of header names and values
     */
    public static Map<String, String> getHeaderList() {
        return HEADER_HOLDER_MAP.computeIfAbsent(Thread.currentThread().threadId(), k -> new HashMap<>());
    }

    /**
     * Returns the current headers as a list of {@link Header} objects.
     *
     * @return A list of headers
     */
    public static List<Header> getHeadersAsList() {
        List<Header> headers = new ArrayList<>();
        for (Map.Entry<String, String> entry : HEADER_HOLDER_MAP.get(Thread.currentThread().threadId()).entrySet()) {
            headers.add(new Header(entry.getKey(), entry.getValue()));
        }
        return headers;
    }

    /**
     * Returns the current headers as a {@link Headers} object.
     *
     * @return A Headers object
     */
    public static Headers getHeaders() {
        return new Headers(getHeadersAsList());
    }

    /**
     * Resets the list of headers to an empty state for the current thread.
     * This should be called during scenario teardown to avoid leaking header state
     * between tests.
     */
    public static void resetParameters() {
        Map<String, String> headerHolderMap = HEADER_HOLDER_MAP.get(Thread.currentThread().threadId());
        if (headerHolderMap != null) {
            headerHolderMap.clear();
        }
        HEADER_HOLDER_MAP.remove(Thread.currentThread().threadId());
    }
}
