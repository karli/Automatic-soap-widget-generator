package ee.stacc.transformer.client;

import com.google.gwt.json.client.*;
import com.google.gwt.junit.client.GWTTestCase;
import ee.stacc.transformer.client.data.DataPackage;
import ee.stacc.transformer.client.data.json.JsonDataFrame;

import java.util.List;

public class TransformerWidgetTest extends GWTTestCase{
  private TransformerWidget transformerWidget;
  private static final String GLOBAL_REF = "http://www.example.org/person/owl#Name";
  private static final String TOPIC = "org.example.topics.one";

  private static final String TOPIC2 = "org.example.topics.two";


  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();    //To change body of overridden methods use File | Settings | File Templates.
    setUpTransformerWidget();
  }

  private void setUpTransformerWidget() {
    transformerWidget = new TransformerWidget();
    String mappingsXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<frames>" +
            "<frame>" +
            "<topic>" + TOPIC + "</topic>" +
            "<format>json</format>" +
            "<mappings>" +
            "<repeating_element_group path=\"/row\">" +
              "<repeating_element_group path=\"/row/column\">" +
                "<mapping>" +
                  "<global_ref>" + GLOBAL_REF + "</global_ref>" +
                  "<path>/row/column/name</path>" +
                "</mapping>" +
              "</repeating_element_group>" +
            "</repeating_element_group>" +
            "</mappings>" +
            "</frame>" +
            "<frame>" +
            "<topic>" + TOPIC2 + "</topic>" +
            "<format>json</format>" +
            "<mappings>" +
              "<mapping>" +
                "<global_ref>" + GLOBAL_REF + "</global_ref>" +
                "<path>/name</path>" +
              "</mapping>" +
            "</mappings>" +
            "</frame>" +
            "</frames>";
    transformerWidget.processMappings(mappingsXml);
  }

  public void testProcessMappingsWithRecursiveRepeatingElements() {
    // check that necessary mappings were added
    Matcher matcher = transformerWidget.getMatcher();
    assertTrue(matcher.getReferenceMappingsToFrames().containsKey(GLOBAL_REF));
    assertEquals(2, matcher.getReferenceMappingsToFrames().get(GLOBAL_REF).size());
  }

  public void testGetUpdatedPackets() {
    // set up publisher data
    JSONObject publisherData = getPublisherDataWithrecursiveArrays();
    Matcher matcher = transformerWidget.getMatcher();
    List<DataPackage> packages = matcher.getUpdatedPackets(TOPIC, publisherData.getJavaScriptObject());
    assertEquals(3, packages.size());
  }

  private JSONObject getPublisherDataWithrecursiveArrays() {
    JSONObject publisherData = new JSONObject();
    JSONArray rows = new JSONArray();
    publisherData.put("row", rows);
    JSONObject row1 = new JSONObject();
    JSONObject row2 = new JSONObject();
    rows.set(0, row1);
    rows.set(1, row2);
    JSONArray columns1 = new JSONArray();
    row1.put("column", columns1);
    JSONArray columns2 = new JSONArray();
    row2.put("column", columns2);
    JSONObject column11 = new JSONObject();
    column11.put("name", new JSONString("name11"));
    columns1.set(0, column11);
    JSONObject column12 = new JSONObject();
    column12.put("name", new JSONString("name12"));
    columns1.set(1, column12);
    JSONObject column2 = new JSONObject();
    column2.put("name", new JSONString("name2"));
    columns2.set(0, column2);
    return publisherData;
  }

  public void testAddRawMappings() {
    String topic = "test.topic";
    String jsonSchemaData = "{\"type\":\"object\",\"properties\":{\"name\":{\"type\":\"string\"}}}";
    String mappings  =
            "<frames>" +
            "<frame>" +
            "<topic>" + topic + "</topic>" +
            "<format>json</format>" +
            "<schema_data>" + jsonSchemaData + "</schema_data>" +
            "<mappings>" +
                "<mapping>" +
                  "<global_ref>" + GLOBAL_REF + "</global_ref>" +
                  "<path>/name</path>" +
                "</mapping>" +
            "</mappings>" +
            "</frame>" +
            "</frames>";
    transformerWidget.addRawMapping(mappings);
    JsonDataFrame dataFrame = (JsonDataFrame)transformerWidget.getMatcher().getDataFrames().get(topic);
    assertTrue(dataFrame != null);
    assertTrue(dataFrame.getJsonSchema().containsKey("properties"));
    assertTrue(dataFrame.getJsonSchema().get("properties").isObject().containsKey("name"));
  }

  @Override
  public String getModuleName() {
    return "ee.stacc.transformer.TransformerWidget";
  }
}
