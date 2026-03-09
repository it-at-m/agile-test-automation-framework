package ataf.rest.helper;

import ataf.core.assertions.CustomAssertions;
import ataf.core.helpers.ResourceHelper;
import ataf.core.logging.ScenarioLogManager;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.InputStream;
import java.io.StringReader;

/**
 * Helper class for validating REST API responses against JSON and XML schemas.
 *
 * <p>
 * JSON schema validation is implemented using RestAssured's json-schema-validator module.
 * XML schema validation is implemented using the standard JAXP {@link SchemaFactory}.
 * </p>
 */
public final class SchemaValidationHelper {

    private SchemaValidationHelper() {
        // Utility class
    }

    /**
     * Validates the given response body against a JSON schema located at the specified path.
     *
     * <p>
     * The path is typically interpreted as a classpath resource, e.g.:
     * {@code "schemas/user-schema.json"} in {@code src/test/resources/schemas}.
     * If you prefer file system paths, you can use RestAssured's
     * {@code matchesJsonSchema(new File(...))}
     * in a dedicated overload.
     * </p>
     *
     * @param response the REST-assured response whose JSON body should be validated
     * @param schemaPath the classpath path to the JSON schema
     */
    public static void validateJsonSchema(Response response, String schemaPath) {
        ScenarioLogManager.getLogger()
                .info("Validating JSON response body against schema [{}]", schemaPath);

        try {
            response.then()
                    .assertThat()
                    .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(schemaPath));
        } catch (AssertionError assertionError) {
            // Schema validation failed
            ExceptionManagerAPI.process(assertionError);
            CustomAssertions.fail(
                    "JSON schema validation failed for schema [" + schemaPath + "]: " + assertionError.getMessage());
        } catch (Exception e) {
            // Any other unexpected errors
            ExceptionManagerAPI.process(e);
            CustomAssertions.fail(
                    "Unexpected error during JSON schema validation for schema [" + schemaPath + "]: " + e.getMessage(),
                    e);
        }
    }

    /**
     * Validates the given response body (expected to be XML) against an XSD schema.
     *
     * <p>
     * The {@code xsdPath} may point to a classpath resource or a file system path and is
     * resolved by {@link ResourceHelper}.
     * </p>
     *
     * @param response the REST-assured response whose XML body should be validated
     * @param xsdPath the path to the XSD schema (classpath or file system)
     */
    public static void validateXmlSchema(Response response, String xsdPath) {
        ScenarioLogManager.getLogger()
                .info("Validating XML response body against XSD [{}]", xsdPath);

        String xmlBody = response.getBody() != null ? response.getBody().asString() : null;

        if (xmlBody == null || xmlBody.isBlank()) {
            CustomAssertions.fail("XML schema validation failed: response body is empty or null.");
            return;
        }

        try (InputStream xsdStream = ResourceHelper.loadAsStream(xsdPath)) {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(new StreamSource(xsdStream));
            Validator validator = schema.newValidator();

            try (StringReader stringReader = new StringReader(xmlBody)) {
                validator.validate(new StreamSource(stringReader));
            }

        } catch (SAXException e) {
            // XML does not conform to schema
            ExceptionManagerAPI.process(e);
            CustomAssertions.fail(
                    "XML schema validation failed for XSD [" + xsdPath + "]: " + e.getMessage(),
                    e);
        } catch (Exception e) {
            // I/O or other errors
            ExceptionManagerAPI.process(e);
            CustomAssertions.fail(
                    "Unexpected error during XML schema validation for XSD [" + xsdPath + "]: " + e.getMessage(),
                    e);
        }
    }
}
