package soapproxy.components.proxy;

public interface JsonRpc2SoapConverter {
  String convert(String jsonRequest, String wsdlUri, String operationName) throws Exception;
}
