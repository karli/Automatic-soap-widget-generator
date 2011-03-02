package soapproxy.application.mapping;

public interface MappingDefaultValuesRepository {

  public boolean hasDefaultValue(String wsdl, String operation,  MessageType messageType, String messagePath);

  public String getDefaultValue(String wsdl, String operation,  MessageType messageType, String messagePath);

  void addDefaultValue(String wsdl, String operation, MessageType messageType, String messagePath,  String value);
}
