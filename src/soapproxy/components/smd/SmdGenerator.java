package soapproxy.components.smd;

public interface SmdGenerator {
  String getSmd(String baseUrl, String wsdlDocumentUrl, String operationName);
}
