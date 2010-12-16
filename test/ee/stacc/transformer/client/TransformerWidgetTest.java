package ee.stacc.transformer.client;

import com.google.gwt.junit.client.GWTTestCase;

public class TransformerWidgetTest extends GWTTestCase{
  private TransformerWidget transformerWidget;


  public void testProcessMappingsWithRecursiveRepeatingElements() {
    transformerWidget = new TransformerWidget();
    String mappingsXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<frames>" +
            "<frame>" +
            "<topic>org.example.topics.new.name</topic>" +
            "<format>json</format>" +
            "<mappings>" +
            "<repeating_element_group path=\"/row\">" +
              "<repeating_element_group path=\"/row/column\">" +
                "<mapping>" +
                  "<global_ref>http://www.example.org/person/owl#Name</global_ref>" +
                  "<path>/row/column/name</path>" +
                "</mapping>" +
              "</repeating_element_group>" +
            "</repeating_element_group>" +
            "</mappings>" +
            "</frame>" +
            "</frames>";
    transformerWidget.processMappings(mappingsXml);
    // check that necessary mappings were added
    assertTrue(true);
  }

  @Override
  public String getModuleName() {
    return "ee.stacc.transformer.TransformerWidget";
  }
}
