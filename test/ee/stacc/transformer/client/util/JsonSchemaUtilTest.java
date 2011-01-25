package ee.stacc.transformer.client.util;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.junit.client.GWTTestCase;


public class JsonSchemaUtilTest extends GWTTestCase {

  public void testFindElementByPath() throws Exception {
    JSONObject jsonSchema = getJsonSchema();
    String path = "parent/child/leaf";
    JSONObject schemaElement = JsonSchemaUtil.findElementByPath(jsonSchema, path);
    assertTrue(schemaElement != null);
    assertTrue(JsonSchemaUtil.isSchemaElementRequired(schemaElement));
  }

  public JSONObject getJsonSchema() {
    String schemaDefinition = "" +
            "{\"type\":\"object\"," +
              "\"properties\":{" +
                "\"parent\":{" +
                  "\"type\":\"array\"," +
                  "\"items\":{" +
                    "\"type\":\"object\"," +
                    "\"properties\":{" +
                      "\"child\":{" +
                        "\"type\":\"object\"," +
                        "\"properties\":{" +
                          "\"leaf\":{\"type\":\"string\", \"required\":true}" +
                        "}" +
                      "}" +
                    "}" +
                  "}" +
                "}" +
              "}" +
            "}";
    JSONObject jsonSchema = JSONParser.parse(schemaDefinition).isObject();
    return jsonSchema;
  }

  @Override
  public String getModuleName() {
    return "ee.stacc.transformer.TransformerWidget";
  }
}
