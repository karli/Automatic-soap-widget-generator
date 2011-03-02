package soapproxy.application.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MappingDefaultValuesRepositoryImpl implements MappingDefaultValuesRepository {

  private List<MappingDefaultValueRow> defaultValues = new ArrayList<MappingDefaultValueRow>();

  private MappingDefaultValuesDataSource dataSource;

  @Autowired
  public MappingDefaultValuesRepositoryImpl(MappingDefaultValuesDataSource dataSource) {
    this.dataSource = dataSource;
    try {
      defaultValues.addAll(dataSource.getAll());
    } catch (Exception e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }

  @Override
  public boolean hasDefaultValue(String wsdl, String operation,  MessageType messageType, String messagePath) {
    if (getDefaultValue(wsdl, operation, messageType, messagePath) != null){
      return true;
    } else {
      return false;
    }
  }

  @Override
  public String getDefaultValue(String wsdl, String operation, MessageType messageType, String messagePath) {
    for (MappingDefaultValueRow defaultValue : defaultValues) {
      if (defaultValue.getWsdl().equals(wsdl)
              && defaultValue.getOperation().equals(operation)
              && defaultValue.getMessageType().equals(messageType)
              && defaultValue.getPath().equals(messagePath)) {
        return defaultValue.getValue();
      }
    }
    return null;
  }

  @Override
  public void addDefaultValue(String wsdl, String operation, MessageType messageType, String messagePath,  String value) {
    MappingDefaultValueRow newRow = new MappingDefaultValueRow(wsdl, operation, messageType, messagePath,  value);
    defaultValues.add(newRow);
  }
}
