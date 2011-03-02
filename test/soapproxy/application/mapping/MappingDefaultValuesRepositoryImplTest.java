package soapproxy.application.mapping;

import org.dom4j.DocumentException;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

public class MappingDefaultValuesRepositoryImplTest {

  @Test
  public void shouldAddDefaultValue() throws Exception {
    String wsdl = "http://myserver.com?wsdl";
    String operation = "myop";
    String path = "/parent/child/value/";
    String value = "defaultValue";
    MappingDefaultValuesRepository repo = new MappingDefaultValuesRepositoryImpl(new MappingDefaultValuesDataSourceStub());
    repo.addDefaultValue(wsdl, operation, MessageType.INPUT, path, value);
    assertTrue(repo.hasDefaultValue(wsdl, operation, MessageType.INPUT, path));
  }

  @Test
  public void shouldGetNullIfDefaultValueDoesNotExist() throws Exception {
    String wsdl = "http://myserver.com?wsdl";
    String operation = "myop";
    String path = "/parent/child/value/";
    String value = "defaultValue";
    MappingDefaultValuesRepository repo = new MappingDefaultValuesRepositoryImpl(new MappingDefaultValuesDataSourceStub());
    repo.addDefaultValue(wsdl, operation, MessageType.INPUT, path, value);
    assertNull(repo.getDefaultValue("http://wrongserver.com?wsdl", operation, MessageType.INPUT, path));
    assertNull(repo.getDefaultValue(wsdl, "wrongop", MessageType.INPUT, path));
    assertNull(repo.getDefaultValue(wsdl, operation, MessageType.INPUT, "wrong/path"));
  }

  @Test
  public void shouldGetDefaultValueIfItExists() {
    String wsdl = "http://myserver.com?wsdl";
    String operation = "myop";
    String path = "/parent/child/value/";
    String value = "defaultValue";
    MappingDefaultValuesRepository repo = new MappingDefaultValuesRepositoryImpl(new MappingDefaultValuesDataSourceStub());
    repo.addDefaultValue(wsdl, operation, MessageType.INPUT, path, value);
    assertEquals(value, repo.getDefaultValue(wsdl, operation, MessageType.INPUT, path));
  }

  private class MappingDefaultValuesDataSourceStub implements MappingDefaultValuesDataSource {
    @Override
    public List<MappingDefaultValueRow> getAll() throws DocumentException, MalformedURLException {
      return new ArrayList<MappingDefaultValueRow>();
    }
  }
}
