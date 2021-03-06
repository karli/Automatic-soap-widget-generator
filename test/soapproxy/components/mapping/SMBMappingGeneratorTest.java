package soapproxy.components.mapping;

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
    mappingGenerator.setWsdlUri(MappingDefaultValuesDataSourceStub.SOURCE_URL);
    mappingGenerator.setMappingDefaultValuesRepository(new MappingDefaultValuesRepositoryImpl(new MappingDefaultValuesDataSourceStub()));
    Element generatedFrame = mappingGenerator.generateFrame(getSoapMessageTemplate(), true, "topic", null, MessageType.INPUT);
    Diff xmlDiff = new Diff(((DOMElement)generatedFrame).asXML(), getExpectedFrameWithDefaults());
    assertTrue(xmlDiff.toString(), xmlDiff.similar());
  }

  @Test
  public void shouldNotGenerateMappingWithDefaultValue() throws Exception {
    SMBMappingGenerator mappingGenerator = new SMBMappingGenerator();
    mappingGenerator.setOperation(MappingDefaultValuesDataSourceStub.OP);
    mappingGenerator.setWsdlUri(MappingDefaultValuesDataSourceStub.SOURCE_URL);
    mappingGenerator.setMappingDefaultValuesRepository(new MappingDefaultValuesRepositoryImpl(new MappingDefaultValuesDataSourceStub()));
    Element generatedFrame = mappingGenerator.generateFrame(getSoapMessageTemplate(), true, "topic", null, MessageType.OUTPUT);
    Diff xmlDiff = new Diff(((DOMElement)generatedFrame).asXML(), getExpectedFrameWithDefaults());
    assertFalse(xmlDiff.toString(), xmlDiff.similar());
  }

  @Test
  public void shouldGenerateInputMappingAndNotIncludeElementWithoutGlobalRef() throws Exception {
    SMBMappingGenerator mappingGenerator = new SMBMappingGenerator();
    Element generatedFrame = mappingGenerator.generateFrame(getSoapMessageTemplateWithoutModelRef(), true, "topic", null, MessageType.OUTPUT);
    Diff xmlDiff = new Diff(((DOMElement)generatedFrame).asXML(), getExpectedFrameWithoutDefaultsExcludingElementWithoutGlobalRef());
    assertTrue(xmlDiff.toString(), xmlDiff.similar());
  }

  private String getExpectedFrameWithoutDefaultsExcludingElementWithoutGlobalRef() {
    String frameXml = "<frame>" +
            "<topic outgoing_only=\"true\">topic</topic>" +
            "<format>json</format>" +
            "<mappings>" +
            "<mapping>" +
            "<global_ref>#soaLicense</global_ref>" +
            "<path>/Header/SOATraderLicense</path>" +
            "</mapping>" +
            "<mapping>" +
            "<global_ref>#languageId</global_ref>" +
            "<path>/Body/findBusiness/languageId</path>" +
            "</mapping>" +
            "</mappings>" +
            "</frame>";
    return frameXml;
  }

  private String getExpectedFrameWithDefaults() {
    String frameXml = "<frame>" +
            "<topic outgoing_only=\"true\">topic</topic>" +
            "<format>json</format>" +
            "<mappings>" +
            "<mapping><global_ref>#registryCode</global_ref><path>/Body/findBusiness/registryCode</path></mapping>" +
            "<mapping>" +
            "<global_ref>#languageId</global_ref>" +
            "<path>/Body/findBusiness/languageId</path>" +
            "<default>" + MappingDefaultValuesDataSourceStub.DEFAULT_VALUE + "</default>" +
            "</mapping>" +
            "</mappings>" +
            "</frame>";
    return frameXml;
  }

  private String getSoapMessageTemplateWithoutModelRef() {
    String soapMessageTemplate = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.soatrader.com/\" xmlns:eer=\"http://eer.soatrader.com/\" xmlns:sawsdl=\"http://www.w3.org/ns/sawsdl\">\n" +
            "   <soapenv:Header>\n" +
            "      <ws:SOATraderLicense sawsdl:modelReference=\"#soaLicense\">?</ws:SOATraderLicense>\n" +
            "   </soapenv:Header>\n" +
            "   <soapenv:Body>\n" +
            "      <eer:findBusiness>\n" +
            "         <!--Optional:-->\n" +
            "         <registryCode>?</registryCode>\n" +
            "         <languageId sawsdl:modelReference=\"#languageId\">?</languageId>\n" +
            "      </eer:findBusiness>\n" +
            "   </soapenv:Body>\n" +
            "</soapenv:Envelope>";
    return soapMessageTemplate;
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
    public static final String SOURCE_URL = "http://www.ebr.ee?wsdl";
    public static final String OP = "findBusiness";
    public static final String PATH = "/Body/findBusiness/languageId";
    public static final String DEFAULT_VALUE = "defaultLanguage";

    @Override
    public List<MappingDefaultValueRow> getAll() throws DocumentException, MalformedURLException {
      List<MappingDefaultValueRow> defaultValueList = new ArrayList<MappingDefaultValueRow>();
      defaultValueList.add(new MappingDefaultValueRow(SOURCE_URL, OP, MessageType.INPUT, PATH, DEFAULT_VALUE));
      return defaultValueList;
    }
  }
}