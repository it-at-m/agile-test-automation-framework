package ataf.rest.steps;

import ataf.core.logging.ScenarioLogManager;
import ataf.rest.frameworkapi.BaseRequest;
import ataf.rest.helper.BodyHelper;
import ataf.rest.helper.HeaderHelper;
import ataf.rest.helper.RequestHelper;
import ataf.rest.model.Operation;
import ataf.rest.model.RetryConfig;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Gegebensei;
import io.cucumber.java.de.Und;
import io.cucumber.java.de.Wenn;
import org.hamcrest.Matchers;

import java.util.Map;

/**
 * This class provides step definitions for REST API interactions, using {@link BaseRequest} for
 * making requests.
 *
 * <p>
 * The steps are intended to be used with Cucumber (language: {@code de}) and provide a consistent
 * and fluent way to configure requests (base URL, headers, payload), send HTTP requests
 * (GET/POST/PUT/PATCH/DELETE) and verify responses (status code, headers, JSON paths, body
 * content).
 * </p>
 *
 * <div>
 * Typical usage:
 *
 * <pre>
 *   Gegeben sei die Basis-URL ist "https://api.example.com"
 *   Und folgende Header gesetzt sind:
 *     | Content-Type | application/json |
 *   Wenn ich eine POST-Anfrage nach "/users" mit folgendem Body sende:
 *     """
 *     { "name": "Alice" }
 *     """
 *   Dann hat die Antwort den Status 201
 *   Und der JSON-Pfad "name" ist gleich "Alice"
 * </pre>
 *
 * </div>
 */
public class BaseRestSteps {

    private BaseRequest baseRequest;

    /**
     * Constructor that initializes a new {@link BaseRequest} instance.
     */
    public BaseRestSteps() {
        baseRequest = new BaseRequest();
    }

    /**
     * Returns the current {@link BaseRequest} instance used for making API requests.
     *
     * @return The current BaseRequest instance
     */
    public BaseRequest getBaseRequest() {
        return baseRequest;
    }

    /**
     * Sets a new {@link BaseRequest} instance for making API requests and logs this action.
     *
     * @param baseRequest The new BaseRequest instance to set
     */
    public void setBaseRequest(BaseRequest baseRequest) {
        this.baseRequest = baseRequest;
        ScenarioLogManager.getLogger().info("BaseRequest set");
    }

    /**
     * Sets the base URI for all subsequent REST requests in the current scenario.
     *
     * @param baseUrl the base URI to use (e.g. "https://api.example.com")
     */
    @Gegebensei("die Basis-URL ist {string}")
    public void gegeben_sei_die_basis_url_ist_string(String baseUrl) {
        ScenarioLogManager.getLogger().info("Setting base URL to {}", baseUrl);
        RequestHelper helper = baseRequest.getRequestHelper();
        helper.setBaseURL(baseUrl);
    }

    /**
     * Sets multiple HTTP headers from a Cucumber {@link DataTable}.
     * <div>
     * The {@link DataTable} is expected to contain key-value pairs with header name and value:
     *
     * <pre>
     *   | Content-Type | application/json |
     *   | Accept       | application/json |
     * </pre>
     *
     * </div>
     *
     * @param dataTable the table of header key-value pairs
     */
    @Und("folgende Header gesetzt sind:")
    public void und_folgende_header_gesetzt_sind_data_table(DataTable dataTable) {
        ScenarioLogManager.getLogger().info("Adding headers from DataTable");
        HeaderHelper.addHeadersFromDataTable(dataTable);
    }

    /**
     * Sets a single HTTP header.
     *
     * @param headerName header name
     * @param headerValue header value
     */
    @Und("der Header {string} mit dem Wert {string} gesetzt ist")
    public void und_der_header_string_mit_dem_wert_string_gesetzt_ist(String headerName, String headerValue) {
        ScenarioLogManager.getLogger().info("Adding single header: {}={}", headerName, headerValue);
        HeaderHelper.addHeader(headerName, headerValue);
    }

    /**
     * Sends a HTTP GET request to the given endpoint (relative to the configured base URL).
     *
     * @param endpoint the endpoint path (e.g. "/users/42")
     */
    @Wenn("ich eine GET-Anfrage nach {string} sende")
    public void wenn_ich_eine_GET_anfrage_nach_string_sende(String endpoint) {
        ScenarioLogManager.getLogger().info("Sending GET request to endpoint {}", endpoint);
        baseRequest.sendRequest(endpoint, Operation.GET);
    }

    /**
     * Sends a HTTP DELETE request to the given endpoint (relative to the configured base URL).
     *
     * @param endpoint the endpoint path (e.g. "/users/42")
     */
    @Wenn("ich eine DELETE-Anfrage nach {string} sende")
    public void wenn_ich_eine_DELETE_anfrage_nach_string_sende(String endpoint) {
        ScenarioLogManager.getLogger().info("Sending DELETE request to endpoint {}", endpoint);
        baseRequest.sendRequest(endpoint, Operation.DELETE);
    }

    /**
     * Sends a HTTP POST request with the given body as a raw string.
     * <div>
     * The body is typically provided as a Cucumber DocString:
     *
     * <pre>
     *   Wenn ich eine POST-Anfrage nach "/users" mit folgendem Body sende:
     *     """
     *     { "name": "Alice" }
     *     """
     * </pre>
     *
     * </div>
     *
     * @param endpoint the endpoint path
     * @param body the raw request body
     */
    @Wenn("ich eine POST-Anfrage nach {string} mit folgendem Body sende:")
    public void wenn_ich_eine_POST_anfrage_nach_string_mit_folgendem_body_sende(String endpoint, String body) {
        ScenarioLogManager.getLogger().info("Sending POST request to {} with raw body", endpoint);
        BodyHelper.setBodyFromString(body);
        baseRequest.sendRequest(endpoint, Operation.POST);
    }

    /**
     * Sends a HTTP PUT request with the given body as a raw string.
     * <div>
     * The body is typically provided as a Cucumber DocString:
     *
     * <pre>
     *   Wenn ich eine PUT-Anfrage nach "/users" mit folgendem Body sende:
     *     """
     *     { "name": "Alice" }
     *     """
     * </pre>
     *
     * </div>
     *
     * @param endpoint the endpoint path
     * @param body the raw request body
     */
    @Wenn("ich eine PUT-Anfrage nach {string} mit folgendem Body sende:")
    public void wenn_ich_eine_PUT_anfrage_nach_string_mit_folgendem_body_sende(String endpoint, String body) {
        ScenarioLogManager.getLogger().info("Sending PUT request to {} with raw body", endpoint);
        BodyHelper.setBodyFromString(body);
        baseRequest.sendRequest(endpoint, Operation.PUT);
    }

    /**
     * Sends a HTTP PATCH request with the given body as a raw string.
     * <div>
     * The body is typically provided as a Cucumber DocString:
     *
     * <pre>
     *   Wenn ich eine PATCH-Anfrage nach "/users" mit folgendem Body sende:
     *     """
     *     { "name": "Alice" }
     *     """
     * </pre>
     *
     * </div>
     *
     * @param endpoint the endpoint path
     * @param body the raw request body
     */
    @Wenn("ich eine PATCH-Anfrage nach {string} mit folgendem Body sende:")
    public void wenn_ich_eine_PATCH_anfrage_nach_string_mit_folgendem_body_sende(String endpoint, String body) {
        ScenarioLogManager.getLogger().info("Sending PATCH request to {} with raw body", endpoint);
        BodyHelper.setBodyFromString(body);
        baseRequest.sendRequest(endpoint, Operation.PATCH);
    }

    /**
     * Sends a HTTP POST request with the request body loaded from a file.
     * <p>
     * The file path is interpreted relative to the project's resource root or a dedicated
     * test-data directory, depending on the implementation of {@link BodyHelper}.
     * </p>
     *
     * @param endpoint the endpoint path
     * @param filePath the file path to load the body from
     */
    @Wenn("ich eine POST-Anfrage nach {string} mit dem Body aus der Datei {string} sende")
    public void wenn_ich_eine_POST_anfrage_nach_string_mit_dem_body_aus_der_datei_string_sende(String endpoint, String filePath) {
        ScenarioLogManager.getLogger().info("Sending POST request to {} with body from file {}", endpoint, filePath);
        BodyHelper.setBodyFromFile(filePath);
        baseRequest.sendRequest(endpoint, Operation.POST);
    }

    /**
     * Sends a HTTP PUT request with the request body loaded from a file.
     *
     * @param endpoint the endpoint path
     * @param filePath the file path to load the body from
     */
    @Wenn("ich eine PUT-Anfrage nach {string} mit dem Body aus der Datei {string} sende")
    public void wenn_ich_eine_PUT_anfrage_nach_string_mit_dem_body_aus_der_datei_string_sende(String endpoint, String filePath) {
        ScenarioLogManager.getLogger().info("Sending PUT request to {} with body from file {}", endpoint, filePath);
        BodyHelper.setBodyFromFile(filePath);
        baseRequest.sendRequest(endpoint, Operation.PUT);
    }

    /**
     * Sends a HTTP PATCH request with the request body loaded from a file.
     *
     * @param endpoint the endpoint path
     * @param filePath the file path to load the body from
     */
    @Wenn("ich eine PATCH-Anfrage nach {string} mit dem Body aus der Datei {string} sende")
    public void wenn_ich_eine_PATCH_anfrage_nach_string_mit_dem_body_aus_der_datei_string_sende(String endpoint, String filePath) {
        ScenarioLogManager.getLogger().info("Sending PATCH request to {} with body from file {}", endpoint, filePath);
        BodyHelper.setBodyFromFile(filePath);
        baseRequest.sendRequest(endpoint, Operation.PATCH);
    }

    /**
     * Sends a HTTP POST request using a template as request body and optional variables from
     * a {@link DataTable}.
     * <div>
     * Example:
     *
     * <pre>
     *   Wenn ich eine POST-Anfrage nach "/users" mit dem Template "user-template.json"
     *   und folgenden Werten sende:
     *     | name | Alice |
     *     | age  | 30    |
     * </pre>
     *
     * </div>
     *
     * @param endpoint the endpoint path
     * @param templateName the name of the template to use
     * @param dataTable variables used for template rendering
     */
    @Wenn("ich eine POST-Anfrage nach {string} mit dem Template {string} und folgenden Werten sende:")
    public void wenn_ich_eine_POST_anfrage_nach_string_mit_dem_template_string_und_folgenden_werten_sende(String endpoint,
            String templateName,
            DataTable dataTable) {
        Map<String, Object> variables = dataTable.asMap(String.class, Object.class);
        ScenarioLogManager.getLogger().info(
                "Sending POST request to {} with template {} and variables {}",
                endpoint, templateName, variables);
        BodyHelper.setBodyFromTemplate(templateName, variables);
        baseRequest.sendRequest(endpoint, Operation.POST);
    }

    /**
     * Sends a HTTP PUT request using a template as request body and optional variables from
     * a {@link DataTable}.
     *
     * @param endpoint the endpoint path
     * @param templateName the name of the template to use
     * @param dataTable variables used for template rendering
     */
    @Wenn("ich eine PUT-Anfrage nach {string} mit dem Template {string} und folgenden Werten sende:")
    public void wenn_ich_eine_PUT_anfrage_nach_string_mit_dem_template_string_und_folgenden_werten_sende(String endpoint,
            String templateName,
            DataTable dataTable) {
        Map<String, Object> variables = dataTable.asMap(String.class, Object.class);
        ScenarioLogManager.getLogger().info(
                "Sending PUT request to {} with template {} and variables {}",
                endpoint, templateName, variables);
        BodyHelper.setBodyFromTemplate(templateName, variables);
        baseRequest.sendRequest(endpoint, Operation.PUT);
    }

    /**
     * Sends a HTTP PATCH request using a template as request body and optional variables from
     * a {@link DataTable}.
     *
     * @param endpoint the endpoint path
     * @param templateName the name of the template to use
     * @param dataTable variables used for template rendering
     */
    @Wenn("ich eine PATCH-Anfrage nach {string} mit dem Template {string} und folgenden Werten sende:")
    public void wenn_ich_eine_PATCH_anfrage_nach_string_mit_dem_template_string_und_folgenden_werten_sende(String endpoint,
            String templateName,
            DataTable dataTable) {
        Map<String, Object> variables = dataTable.asMap(String.class, Object.class);
        ScenarioLogManager.getLogger().info(
                "Sending PATCH request to {} with template {} and variables {}",
                endpoint, templateName, variables);
        BodyHelper.setBodyFromTemplate(templateName, variables);
        baseRequest.sendRequest(endpoint, Operation.PATCH);
    }

    /**
     * Asserts that the HTTP status code of the response matches the expected value.
     *
     * @param statusCode expected HTTP status code
     */
    @Dann("hat die Antwort den Status {int}")
    public void dann_hat_die_antwort_den_status_int(int statusCode) {
        ScenarioLogManager.getLogger().info("Asserting response status code {}", statusCode);
        baseRequest.assertStatusCode(statusCode);
    }

    /**
     * Asserts that the response has the expected Content-Type.
     *
     * @param contentType expected Content-Type (e.g. "application/json")
     */
    @Dann("hat die Antwort den Content-Type {string}")
    public void dann_hat_die_antwort_den_content_type_string(String contentType) {
        ScenarioLogManager.getLogger().info("Asserting response Content-Type {}", contentType);
        baseRequest.assertContentType(contentType);
    }

    /**
     * Asserts that the response body contains the given substring.
     *
     * @param expectedSubstring substring that must be present in the response body
     */
    @Dann("enthält der Response-Body {string}")
    public void dann_enthaelt_der_response_body_string(String expectedSubstring) {
        ScenarioLogManager.getLogger().info("Asserting response body contains '{}'", expectedSubstring);
        baseRequest.assertBodyContains(expectedSubstring);
    }

    /**
     * Asserts that a JSON path in the response body equals the given string value.
     *
     * @param jsonPath the JSON path expression (e.g. "data.id")
     * @param expectedValue the expected string value
     */
    @Dann("ist der JSON-Pfad {string} gleich {string}")
    public void dann_ist_der_json_pfad_string_gleich_string(String jsonPath, String expectedValue) {
        ScenarioLogManager.getLogger().info(
                "Asserting JSON path {} equals string '{}'", jsonPath, expectedValue);
        baseRequest.assertJsonPath(jsonPath, Matchers.is(expectedValue));
    }

    /**
     * Asserts that a JSON path in the response body equals the given integer value.
     *
     * @param jsonPath the JSON path expression (e.g. "data.id")
     * @param expectedValue the expected integer value
     */
    @Dann("ist der JSON-Pfad {string} gleich {int}")
    public void dann_ist_der_json_pfad_string_gleich_int(String jsonPath, int expectedValue) {
        ScenarioLogManager.getLogger().info(
                "Asserting JSON path {} equals int {}", jsonPath, expectedValue);
        baseRequest.assertJsonPath(jsonPath, Matchers.is(expectedValue));
    }

    /**
     * Asserts that a JSON path in the response body equals the given boolean value.
     *
     * @param jsonPath the JSON path expression (e.g. "active")
     * @param expectedValue the expected boolean value
     */
    @Dann("ist der JSON-Pfad {string} gleich {word}")
    public void dann_ist_der_json_pfad_string_gleich_boolean(String jsonPath, String expectedValue) {
        // this step is intended mainly for boolean literals "true" / "false"
        boolean boolValue = Boolean.parseBoolean(expectedValue);
        ScenarioLogManager.getLogger().info(
                "Asserting JSON path {} equals boolean {}", jsonPath, boolValue);
        baseRequest.assertJsonPath(jsonPath, Matchers.is(boolValue));
    }

    /**
     * Asserts that the given header is present in the response.
     *
     * @param headerName header name
     */
    @Dann("ist der Header {string} vorhanden")
    public void dann_ist_der_header_string_vorhanden(String headerName) {
        ScenarioLogManager.getLogger().info("Asserting header {} is present", headerName);
        baseRequest.assertHeaderPresent(headerName);
    }

    /**
     * Asserts that the given header has the expected value in the response.
     *
     * @param headerName header name
     * @param headerValue expected header value
     */
    @Dann("hat der Header {string} den Wert {string}")
    public void dann_hat_der_header_string_den_wert_string(String headerName, String headerValue) {
        ScenarioLogManager.getLogger().info(
                "Asserting header {} has value {}", headerName, headerValue);
        String actual = baseRequest.getResponse().getHeader(headerName);
        ataf.core.assertions.CustomAssertions.assertEquals(actual, headerValue,
                String.format("Expected header '%s' to have value '%s' but was '%s'",
                        headerName, headerValue, actual));
    }

    /**
     * Enables HTTP retry support using the default configuration for idempotent operations
     * in the current scenario.
     *
     * <p>
     * This step configures the underlying {@link RequestHelper} with
     * {@link RetryConfig#defaultIdempotent()}, which:
     * </p>
     * <ul>
     * <li>enables retries</li>
     * <li>applies retries only to idempotent HTTP methods (GET, PUT, DELETE)</li>
     * <li>uses a limited number of attempts with exponential backoff</li>
     * <li>retries on network errors and selected HTTP status codes (e.g. 502, 503, 504)</li>
     * </ul>
     *
     * <p>
     * Non-idempotent operations (such as POST or PATCH) are not retried when using this default
     * configuration.
     * </p>
     */
    @Gegebensei("Retry ist mit der Standardkonfiguration für idempotente Operationen aktiviert")
    public void retryIstMitStandardkonfigurationAktiviert() {
        RetryConfig config = RetryConfig.defaultIdempotent();
        baseRequest.getRequestHelper().setRetryConfig(config);
    }
}
