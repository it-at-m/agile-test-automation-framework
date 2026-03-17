package ataf.rest.frameworkapi;

import ataf.core.assertions.CustomAssertions;
import ataf.core.assertions.strategy.impl.TestNGAssertionStrategy;
import ataf.rest.helper.RequestHelper;
import ataf.rest.model.Operation;
import io.restassured.builder.ResponseBuilder;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.Field;

public class BaseRequestTest {

    @BeforeClass
    public void setUpAssertions() {
        // Use TestNG-based assertion strategy for all CustomAssertions in tests
        CustomAssertions.setStrategy(new TestNGAssertionStrategy());
    }

    @AfterMethod
    public void resetStaticResponse() {
        // Ensure static response is cleared between tests
        BaseRequest baseRequest = new BaseRequest();
        baseRequest.setResponse(null);
    }

    private Response buildResponse(int statusCode, String contentType, String body, Headers headers) {
        ResponseBuilder builder = new ResponseBuilder();
        builder.setStatusCode(statusCode);
        builder.setBody(body);

        if (contentType != null) {
            builder.setContentType(contentType);
        }
        if (headers != null) {
            builder.setHeaders(headers);
        }

        return builder.build();
    }

    private Response buildResponse(int statusCode, String contentType, String body) {
        return buildResponse(statusCode, contentType, body, null);
    }

    // -------------------------------------------------------------------------
    // Fake RequestHelper to test sendRequest without real HTTP
    // -------------------------------------------------------------------------

    static class FakeRequestHelper extends RequestHelper {

        private String lastEndpoint;
        private Response responseToReturn;

        public void setResponseToReturn(Response responseToReturn) {
            this.responseToReturn = responseToReturn;
        }

        public String getLastEndpoint() {
            return lastEndpoint;
        }

        @Override
        public void setEndpoint(String endpoint) {
            this.lastEndpoint = endpoint;
        }

        @Override
        public Response sendRequest(Operation operation) {
            return responseToReturn;
        }
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void testSendRequestUsesRequestHelper() throws Exception {
        BaseRequest baseRequest = new BaseRequest();
        FakeRequestHelper fakeHelper = new FakeRequestHelper();

        Response fakeResponse = buildResponse(200, "application/json", "{\"ok\":true}");
        fakeHelper.setResponseToReturn(fakeResponse);

        // Inject FakeRequestHelper via reflection (field is private final)
        Field field = BaseRequest.class.getDeclaredField("REQUEST_HELPER");
        field.setAccessible(true);
        field.set(baseRequest, fakeHelper);

        baseRequest.sendRequest("/test-endpoint", Operation.GET);

        // Verify that sendRequest set endpoint and the response is available
        CustomAssertions.assertEquals(fakeHelper.getLastEndpoint(), "/test-endpoint");
        CustomAssertions.assertEquals(baseRequest.getResponse().getStatusCode(), 200);
    }

    @Test
    public void testGetResponseWithoutSettingThrows() {
        BaseRequest baseRequest = new BaseRequest();

        CustomAssertions.assertThrows(AssertionError.class, baseRequest::getResponse);
    }

    @Test
    public void testSetAndGetResponse() {
        BaseRequest baseRequest = new BaseRequest();
        Response response = buildResponse(201, "application/json", "{\"foo\":\"bar\"}");

        baseRequest.setResponse(response);
        Response result = baseRequest.getResponse();

        CustomAssertions.assertNotNull(result);
        CustomAssertions.assertEquals(result.getStatusCode(), 201);
    }

    @Test
    public void testGetRequestHelperNotNull() {
        BaseRequest baseRequest = new BaseRequest();
        CustomAssertions.assertNotNull(baseRequest.getRequestHelper());
    }

    @Test
    public void testResetRequestHelperDoesNotThrow() {
        BaseRequest baseRequest = new BaseRequest();
        baseRequest.resetRequestHelper();
        // If no exception occurs, test is fine – behaviour is internal to RequestHelper
    }

    @Test
    public void testAssertStatusCodePasses() {
        BaseRequest baseRequest = new BaseRequest();
        Response response = buildResponse(200, "application/json", "{}");

        baseRequest.setResponse(response);
        baseRequest.assertStatusCode(200); // no exception expected
    }

    @Test
    public void testAssertStatusCodeFails() {
        BaseRequest baseRequest = new BaseRequest();
        Response response = buildResponse(404, "application/json", "{}");

        baseRequest.setResponse(response);

        CustomAssertions.assertThrows(
                AssertionError.class,
                () -> baseRequest.assertStatusCode(200));
    }

    @Test
    public void testAssertHeaderPresentPasses() {
        BaseRequest baseRequest = new BaseRequest();

        Headers headers = new Headers(
                new Header("X-Request-Id", "abc123"));
        Response response = buildResponse(200, "application/json", "{}", headers);

        baseRequest.setResponse(response);
        baseRequest.assertHeaderPresent("X-Request-Id"); // no exception expected
    }

    @Test
    public void testAssertHeaderPresentFails() {
        BaseRequest baseRequest = new BaseRequest();
        Response response = buildResponse(200, "application/json", "{}");

        baseRequest.setResponse(response);

        CustomAssertions.assertThrows(
                AssertionError.class,
                () -> baseRequest.assertHeaderPresent("X-Not-There"));
    }

    @Test
    public void testExtractJsonPath() {
        BaseRequest baseRequest = new BaseRequest();
        Response response = buildResponse(200, "application/json", "{\"data\":{\"id\":42}}");

        baseRequest.setResponse(response);

        String id = baseRequest.extractJsonPath("data.id");
        CustomAssertions.assertEquals(id, "42");
    }

    @Test
    public void testAssertJsonPathWithMatcherPasses() {
        BaseRequest baseRequest = new BaseRequest();
        Response response = buildResponse(
                200,
                "application/json",
                "{\"id\":42,\"active\":true}");

        baseRequest.setResponse(response);

        baseRequest
                .assertJsonPath("id", Matchers.is(42))
                .assertJsonPath("active", Matchers.is(true));
    }

    @Test
    public void testAssertJsonPathWithMatcherFails() {
        BaseRequest baseRequest = new BaseRequest();
        Response response = buildResponse(
                200,
                "application/json",
                "{\"id\":42}");

        baseRequest.setResponse(response);

        CustomAssertions.assertThrows(
                AssertionError.class,
                () -> baseRequest.assertJsonPath("id", Matchers.is(100)));
    }

    @Test
    public void testAssertContentTypePasses() {
        BaseRequest baseRequest = new BaseRequest();
        Response response = buildResponse(
                200,
                "application/json",
                "{}");

        baseRequest.setResponse(response);
        baseRequest.assertContentType("application/json");
    }

    @Test
    public void testAssertContentTypeFails() {
        BaseRequest baseRequest = new BaseRequest();
        Response response = buildResponse(
                200,
                "text/plain",
                "ok");

        baseRequest.setResponse(response);

        CustomAssertions.assertThrows(
                AssertionError.class,
                () -> baseRequest.assertContentType("application/json"));
    }

    @Test
    public void testAssertBodyContainsPasses() {
        BaseRequest baseRequest = new BaseRequest();
        Response response = buildResponse(
                200,
                "application/json",
                "{\"message\":\"hello world\"}");

        baseRequest.setResponse(response);
        baseRequest.assertBodyContains("hello world");
    }

    @Test
    public void testAssertBodyContainsFails() {
        BaseRequest baseRequest = new BaseRequest();
        Response response = buildResponse(
                200,
                "application/json",
                "{\"message\":\"hello world\"}");

        baseRequest.setResponse(response);

        CustomAssertions.assertThrows(
                AssertionError.class,
                () -> baseRequest.assertBodyContains("goodbye"));
    }

    @Test
    public void testExtractJsonPathAs() {
        BaseRequest baseRequest = new BaseRequest();
        Response response = buildResponse(
                200,
                "application/json",
                "{\"data\":{\"id\":42,\"name\":\"foo\"}}");

        baseRequest.setResponse(response);

        Integer id = baseRequest.extractJsonPathAs("data.id", Integer.class);
        String name = baseRequest.extractJsonPathAs("data.name", String.class);

        CustomAssertions.assertEquals(id, Integer.valueOf(42));
        CustomAssertions.assertEquals(name, "foo");
    }

    // -------------------------------------------------------------------------
    // Tests for schema validation helpers
    // -------------------------------------------------------------------------

    @Test
    public void testAssertJsonSchemaFailsForMissingSchema() {
        BaseRequest baseRequest = new BaseRequest();
        Response response = buildResponse(
                200,
                "application/json",
                "{\"id\":1}");

        baseRequest.setResponse(response);

        // For a non-existing schema path, SchemaValidationHelper should end up
        // triggering CustomAssertions.fail(...) -> AssertionError
        CustomAssertions.assertThrows(
                AssertionError.class,
                () -> baseRequest.assertJsonSchema("schemas/non-existing-schema.json"));
    }

    @Test
    public void testAssertXmlSchemaFailsForMissingSchema() {
        BaseRequest baseRequest = new BaseRequest();
        Response response = buildResponse(
                200,
                "application/xml",
                "<root><id>1</id></root>");

        baseRequest.setResponse(response);

        // For a non-existing XSD path, SchemaValidationHelper should end up
        // triggering CustomAssertions.fail(...) -> AssertionError
        CustomAssertions.assertThrows(
                AssertionError.class,
                () -> baseRequest.assertXmlSchema("schemas/non-existing-schema.xsd"));
    }
}
