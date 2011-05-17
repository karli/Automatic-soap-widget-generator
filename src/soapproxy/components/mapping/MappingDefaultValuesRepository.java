package soapproxy.components.mapping;

public interface MappingDefaultValuesRepository {

  public boolean hasDefaultValue(String sourceUrl, String operation,  MessageType messageType, String messagePath);

  public String getDefaultValue(String sourceUrl, String operation,  MessageType messageType, String messagePath);

  void addDefaultValue(String sourceUrl, String operation, MessageType messageType, String messagePath,  String value);
}
