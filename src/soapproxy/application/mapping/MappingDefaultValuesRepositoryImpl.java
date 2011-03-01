package soapproxy.application.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MappingDefaultValuesRepositoryImpl implements MappingDefaultValuesRepository {

  private Map<String, Map<String, Map<String, String>>> defaultValues = new HashMap<String, Map<String, Map<String, String>>>();

  private MappingDefaultValuesDataSource dataSource;

  @Autowired
  public MappingDefaultValuesRepositoryImpl(MappingDefaultValuesDataSource dataSource) {
    this.dataSource = dataSource;
    try {
      initDefaultValuesMap(dataSource.getAll());
    } catch (Exception e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }

  private void initDefaultValuesMap(List<MappingDefaultValueRow> mappingDefaultValueRows) {
    for (MappingDefaultValueRow mappingDefaultValueRow : mappingDefaultValueRows) {
      addDefaultValue(mappingDefaultValueRow.getWsdl(),
                      mappingDefaultValueRow.getOperation(),
                      mappingDefaultValueRow.getPath(),
                      mappingDefaultValueRow.getValue());
    }
  }

  @Override
  public boolean hasDefaultValue(String wsdl, String operation, String messagePath) {
    if (!defaultValues.containsKey(wsdl)) {
      return false;
    }
    Map<String, Map<String, String>> opMap = defaultValues.get(wsdl);
    if (!opMap.containsKey(operation)) {
      return false;
    }
    Map<String, String> valueMap = opMap.get(operation);
    if (valueMap.containsKey(messagePath)) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public String getDefaultValue(String wsdl, String operation, String messagePath) {
    if (hasDefaultValue(wsdl, operation, messagePath)) {
      return defaultValues.get(wsdl).get(operation).get(messagePath);
    }
    else {
      return null;
    }
  }

  @Override
  public void addDefaultValue(String wsdl, String operation, String messagePath, String value) {
    Map<String, String> valueMap = getValueMap(wsdl, operation, messagePath);
    valueMap.put(messagePath, value);
  }

  private Map<String, String> getValueMap(String wsdl, String operation, String messagePath) {
    if (!defaultValues.containsKey(wsdl)) {
      defaultValues.put(wsdl, new HashMap<String, Map<String, String>>());
    }
    Map<String, Map<String, String>> operationMap = defaultValues.get(wsdl);
    if (!operationMap.containsKey(operation)) {
      operationMap.put(operation, new HashMap<String, String>());
    }
    return operationMap.get(operation);
  }
}
