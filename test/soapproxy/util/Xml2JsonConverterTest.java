package soapproxy.util;

import org.codehaus.jackson.JsonNode;
import org.junit.Test;

public class Xml2JsonConverterTest {

  @Test
  public void testConvertingElementWithAttributes() {
    String xml = "<elem attr1=\"val1\" attr2=\"val2\">elemval1</elem>";
    Xml2JsonConverter converter = new Xml2JsonConverter();
    JsonNode result = converter.convert(xml);
    // TODO assert :)
  }

  @Test
  public void testConvertinArrayOfElementsWithAttributes() {
    String xml = "<parent><normal>blah</normal><elem attr1=\"val1\" attr2=\"val2\">elemval1</elem><elem attr1=\"val12\" attr2=\"val22\">elemval2</elem></parent>";
    Xml2JsonConverter converter = new Xml2JsonConverter();
    JsonNode result = converter.convert(xml);
  }
}
