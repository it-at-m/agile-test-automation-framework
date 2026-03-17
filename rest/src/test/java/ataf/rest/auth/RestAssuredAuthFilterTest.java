package ataf.rest.auth;

import ataf.core.assertions.CustomAssertions;
import ataf.core.assertions.strategy.impl.TestNGAssertionStrategy;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RestAssuredAuthFilterTest {
    @BeforeClass
    public void setUp() {
        CustomAssertions.setStrategy(new TestNGAssertionStrategy());
    }

    @Test
    public void filter_shouldApplyBearerHeader() {
        // Arrange
        AuthConfig authConfig = new AuthConfig(
                AuthType.BEARER, null, null, null, null,
                null, null, null, null, null,
                "token123", null, null, null);

        RestAssuredAuthFilter authFilter = new RestAssuredAuthFilter(authConfig, null);

        FilterableRequestSpecification requestSpecification = mock(FilterableRequestSpecification.class);
        FilterableResponseSpecification responseSpecification = mock(FilterableResponseSpecification.class);
        FilterContext filterContext = mock(FilterContext.class);

        when(filterContext.next(any(), any())).thenReturn(mock(Response.class));

        // Act
        Response response = authFilter.filter(requestSpecification, responseSpecification, filterContext);

        // Assert
        CustomAssertions.assertNotNull(response);
        verify(requestSpecification, atLeastOnce()).header("Authorization", "Bearer token123");
        verify(filterContext, times(1)).next(requestSpecification, responseSpecification);
    }

    @Test
    public void filter_shouldApplyApiKeyQueryParam() {
        // Arrange
        AuthConfig authConfig = new AuthConfig(
                AuthType.API_KEY_QUERY, null, null, null, null,
                null, null, null, null, null,
                null, "api_key", "K123", ApiKeyLocation.QUERY);

        RestAssuredAuthFilter authFilter = new RestAssuredAuthFilter(authConfig, null);

        FilterableRequestSpecification requestSpecification = mock(FilterableRequestSpecification.class);
        FilterableResponseSpecification responseSpecification = mock(FilterableResponseSpecification.class);
        FilterContext filterContext = mock(FilterContext.class);

        when(filterContext.next(any(), any())).thenReturn(mock(Response.class));

        // Capture queryParam calls
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        when(requestSpecification.queryParam(nameCaptor.capture(), valueCaptor.capture())).thenReturn(requestSpecification);

        // Act
        authFilter.filter(requestSpecification, responseSpecification, filterContext);

        // Assert
        verify(requestSpecification, atLeastOnce()).queryParam(eq("api_key"), eq("K123"));
        CustomAssertions.assertEquals(nameCaptor.getAllValues(), List.of("api_key"));
        CustomAssertions.assertEquals(valueCaptor.getAllValues(), List.of("K123"));
    }
}
