package soapproxy.application.mapping;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XmlMappingDefaultValuesDataSource implements MappingDefaultValuesDataSource {

  private String xmlSource;
  private Document sourceDocument;
  private List<MappingDefaultValueRow> defaultValues = new ArrayList<MappingDefaultValueRow>();

  public String getXmlSource() {
    return xmlSource;
  }

  public void setXmlSource(String xmlSource) {
    this.xmlSource = xmlSource;
  }

  public Document getSourceDocument() {
    return sourceDocument;
  }

  public void setSourceDocument(Document sourceDocument) {
    this.sourceDocument = sourceDocument;
  }

  @Override
  public List<MappingDefaultValueRow> getAll() throws DocumentException, MalformedURLException {
    if (getSourceDocument() == null){
      SAXReader saxReader = new SAXReader();
      setSourceDocument(saxReader.read(new URL(xmlSource)));
    }
    List defaultValueList = getSourceDocument().selectNodes("defaults/value");
    Iterator it = defaultValueList.iterator();
    while (it.hasNext()) {
      Element row = (Element)it.next();
      String value = row.getText();
      String sourceUrl = row.attributeValue("sourceUrl");
      String operation = row.attributeValue("operation");
      String path = row.attributeValue("path");
      MessageType messageType = MessageType.getByTypeValue(row.attributeValue("messageType"));
      MappingDefaultValueRow mappingDefaultValueRow = new MappingDefaultValueRow(sourceUrl, operation, messageType, path, value);
      defaultValues.add(mappingDefaultValueRow);
    }
    return defaultValues;
  }
}
