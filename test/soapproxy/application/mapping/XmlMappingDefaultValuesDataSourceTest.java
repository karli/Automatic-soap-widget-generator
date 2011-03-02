package soapproxy.application.mapping;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;

import static junit.framework.Assert.*;

public class XmlMappingDefaultValuesDataSourceTest {

  private Document sourceDocument;
  XmlMappingDefaultValuesDataSource dataSource;

  @Before
  public void init() throws DocumentException {
    SAXReader saxReader = new SAXReader();
    sourceDocument = saxReader.read(new StringReader(getSourceDocumentContent()));
    dataSource = new XmlMappingDefaultValuesDataSource();
    dataSource.setSourceDocument(sourceDocument);
  }

  private String getSourceDocumentContent() {
    String testContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<defaults>" +
            "<value wsdl=\"http://www.myserver.com?wsdl\" operation=\"myOp\" path=\"/my/path\" messageType=\"input\">myDefault</value>" +
            "<value wsdl=\"http://www.yourserver.com?wsdl\" operation=\"yourOp\" path=\"/your/path\" messageType=\"output\">yourDefault</value>" +
            "</defaults>";
    return testContent;
  }

  @Test
  public void shouldGetAllDefaultValues() throws Exception {
    // TODO: write more specific tests
    assertEquals(2, dataSource.getAll().size());
  }
}
