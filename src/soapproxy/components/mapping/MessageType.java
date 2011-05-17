package soapproxy.components.mapping;

public enum MessageType {
  INPUT("input"), OUTPUT("output");

  private String messageTypeValue;

  MessageType(String messageTypeValue) {
    this.messageTypeValue = messageTypeValue;
  }

  public static MessageType getByTypeValue(String typeValue) {
    for (MessageType messageType : MessageType.values()) {
      if (typeValue.equals(messageType.getMessageTypeValue())) {
        return messageType;
      }
    }
    return null;
  }

  public String getMessageTypeValue() {
    return messageTypeValue;
  }
}
