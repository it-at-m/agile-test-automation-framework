package ataf.rest.frameworkapi;

import ataf.core.assertions.CustomAssertions;
import ataf.core.logging.ScenarioLogManager;
import ataf.rest.helper.BodyHelper;
import ataf.rest.helper.RequestHelper;
import ataf.rest.helper.SchemaValidationHelper;
import ataf.rest.model.Operation;
import io.restassured.response.Response;
import org.hamcrest.Matcher;

/**
 * This class represents the base request functionality for sending HTTP requests and managing
 * responses within the framework.
 */
public class BaseRequest {

    /**
     * Thread-safe instance of RequestHelper for managing HTTP requests.
     */
    private final RequestHelper REQUEST_HELPER;

    /**
     * The last response of the request.
     */
    private Response response;

    /**
     * Default constructor that initializes RequestHelper with default timeouts.
     */
    public BaseRequest() {
        this.REQUEST_HELPER = new RequestHelper();
        BodyHelper.setRequestHelper(this.REQUEST_HELPER);
    }

    /**
     * Constructor with custom timeout configuration.
     *
     * @param connectTimeoutMs Connection timeout in milliseconds
     * @param socketTimeoutMs Socket timeout in milliseconds
     */
    public BaseRequest(int connectTimeoutMs, int socketTimeoutMs) {
        this.REQUEST_HELPER = new RequestHelper(connectTimeoutMs, socketTimeoutMs);
        BodyHelper.setRequestHelper(this.REQUEST_HELPER);
    }

    /**
     * Sends a request where the inner parameters have been prepared in advance.
     *
     * @param endpoint The endpoint to which the request should be sent
     * @param operation The HTTP method to be used for the request (GET, POST, PUT, DELETE, PATCH)
     */
    public void sendRequest(String endpoint, Operation operation) {
        REQUEST_HELPER.setEndpoint(endpoint);
        response = REQUEST_HELPER.sendRequest(operation);
        ScenarioLogManager.getLogger().info("Response time of the request: {}ms", response.getTime());
    }

    /**
     * Returns the response of a previously sent request. If no request has been sent,
     * a "NotNull" assertion will fail.
     *
     * @return The response of the previous request
     */
    public Response getResponse() {
        CustomAssertions.assertNotNull(response, "Empty response retrieved! It seems no request has been sent.");
        return response;
    }

    /**
     * Manually sets a response where it is expected by other parts of the framework.
     *
     * @param _response The response to be manually set
     */
    public void setResponse(Response _response) {
        response = _response;
    }

    /**
     * Returns the RequestHelper instance for advanced configuration.
     *
     * @return The RequestHelper instance
     */
    public RequestHelper getRequestHelper() {
        return REQUEST_HELPER;
    }

    /**
     * Resets all parameters in the RequestHelper. Must be called in test teardown
     * to prevent memory leaks.
     */
    public void resetRequestHelper() {
        REQUEST_HELPER.resetParameters();
    }

    /**
     * Asserts that the HTTP status code of the last response matches the expected value.
     *
     * @param expectedStatusCode expected HTTP status code
     * @return this BaseRequest instance for fluent API usage
     */
    public BaseRequest assertStatusCode(int expectedStatusCode) {
        int actualStatusCode = getResponse().getStatusCode();

        CustomAssertions.assertEquals(
                actualStatusCode,
                expectedStatusCode,
                String.format(
                        "Unexpected HTTP status code. Expected <%d> but was <%d>.",
                        expectedStatusCode,
                        actualStatusCode));

        return this;
    }

    /**
     * Asserts that the given header is present in the last response.
     *
     * @param headerName name of the HTTP header
     * @return this BaseRequest instance for fluent API usage
     */
    public BaseRequest assertHeaderPresent(String headerName) {
        String headerValue = getResponse().getHeader(headerName);

        CustomAssertions.assertNotNull(
                headerValue,
                String.format("Expected header '%s' to be present, but it was not found.", headerName));

        return this;
    }

    /**
     * Extracts a value from the JSON response body using the given JsonPath expression.
     *
     * @param jsonPath JsonPath expression (e.g. "$.data.id" or "data.id")
     * @return the extracted value as String, or {@code null} if extraction fails (test will fail via
     *         assertion)
     */
    public String extractJsonPath(String jsonPath) {
        try {
            return getResponse().jsonPath().getString(jsonPath);
        } catch (Exception e) {
            CustomAssertions.fail(
                    String.format("Failed to extract JsonPath '%s': %s", jsonPath, e.getMessage()),
                    e);
            return null; // for compiler only
        }
    }

    /**
     * Asserts that the value at the given JsonPath in the JSON response body matches
     * the provided Hamcrest matcher.
     *
     * <p>
     * Diese Methode nutzt Hamcrest nur lokal und leitet das Ergebnis der
     * Matcher-Auswertung über {@link CustomAssertions#assertTrue(boolean, String)} weiter,
     * wodurch die Assertion-Strategie (JUnit, TestNG, ...) gewahrt bleibt.
     * </p>
     *
     * @param jsonPath JsonPath expression (e.g. "$.data.id" or "data.id")
     * @param matcher Hamcrest matcher used to validate the extracted value
     * @return this BaseRequest instance for fluent API usage
     */
    public BaseRequest assertJsonPath(String jsonPath, Matcher<?> matcher) {
        Object actualValue;

        try {
            actualValue = getResponse().jsonPath().get(jsonPath);
        } catch (Exception e) {
            CustomAssertions.fail(
                    String.format("Failed to read JsonPath '%s': %s", jsonPath, e.getMessage()),
                    e);
            return this; // for compiler only
        }

        boolean matches = matcher.matches(actualValue);

        CustomAssertions.assertTrue(
                matches,
                String.format(
                        "JsonPath '%s' did not match. Actual value: <%s>, matcher: <%s>",
                        jsonPath,
                        actualValue,
                        matcher));

        return this;
    }

    /**
     * Asserts that the Content-Type of the response matches the expected value.
     *
     * @param expected expected Content-Type (e.g. "application/json")
     * @return this BaseRequest instance for fluent API usage
     */
    public BaseRequest assertContentType(String expected) {
        String actual = getResponse().getContentType();

        CustomAssertions.assertEquals(
                actual,
                expected,
                String.format(
                        "Unexpected Content-Type. Expected <%s> but was <%s>.",
                        expected,
                        actual));

        return this;
    }

    /**
     * Asserts that the response body contains the given substring.
     *
     * @param expectedSubstring substring that must be present in the body
     * @return this BaseRequest instance for fluent API usage
     */
    public BaseRequest assertBodyContains(String expectedSubstring) {
        String body = getResponse().getBody() != null ? getResponse().getBody().asString() : null;

        CustomAssertions.assertNotNull(
                body,
                "Expected response body to be non-null for assertBodyContains.");

        boolean contains = body.contains(expectedSubstring);

        CustomAssertions.assertTrue(
                contains,
                String.format(
                        "Expected response body to contain <%s>, but it did not. Actual body: <%s>",
                        expectedSubstring,
                        body));

        return this;
    }

    /**
     * Extracts a value from the JSON response body using the given JsonPath expression
     * and converts it to the specified type.
     *
     * @param jsonPath JsonPath expression (e.g. "$.data.id" or "data.id")
     * @param type target type
     * @param <T> generic type parameter
     * @return extracted value as the requested type
     */
    public <T> T extractJsonPathAs(String jsonPath, Class<T> type) {
        try {
            return getResponse().jsonPath().getObject(jsonPath, type);
        } catch (Exception e) {
            CustomAssertions.fail(
                    String.format(
                            "Failed to extract JsonPath '%s' as type %s: %s",
                            jsonPath,
                            type != null ? type.getSimpleName() : "null",
                            e.getMessage()),
                    e);
            return null; // for compiler only
        }
    }

    /**
     * Validates the JSON response body of the last request against the given JSON schema.
     *
     * <p>
     * The {@code schemaPath} is typically a classpath location such as
     * {@code "schemas/user-schema.json"}. It is passed to RestAssured's
     * json-schema-validator using {@code matchesJsonSchemaInClasspath(schemaPath)}.
     * </p>
     *
     * @param schemaPath classpath path to the JSON schema
     * @return this BaseRequest instance for fluent API usage
     */
    public BaseRequest assertJsonSchema(String schemaPath) {
        Response resp = getResponse();
        SchemaValidationHelper.validateJsonSchema(resp, schemaPath);
        return this;
    }

    /**
     * Validates the XML response body of the last request against the given XSD schema.
     *
     * <p>
     * The {@code xsdPath} may be a classpath resource or a file system path, resolved by
     * {@link ataf.core.helpers.ResourceHelper}.
     * </p>
     *
     * @param xsdPath path to the XSD schema (classpath or file system)
     * @return this BaseRequest instance for fluent API usage
     */
    public BaseRequest assertXmlSchema(String xsdPath) {
        Response resp = getResponse();
        SchemaValidationHelper.validateXmlSchema(resp, xsdPath);
        return this;
    }
}
