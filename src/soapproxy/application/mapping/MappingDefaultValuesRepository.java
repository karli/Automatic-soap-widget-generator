package soapproxy.application.mapping;

public interface MappingDefaultValuesRepository {

  public boolean hasDefaultValue(String wsdl, String operation, String messagePath);

  public String getDefaultValue(String wsdl, String operation, String messagePath);

  void addDefaultValue(String wsdl, String operation, String messagePath, String value);
}
