package soapproxy.application.mapping;

public interface MappingGenerator {
  String getMapping(String wsdlUri, String operation, String jsonSchemaUrl) throws Exception;
}
