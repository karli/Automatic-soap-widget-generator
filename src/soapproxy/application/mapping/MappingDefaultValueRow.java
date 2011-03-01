package soapproxy.application.mapping;

public class MappingDefaultValueRow {
  private String wsdl;
  private String operation;
  private String path;
  private String value;

  public MappingDefaultValueRow(String wsdl, String operation, String path, String value) {
    this.wsdl = wsdl;
    this.operation = operation;
    this.path = path;
    this.value = value;
  }

  public String getWsdl() {
    return wsdl;
  }

  public void setWsdl(String wsdl) {
    this.wsdl = wsdl;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
