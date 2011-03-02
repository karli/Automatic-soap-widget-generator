package soapproxy.application.mapping;

import org.custommonkey.xmlunit.Diff;
import org.dom4j.DocumentException;
import org.dom4j.dom.DOMElement;
import org.junit.Test;
import org.w3c.dom.Element;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class SMBMappingGeneratorTest {

  @Test
  public void shouldGenerateMappingWithDefaultValues() throws Exception {
    SMBMappingGenerator mappingGenerator = new SMBMappingGenerator();
    mappingGenerator.setOperation(MappingDefaultValuesDataSourceStub.OP);
    mappingGenerator.setWsdlUri(MappingDefaultValuesDataSourceStub.WSDL);
    mappingGenerator.setMappingDefaultValuesRepository(new MappingDefaultValuesRepositoryImpl(new MappingDefaultValuesDataSourceStub()));
    Element generatedFrame = mappingGenerator.generateFrame(getSoapMessageTemplate(), true, "topic", null, MessageType.INPUT);
    Diff xmlDiff = new Diff(((DOMElement)generatedFrame).asXML(), getExpectedFrame());
    assertTrue(xmlDiff.toString(), xmlDiff.similar());
  }

  @Test
  public void shouldNotGenerateMappingWithDefaultValue() throws Exception {
    SMBMappingGenerator mappingGenerator = new SMBMappingGenerator();
    mappingGenerator.setOperation(MappingDefaultValuesDataSourceStub.OP);
    mappingGenerator.setWsdlUri(MappingDefaultValuesDataSourceStub.WSDL);
    mappingGenerator.setMappingDefaultValuesRepository(new MappingDefaultValuesRepositoryImpl(new MappingDefaultValuesDataSourceStub()));
    Element generatedFrame = mappingGenerator.generateFrame(getSoapMessageTemplate(), true, "topic", null, MessageType.OUTPUT);
    Diff xmlDiff = new Diff(((DOMElement)generatedFrame).asXML(), getExpectedFrame());
    assertFalse(xmlDiff.toString(), xmlDiff.similar());
  }

  private String getExpectedFrame() {
    String frameXml = "<frame>" +
            "<topic outgoing_only=\"true\">topic</topic>" +
            "<format>json</format>" +
            "<mappings>" +
            "<mapping><global_ref>#registryCode</global_ref><path>/findBusiness/registryCode</path></mapping>" +
            "<mapping>" +
            "<global_ref>#languageId</global_ref>" +
            "<path>/findBusiness/languageId</path>" +
            "<default>" + MappingDefaultValuesDataSourceStub.DEFAULT_VALUE + "</default>" +
            "</mapping>" +
            "</mappings>" +
            "</frame>";
    return frameXml;
  }

  private String getSoapMessageTemplate() {
    String soapMessageTemplate = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.soatrader.com/\" xmlns:eer=\"http://eer.soatrader.com/\" xmlns:sawsdl=\"http://www.w3.org/ns/sawsdl\">\n" +
            "   <soapenv:Header>\n" +
            "      <ws:SOATraderLicense>?</ws:SOATraderLicense>\n" +
            "   </soapenv:Header>\n" +
            "   <soapenv:Body>\n" +
            "      <eer:findBusiness>\n" +
            "         <!--Optional:-->\n" +
            "         <registryCode sawsdl:modelReference=\"#registryCode\">?</registryCode>\n" +
            "         <languageId sawsdl:modelReference=\"#languageId\">?</languageId>\n" +
            "      </eer:findBusiness>\n" +
            "   </soapenv:Body>\n" +
            "</soapenv:Envelope>";
    return soapMessageTemplate;
  }

  private class MappingDefaultValuesDataSourceStub implements MappingDefaultValuesDataSource {
    public static final String WSDL = "http://www.ebr.ee?wsdl";
    public static final String OP = "findBusiness";
    public static final String PATH = "/findBusiness/languageId";
    public static final String DEFAULT_VALUE = "defaultLanguage";

    @Override
    public List<MappingDefaultValueRow> getAll() throws DocumentException, MalformedURLException {
      List<MappingDefaultValueRow> defaultValueList = new ArrayList<MappingDefaultValueRow>();
      defaultValueList.add(new MappingDefaultValueRow(WSDL, OP, MessageType.INPUT, PATH, DEFAULT_VALUE));
      return defaultValueList;
    }
  }
}