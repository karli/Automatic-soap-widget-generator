package soapproxy.application.schema;

import org.junit.Test;

public class DefaultJsonSchemaConverterTest {

  @Test
  public void testHelloWorld() throws Exception {
    String operationName = "sayHelloToAll";
    String path = this.getClass().getResource(".").getPath().replace("%20", " ");
    String wsdlUri = path + "/helloTest.wsdl";
    DefaultJsonSchemaConverter schemaConverter = new DefaultJsonSchemaConverter(wsdlUri, operationName, DefaultJsonSchemaConverter.MessageType.INPUT_MESSAGE);
    String result = schemaConverter.getJsonSchema();
  }
}
