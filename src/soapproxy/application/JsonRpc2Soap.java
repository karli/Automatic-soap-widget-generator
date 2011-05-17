package soapproxy.application;

public interface JsonRpc2Soap {
  String convert(String jsonRequest, String wsdlUri, String operationName) throws Exception;
}
