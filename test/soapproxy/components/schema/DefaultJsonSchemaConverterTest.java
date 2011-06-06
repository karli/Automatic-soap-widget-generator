package soapproxy.components.schema;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class DefaultJsonSchemaConverterTest {

  @Test
  public void shouldGenerateJsonSchema() throws Exception {
    DefaultJsonSchemaGenerator schemaConverter = new DefaultJsonSchemaGenerator(null, null, null, null);
    JsonNode actualResult = schemaConverter.getJsonSchemaObjectNode(getSoapMessage());
    actualResult.toString();
    String expected = "{\"type\":\"object\",\"properties\":{\"Header\":{\"required\":true,\"type\":\"object\",\"properties\":{\"SOATraderLicense\":{\"required\":true,\"type\":\"string\"}}},\"Body\":{\"required\":true,\"type\":\"object\",\"properties\":{\"findBusiness\":{\"required\":true,\"type\":\"object\",\"properties\":{\"registryCode\":{\"type\":\"string\"},\"languageId\":{\"required\":true,\"type\":\"string\"}}}}}}}";
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode expectedResult = objectMapper.readValue(expected, JsonNode.class);
    assertTrue(expectedResult.equals(actualResult));
  }

  private String getSoapMessage() {
    String soapMessageTemplate = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.soatrader.com/\" xmlns:eer=\"http://eer.soatrader.com/\" xmlns:sawsdl=\"http://www.w3.org/ns/sawsdl\">\n" +
            "   <soapenv:Header>\n" +
            "      <ws:SOATraderLicense sawsdl:modelReference=\"#soaLicense\">?</ws:SOATraderLicense>\n" +
            "   </soapenv:Header>\n" +
            "   <soapenv:Body>\n" +
            "      <eer:findBusiness>\n" +
            "         <!--Optional:-->\n" +
            "         <registryCode sawsdl:modelReference=\"#registryCode\">?</registryCode>\n" +
            "         <languageId sawsdl:modelReference=\"#languageId\">?</languageId>\n" +
            "      </eer:findBusiness>\n" +
            "   </soapenv:Body>\n" +
            "</soapenv:Envelope>";
    return soapMessageTemplate;
  }
}
