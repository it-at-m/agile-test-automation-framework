package ataf.rest.steps;

import ataf.core.assertions.CustomAssertions;
import ataf.core.assertions.strategy.AssertionStrategy;
import ataf.core.logging.ScenarioLogManager;
import ataf.rest.frameworkapi.BaseRequest;
import ataf.rest.helper.BodyHelper;
import ataf.rest.helper.HeaderHelper;
import ataf.rest.helper.RequestHelper;
import ataf.rest.model.Operation;
import io.cucumber.datatable.DataTable;
import org.hamcrest.Matcher;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * Unit Tests for {@link BaseRestSteps}.
 * <p>
 * Notes:
 * - Assertions are done via CustomAssertions (strategy: TestNGAssertionStrategy).
 * - External collaborators are mocked (BaseRequest, RequestHelper).
 * - Static helper calls (BodyHelper/HeaderHelper) are verified via Mockito static mocking.
 * - ScenarioLogManager is statically mocked to avoid NPEs in unit test context.
 */
public class BaseRestStepsTest {

    @SuppressWarnings("rawtypes")
    private MockedStatic scenarioLogManagerMock;

    private BaseRestSteps steps;

    private BaseRequest baseRequestMock;
    private RequestHelper requestHelperMock;

    @BeforeClass(alwaysRun = true)
    public void configureAssertionStrategy() {
        CustomAssertions.setStrategy(instantiateTestNgAssertionStrategy());
    }

    @SuppressWarnings("unchecked")
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        // ---- Mock ScenarioLogManager.getLogger() to avoid any scenario-context dependency ----
        // We intentionally keep this raw-typed to avoid binding to a specific logger type.
        scenarioLogManagerMock = Mockito.mockStatic(ScenarioLogManager.class);

        Method getLoggerMethod = ScenarioLogManager.class.getMethod("getLogger");
        Class<?> loggerType = getLoggerMethod.getReturnType();
        Object loggerMock = Mockito.mock(loggerType);

        scenarioLogManagerMock.when(ScenarioLogManager::getLogger).thenReturn(loggerMock);

        // ---- Create mocks used by BaseRestSteps ----
        baseRequestMock = Mockito.mock(BaseRequest.class, RETURNS_DEEP_STUBS);
        requestHelperMock = Mockito.mock(RequestHelper.class);
        when(baseRequestMock.getRequestHelper()).thenReturn(requestHelperMock);

        // ---- System under test ----
        steps = new BaseRestSteps();
        steps.setBaseRequest(baseRequestMock);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (scenarioLogManagerMock != null) {
            scenarioLogManagerMock.close();
        }
    }

    // -----------------------------------------------------------------------------------------
    // Constructor + BaseRequest getter/setter
    // -----------------------------------------------------------------------------------------

    @Test
    public void constructor_shouldInitializeBaseRequest() {
        BaseRestSteps fresh = new BaseRestSteps();
        CustomAssertions.assertNotNull(fresh.getBaseRequest(), "BaseRequest must be initialized in constructor.");
    }

    @Test
    public void getBaseRequest_shouldReturnCurrentInstance() {
        CustomAssertions.assertTrue(steps.getBaseRequest() == baseRequestMock,
                "getBaseRequest() should return the instance previously set via setBaseRequest().");
    }

    @Test
    public void setBaseRequest_shouldReplaceInstance() {
        BaseRequest other = Mockito.mock(BaseRequest.class, RETURNS_DEEP_STUBS);

        steps.setBaseRequest(other);

        CustomAssertions.assertTrue(steps.getBaseRequest() == other,
                "setBaseRequest() should replace the internal BaseRequest reference.");
    }

    // -----------------------------------------------------------------------------------------
    // Base URL + Header steps
    // -----------------------------------------------------------------------------------------

    @Test
    public void gegeben_sei_die_basis_url_ist_string_shouldDelegateToRequestHelper() {
        String baseUrl = "https://api.example.com";

        steps.gegeben_sei_die_basis_url_ist_string(baseUrl);

        verify(baseRequestMock, times(1)).getRequestHelper();
        verify(requestHelperMock, times(1)).setBaseURL(baseUrl);
    }

    @Test
    public void und_folgende_header_gesetzt_sind_data_table_shouldCallHeaderHelper() {
        DataTable dt = DataTable.create(List.of(
                List.of("Content-Type", "application/json"),
                List.of("Accept", "application/json")));

        try (MockedStatic<HeaderHelper> headerHelper = Mockito.mockStatic(HeaderHelper.class)) {
            steps.und_folgende_header_gesetzt_sind_data_table(dt);

            headerHelper.verify(() -> HeaderHelper.addHeadersFromDataTable(dt), times(1));
        }
    }

    @Test
    public void und_der_header_string_mit_dem_wert_string_gesetzt_ist_shouldCallHeaderHelper() {
        String headerName = "Authorization";
        String headerValue = "Bearer token";

        try (MockedStatic<HeaderHelper> headerHelper = Mockito.mockStatic(HeaderHelper.class)) {
            steps.und_der_header_string_mit_dem_wert_string_gesetzt_ist(headerName, headerValue);

            headerHelper.verify(() -> HeaderHelper.addHeader(headerName, headerValue), times(1));
        }
    }

    // -----------------------------------------------------------------------------------------
    // Send request steps
    // -----------------------------------------------------------------------------------------

    @Test
    public void wenn_ich_eine_GET_anfrage_nach_string_sende_shouldSendGetRequest() {
        String endpoint = "/users/42";

        steps.wenn_ich_eine_GET_anfrage_nach_string_sende(endpoint);

        verify(baseRequestMock, times(1)).sendRequest(endpoint, Operation.GET);
    }

    @Test
    public void wenn_ich_eine_DELETE_anfrage_nach_string_sende_shouldSendDeleteRequest() {
        String endpoint = "/users/42";

        steps.wenn_ich_eine_DELETE_anfrage_nach_string_sende(endpoint);

        verify(baseRequestMock, times(1)).sendRequest(endpoint, Operation.DELETE);
    }

    @Test
    public void wenn_ich_eine_POST_anfrage_nach_string_mit_folgendem_body_sende_shouldSetBodyFromStringAndSendPost() {
        String endpoint = "/users";
        String body = "{ \"name\": \"Alice\" }";

        try (MockedStatic<BodyHelper> bodyHelper = Mockito.mockStatic(BodyHelper.class)) {
            steps.wenn_ich_eine_POST_anfrage_nach_string_mit_folgendem_body_sende(endpoint, body);

            bodyHelper.verify(() -> BodyHelper.setBodyFromString(body), times(1));
        }

        verify(baseRequestMock, times(1)).sendRequest(endpoint, Operation.POST);
    }

    @Test
    public void wenn_ich_eine_PUT_anfrage_nach_string_mit_folgendem_body_sende_shouldSetBodyFromStringAndSendPut() {
        String endpoint = "/users/42";
        String body = "{ \"name\": \"Bob\" }";

        try (MockedStatic<BodyHelper> bodyHelper = Mockito.mockStatic(BodyHelper.class)) {
            steps.wenn_ich_eine_PUT_anfrage_nach_string_mit_folgendem_body_sende(endpoint, body);

            bodyHelper.verify(() -> BodyHelper.setBodyFromString(body), times(1));
        }

        verify(baseRequestMock, times(1)).sendRequest(endpoint, Operation.PUT);
    }

    @Test
    public void wenn_ich_eine_PATCH_anfrage_nach_string_mit_folgendem_body_sende_shouldSetBodyFromStringAndSendPatch() {
        String endpoint = "/users/42";
        String body = "{ \"active\": true }";

        try (MockedStatic<BodyHelper> bodyHelper = Mockito.mockStatic(BodyHelper.class)) {
            steps.wenn_ich_eine_PATCH_anfrage_nach_string_mit_folgendem_body_sende(endpoint, body);

            bodyHelper.verify(() -> BodyHelper.setBodyFromString(body), times(1));
        }

        verify(baseRequestMock, times(1)).sendRequest(endpoint, Operation.PATCH);
    }

    @Test
    public void wenn_ich_eine_POST_anfrage_nach_string_mit_dem_body_aus_der_datei_string_sende_shouldSetBodyFromFileAndSendPost() {
        String endpoint = "/users";
        String filePath = "bodies/user.json";

        try (MockedStatic<BodyHelper> bodyHelper = Mockito.mockStatic(BodyHelper.class)) {
            steps.wenn_ich_eine_POST_anfrage_nach_string_mit_dem_body_aus_der_datei_string_sende(endpoint, filePath);

            bodyHelper.verify(() -> BodyHelper.setBodyFromFile(filePath), times(1));
        }

        verify(baseRequestMock, times(1)).sendRequest(endpoint, Operation.POST);
    }

    @Test
    public void wenn_ich_eine_PUT_anfrage_nach_string_mit_dem_body_aus_der_datei_string_sende_shouldSetBodyFromFileAndSendPut() {
        String endpoint = "/users/42";
        String filePath = "bodies/user-update.json";

        try (MockedStatic<BodyHelper> bodyHelper = Mockito.mockStatic(BodyHelper.class)) {
            steps.wenn_ich_eine_PUT_anfrage_nach_string_mit_dem_body_aus_der_datei_string_sende(endpoint, filePath);

            bodyHelper.verify(() -> BodyHelper.setBodyFromFile(filePath), times(1));
        }

        verify(baseRequestMock, times(1)).sendRequest(endpoint, Operation.PUT);
    }

    @Test
    public void wenn_ich_eine_PATCH_anfrage_nach_string_mit_dem_body_aus_der_datei_string_sende_shouldSetBodyFromFileAndSendPatch() {
        String endpoint = "/users/42";
        String filePath = "bodies/user-patch.json";

        try (MockedStatic<BodyHelper> bodyHelper = Mockito.mockStatic(BodyHelper.class)) {
            steps.wenn_ich_eine_PATCH_anfrage_nach_string_mit_dem_body_aus_der_datei_string_sende(endpoint, filePath);

            bodyHelper.verify(() -> BodyHelper.setBodyFromFile(filePath), times(1));
        }

        verify(baseRequestMock, times(1)).sendRequest(endpoint, Operation.PATCH);
    }

    @Test
    public void wenn_ich_eine_POST_anfrage_nach_string_mit_dem_template_string_und_folgenden_werten_sende_shouldSetBodyFromTemplateAndSendPost() {
        String endpoint = "/users";
        String template = "user-template.json";

        Map<String, Object> expectedVars = Map.of(
                "name", (Object) "Alice",
                "age", (Object) "30");

        DataTable dt = Mockito.mock(DataTable.class);
        when(dt.asMap(String.class, Object.class)).thenReturn(expectedVars);

        try (MockedStatic<BodyHelper> bodyHelper = Mockito.mockStatic(BodyHelper.class)) {
            steps.wenn_ich_eine_POST_anfrage_nach_string_mit_dem_template_string_und_folgenden_werten_sende(
                    endpoint, template, dt);

            bodyHelper.verify(() -> BodyHelper.setBodyFromTemplate(eq(template), eq(expectedVars)), times(1));
        }

        verify(baseRequestMock, times(1)).sendRequest(endpoint, Operation.POST);
    }

    @Test
    public void wenn_ich_eine_PUT_anfrage_nach_string_mit_dem_template_string_und_folgenden_werten_sende_shouldSetBodyFromTemplateAndSendPut() {
        String endpoint = "/users/42";
        String template = "user-template.json";

        Map<String, Object> expectedVars = Map.of(
                "name", (Object) "Bob",
                "age", (Object) "31");

        DataTable dt = Mockito.mock(DataTable.class);
        when(dt.asMap(String.class, Object.class)).thenReturn(expectedVars);

        try (MockedStatic<BodyHelper> bodyHelper = Mockito.mockStatic(BodyHelper.class)) {
            steps.wenn_ich_eine_PUT_anfrage_nach_string_mit_dem_template_string_und_folgenden_werten_sende(
                    endpoint, template, dt);

            bodyHelper.verify(() -> BodyHelper.setBodyFromTemplate(eq(template), eq(expectedVars)), times(1));
        }

        verify(baseRequestMock, times(1)).sendRequest(endpoint, Operation.PUT);
    }

    @Test
    public void wenn_ich_eine_PATCH_anfrage_nach_string_mit_dem_template_string_und_folgenden_werten_sende_shouldSetBodyFromTemplateAndSendPatch() {
        String endpoint = "/users/42";
        String template = "user-template.json";

        Map<String, Object> expectedVars = Map.of(
                "active", (Object) "true");

        DataTable dt = Mockito.mock(DataTable.class);
        when(dt.asMap(String.class, Object.class)).thenReturn(expectedVars);

        try (MockedStatic<BodyHelper> bodyHelper = Mockito.mockStatic(BodyHelper.class)) {
            steps.wenn_ich_eine_PATCH_anfrage_nach_string_mit_dem_template_string_und_folgenden_werten_sende(
                    endpoint, template, dt);

            bodyHelper.verify(() -> BodyHelper.setBodyFromTemplate(eq(template), eq(expectedVars)), times(1));
        }

        verify(baseRequestMock, times(1)).sendRequest(endpoint, Operation.PATCH);
    }

    // -----------------------------------------------------------------------------------------
    // Response assertion steps
    // -----------------------------------------------------------------------------------------

    @Test
    public void dann_hat_die_antwort_den_status_int_shouldDelegateToBaseRequest() {
        int statusCode = 201;

        steps.dann_hat_die_antwort_den_status_int(statusCode);

        verify(baseRequestMock, times(1)).assertStatusCode(statusCode);
    }

    @Test
    public void dann_hat_die_antwort_den_content_type_string_shouldDelegateToBaseRequest() {
        String contentType = "application/json";

        steps.dann_hat_die_antwort_den_content_type_string(contentType);

        verify(baseRequestMock, times(1)).assertContentType(contentType);
    }

    @Test
    public void dann_enthaelt_der_response_body_string_shouldDelegateToBaseRequest() {
        String substring = "Alice";

        steps.dann_enthaelt_der_response_body_string(substring);

        verify(baseRequestMock, times(1)).assertBodyContains(substring);
    }

    @Test
    public void dann_ist_der_json_pfad_string_gleich_string_shouldDelegateWithMatcher() {
        String jsonPath = "name";
        String expected = "Alice";

        steps.dann_ist_der_json_pfad_string_gleich_string(jsonPath, expected);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Matcher<?>> captor = ArgumentCaptor.forClass(Matcher.class);
        verify(baseRequestMock, times(1)).assertJsonPath(eq(jsonPath), captor.capture());

        Matcher<?> matcher = captor.getValue();
        CustomAssertions.assertTrue(matcher.matches(expected),
                "Expected matcher to match the provided String value.");
    }

    @Test
    public void dann_ist_der_json_pfad_string_gleich_int_shouldDelegateWithMatcher() {
        String jsonPath = "age";
        int expected = 30;

        steps.dann_ist_der_json_pfad_string_gleich_int(jsonPath, expected);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Matcher<?>> captor = ArgumentCaptor.forClass(Matcher.class);
        verify(baseRequestMock, times(1)).assertJsonPath(eq(jsonPath), captor.capture());

        Matcher<?> matcher = captor.getValue();
        CustomAssertions.assertTrue(matcher.matches(expected),
                "Expected matcher to match the provided int value.");
    }

    @Test
    public void dann_ist_der_json_pfad_string_gleich_boolean_shouldParseAndDelegateWithMatcher() {
        String jsonPath = "active";
        String expectedLiteral = "true";

        steps.dann_ist_der_json_pfad_string_gleich_boolean(jsonPath, expectedLiteral);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Matcher<?>> captor = ArgumentCaptor.forClass(Matcher.class);
        verify(baseRequestMock, times(1)).assertJsonPath(eq(jsonPath), captor.capture());

        Matcher<?> matcher = captor.getValue();
        CustomAssertions.assertTrue(matcher.matches(true),
                "Expected matcher to match the parsed boolean value 'true'.");
    }

    @Test
    public void dann_ist_der_header_string_vorhanden_shouldDelegateToBaseRequest() {
        String headerName = "Content-Type";

        steps.dann_ist_der_header_string_vorhanden(headerName);

        verify(baseRequestMock, times(1)).assertHeaderPresent(headerName);
    }

    @Test
    public void dann_hat_der_header_string_den_wert_string_shouldPassWhenHeaderMatches() {
        String headerName = "Content-Type";
        String expected = "application/json";

        when(baseRequestMock.getResponse().getHeader(headerName)).thenReturn(expected);

        // Should not throw
        steps.dann_hat_der_header_string_den_wert_string(headerName, expected);
    }

    @Test
    public void dann_hat_der_header_string_den_wert_string_shouldFailWhenHeaderDoesNotMatch() {
        String headerName = "X-Test";
        String expected = "expected-value";
        String actual = "actual-value";

        when(baseRequestMock.getResponse().getHeader(headerName)).thenReturn(actual);

        try {
            steps.dann_hat_der_header_string_den_wert_string(headerName, expected);
            CustomAssertions.fail("Expected an AssertionError due to header value mismatch.");
        } catch (AssertionError e) {
            // Message should contain our formatted message (at least partially)
            CustomAssertions.assertTrue(
                    e.getMessage() != null && e.getMessage().contains("Expected header"),
                    "AssertionError message should contain the custom failure text.");
        }
    }

    // -----------------------------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------------------------

    private static AssertionStrategy instantiateTestNgAssertionStrategy() {
        // Try a couple of plausible FQCNs so the test code stays robust if packages differ.
        List<String> candidates = List.of(
                "ataf.core.assertions.strategy.TestNGAssertionStrategy",
                "ataf.core.assertions.strategy.testng.TestNGAssertionStrategy",
                "ataf.core.assertions.strategy.impl.TestNGAssertionStrategy",
                "ataf.core.assertions.testng.TestNGAssertionStrategy");

        for (String fqcn : candidates) {
            try {
                Class<?> clazz = Class.forName(fqcn);
                Object instance = clazz.getDeclaredConstructor().newInstance();
                return (AssertionStrategy) instance;
            } catch (ClassNotFoundException ignored) {
                // try next
            } catch (Exception e) {
                throw new RuntimeException("Found " + fqcn + " but could not instantiate it.", e);
            }
        }

        throw new RuntimeException("TestNGAssertionStrategy not found on classpath. Checked: " + candidates);
    }
}
