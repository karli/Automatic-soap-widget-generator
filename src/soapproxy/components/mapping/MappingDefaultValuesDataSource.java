package soapproxy.components.mapping;

import org.dom4j.DocumentException;

import java.net.MalformedURLException;
import java.util.List;

public interface MappingDefaultValuesDataSource {

  public List<MappingDefaultValueRow> getAll() throws DocumentException, MalformedURLException;
}
