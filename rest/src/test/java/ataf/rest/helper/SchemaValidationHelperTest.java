package ataf.rest.helper;

import ataf.core.assertions.CustomAssertions;
import ataf.core.assertions.strategy.impl.TestNGAssertionStrategy;
import io.restassured.builder.ResponseBuilder;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests for {@link SchemaValidationHelper}.
 *
 * <p>
 * Hinweise zu Test-Ressourcen:
 * </p>
 * <ul>
 * <li>JSON-Schema: <code>src/test/resources/schemas/simple-json-schema.json</code></li>
 * <li>XML-Schema: <code>src/test/resources/schemas/simple-xml-schema.xsd</code></li>
 * </ul>
 *
 * <p>
 * Beispiel-Inhalte (minimal):
 * </p>
 *
 * <pre>
 * // simple-json-schema.json
 * {
 *   "$schema": "http://json-schema.org/draft-07/schema#",
 *   "type": "object",
 *   "properties": {
 *     "id":   { "type": "integer" },
 *     "name": { "type": "string" }
 *   },
 *   "required": ["id", "name"]
 * }
 *
 * // simple-xml-schema.xsd
 * &lt;xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"&gt;
 *   &lt;xs:element name="root"&gt;
 *     &lt;xs:complexType&gt;
 *       &lt;xs:sequence&gt;
 *         &lt;xs:element name="id" type="xs:int"/&gt;
 *         &lt;xs:element name="name" type="xs:string"/&gt;
 *       &lt;/xs:sequence&gt;
 *     &lt;/xs:complexType&gt;
 *   &lt;/xs:element&gt;
 * &lt;/xs:schema&gt;
 * </pre>
 */
public class SchemaValidationHelperTest {

    @BeforeClass
    public void setUpAssertions() {
        CustomAssertions.setStrategy(new TestNGAssertionStrategy());
    }

    private Response buildResponse(int statusCode, String contentType, String body) {
        ResponseBuilder builder = new ResponseBuilder();
        builder.setStatusCode(statusCode);
        builder.setBody(body);
        if (contentType != null) {
            builder.setContentType(contentType);
        }
        return builder.build();
    }

    // -------------------------------------------------------------------------
    // JSON Schema Validation
    // -------------------------------------------------------------------------

    @Test
    public void validateJsonSchema_validBodyAndSchema_doesNotThrow() {
        // given
        Response response = buildResponse(
                200,
                "application/json",
                "{\"id\": 1, \"name\": \"test\"}");

        // when / then: sollte ohne AssertionError durchlaufen,
        // sofern das Schema "schemas/simple-json-schema.json" existiert und passt.
        SchemaValidationHelper.validateJsonSchema(response, "schemas/simple-json-schema.json");
    }

    @Test
    public void validateJsonSchema_invalidBody_violatesSchema_throwsAssertionError() {
        // given: Body fehlt required Feld "name"
        Response response = buildResponse(
                200,
                "application/json",
                "{\"id\": 1}");

        // when / then: Schema-Validierung sollte fehlschlagen
        CustomAssertions.assertThrows(
                AssertionError.class,
                () -> SchemaValidationHelper.validateJsonSchema(response, "schemas/simple-json-schema.json"));
    }

    @Test
    public void validateJsonSchema_missingSchema_throwsAssertionError() {
        // given
        Response response = buildResponse(
                200,
                "application/json",
                "{\"id\": 1, \"name\": \"test\"}");

        // when / then: nicht existierendes Schema -> Exception-Pfad im Helper
        CustomAssertions.assertThrows(
                AssertionError.class,
                () -> SchemaValidationHelper.validateJsonSchema(response, "schemas/non-existing-schema.json"));
    }

    // -------------------------------------------------------------------------
    // XML Schema Validation
    // -------------------------------------------------------------------------

    @Test
    public void validateXmlSchema_emptyBody_throwsAssertionError() {
        // given: leerer Body
        Response response = buildResponse(
                200,
                "application/xml",
                "");

        // when / then: sollte direkt im "empty or null"-Pfad scheitern
        CustomAssertions.assertThrows(
                AssertionError.class,
                () -> SchemaValidationHelper.validateXmlSchema(response, "schemas/simple-xml-schema.xsd"));
    }

    @Test
    public void validateXmlSchema_validBodyAndSchema_doesNotThrow() {
        // given
        String xml = "<root><id>1</id><name>test</name></root>";
        Response response = buildResponse(
                200,
                "application/xml",
                xml);

        // when / then: gültiges XML + passendes XSD -> kein Fehler
        SchemaValidationHelper.validateXmlSchema(response, "schemas/simple-xml-schema.xsd");
    }

    @Test
    public void validateXmlSchema_invalidBody_violatesXsd_throwsAssertionError() {
        // given: <name> fehlt
        String xml = "<root><id>1</id></root>";
        Response response = buildResponse(
                200,
                "application/xml",
                xml);

        // when / then: XSD-Validierung sollte mit SAXException-Pfad scheitern
        CustomAssertions.assertThrows(
                AssertionError.class,
                () -> SchemaValidationHelper.validateXmlSchema(response, "schemas/simple-xml-schema.xsd"));
    }

    @Test
    public void validateXmlSchema_missingXsd_throwsAssertionError() {
        String xml = "<root><id>1</id><name>test</name></root>";
        Response response = buildResponse(
                200,
                "application/xml",
                xml);

        CustomAssertions.assertThrows(
                AssertionError.class,
                () -> SchemaValidationHelper.validateXmlSchema(response, "schemas/non-existing-schema.xsd"));
    }
}
