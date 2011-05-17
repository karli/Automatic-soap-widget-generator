package soapproxy.components.smd;

public class SmdGeneratorImpl implements SmdGenerator {
  @Override
  public String getSmd(String baseUrl, String wsdlDocumentUrl, String operationName) {
    String smd = "{\n" +
              "    transport:\"JSONP\",\n" +
              "    envelope:\"JSON-RPC-2.0\", // We will use JSON-RPC\n" +
              "    SMDVersion:\"2.0\",\n" +
              "    services: {\n" +
              "      " + operationName + ": {\n" +
              "        // this defines the URL to connect for the services\n" +
              "        target:   \"" + baseUrl + "/proxy?wsdl=" + wsdlDocumentUrl + "&operation=" + operationName + "\"\n" +
              "      }\n" +
              "    }\n" +
              "  }";
    return smd;
  }
}
