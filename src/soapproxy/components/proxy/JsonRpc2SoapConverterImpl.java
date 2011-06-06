package soapproxy.components.proxy;

import com.eviware.soapui.impl.wsdl.support.soap.SoapMessageBuilder;
import com.eviware.soapui.impl.wsdl.support.soap.SoapUtils;
import com.eviware.soapui.impl.wsdl.support.soap.SoapVersion;
import com.eviware.soapui.impl.wsdl.support.soap.SoapVersion11;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlContext;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlUtils;
import com.ibm.wsdl.factory.WSDLFactoryImpl;
import com.ibm.wsdl.xml.WSDLReaderImpl;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import soapproxy.components.proxy.JsonRpc2SoapConverter;
import soapproxy.components.wsdl.WsdlContextCache;
import soapproxy.util.Xml2JsonConverter;

import javax.wsdl.*;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;


public class JsonRpc2SoapConverterImpl implements JsonRpc2SoapConverter {

  private WsdlContextCache wsdlContextCache;

  public static final String ATTRIBUTE_ELEMENT_PREFIX = "_attr_";
  public static final String VALUE_ELEMENT_NAME = "_value_";

  public JsonRpc2SoapConverterImpl(WsdlContextCache wsdlContextCache) {
    this.wsdlContextCache = wsdlContextCache;
  }

  @Override
  public String convert(String jsonRpcRequestParams, String wsdlUri, String operationName) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonRequestParamsNode = mapper.readValue(jsonRpcRequestParams, JsonNode.class);
    // create a soap request
    XmlObject soapRequest = this.convertToSoapMessage(wsdlUri, operationName, jsonRequestParamsNode);
    // get a response from the service
    SOAPMessage response = this.doRequest(soapRequest, wsdlUri);
    OutputStream out = new ByteArrayOutputStream();
    response.writeTo(out);
    String responseString = out.toString();
    XmlObject responseXml = XmlObject.Factory.parse(responseString);

    Xml2JsonConverter converter = new Xml2JsonConverter();
    JsonNode jsonNode = converter.convert(responseXml.xmlText());

    // show only contents of envelope
    return jsonNode.get("Envelope").toString();
  }


 private SOAPMessage doRequest(XmlObject soapRequest, String wsdlUri) throws SOAPException, WSDLException, IOException {
    System.out.println(soapRequest);
    SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
    SOAPConnection soapConnection = soapConnectionFactory.createConnection();
    MessageFactory mf = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
    SOAPMessage requestMessage = mf.createMessage();
    requestMessage.getSOAPPart().setContent(new DOMSource(soapRequest.getDomNode()));
    Definition definition = getDefinition(wsdlUri);

    // lets currently assume that there is only one service with only one port
    Service service = (Service) definition.getServices().values().toArray()[0];
    Port port = (Port) service.getPorts().values().toArray()[0];
    String endpoint = WsdlUtils.getSoapEndpoint(port);
    SOAPMessage reply = soapConnection.call(requestMessage, endpoint);
    //Close the connection
    soapConnection.close();
    return reply;
  }

  private Definition getDefinition(String wsdlUri) throws WSDLException {
    WSDLFactory factory = WSDLFactoryImpl.newInstance();
    WSDLReaderImpl reader = (WSDLReaderImpl) factory.newWSDLReader();
    return reader.readWSDL(wsdlUri);
  }

  private XmlObject convertToSoapMessage(String wsdlUri, String operationName, JsonNode jsonRequestParams) throws Exception {
    SoapMessageBuilder soapMessageBuilder = new SoapMessageBuilder(wsdlContextCache.getContextForWsdlDocument(wsdlUri));

    Definition definition = getDefinition(wsdlUri);
    BindingOperation bindingOperation = WsdlUtils.findBindingOperation(definition, operationName);
    String soapMessageTemplate = soapMessageBuilder.buildSoapMessageFromInput(bindingOperation, true);
    return populateMessageTemplate(soapMessageTemplate, jsonRequestParams);
  }

  private XmlObject populateMessageTemplate(String soapMessageTemplateString, JsonNode jsonRequestParams) throws XmlException {
    XmlObject soapMessageTemplate = XmlObject.Factory.parse(soapMessageTemplateString);
    XmlCursor rootCursor = soapMessageTemplate.newCursor();
    rootCursor.toFirstChild();
    XmlObject envelopeObject = rootCursor.getObject();
    rootCursor.dispose();
    XmlCursor cursor = envelopeObject.newCursor();

    if (jsonRequestParams.isArray()) {
      jsonRequestParams = jsonRequestParams.get(0);
    }

    transferValues(jsonRequestParams, cursor);
    cursor.dispose();

    return soapMessageTemplate;
  }

  /**
   * Starting from the root of the soap request message body,
   * transfer all values that can be found in the JSON request params.
   *
   * @param jsonNode
   * @param cursor
   */
  private void transferValues(JsonNode jsonNode, XmlCursor cursor) {
    // save current location before navigating forward
    cursor.push();

    if (jsonNode.isArray()) {
      int childCount = jsonNode.size();
      // make necessary copies
      cursor.push();
      if (childCount > 1) {
        cursor.toFirstChild();
        XmlCursor childCursor = cursor.getObject().newCursor();
        cursor.toPrevToken();

        while (--childCount > 0) {
          childCursor.copyXml(cursor);
        }
      }
      cursor.pop();

      boolean isFirstIteration = true;
      for (Iterator it = jsonNode.getElements(); it.hasNext(); isFirstIteration = false) {
        JsonNode element = (JsonNode) it.next();
        if (isFirstIteration) {
          cursor.toFirstChild();
        } else {
          // because cursor position is still before the start of the previous child
          // just calling toNextSibling would give us previous child
          cursor.toNextSibling();
          cursor.toNextSibling();
        }
        cursor.toPrevToken();
        transferValues(element, cursor);
      }

    } else if (jsonNode.isContainerNode()) {

      while (!cursor.toNextToken().isStart()) ;

      for (Iterator<String> it = jsonNode.getFieldNames(); it.hasNext();) {
        String fieldName = it.next();
        JsonNode field = jsonNode.get(fieldName);
        // check for attribute and value nodes
        if (field.equals(VALUE_ELEMENT_NAME)) {
          cursor.setTextValue(field.getValueAsText());
          continue;
        }
        if (fieldName.startsWith(ATTRIBUTE_ELEMENT_PREFIX)) {
          String attributeName = fieldName.replaceFirst(ATTRIBUTE_ELEMENT_PREFIX, "");
          setAttributeValue(attributeName, jsonNode.getValueAsText(), cursor);
          continue;
        }
        cursor.push();
        if (moveCursorByLocalName(cursor, fieldName)) {
          transferValues(field, cursor);
        }
        cursor.pop();
      }
    } else if (jsonNode.isValueNode()) {
      cursor.setTextValue(jsonNode.getValueAsText());
    }
    cursor.pop();
  }

  private void setAttributeValue(String attributeName, String value, XmlCursor cursor) {
    cursor.push();
    while (!cursor.toNextToken().isEnd()) {
      if (cursor.isAttr() && cursor.getName().getLocalPart().equals(attributeName)) {
        cursor.setTextValue(value);
      }
    }
    cursor.pop();
  }

  private boolean moveCursorByLocalName(XmlCursor cursor, String fieldName) {
    boolean childExists;
    if (!(childExists = cursor.getName().getLocalPart().equals(fieldName))) {
      while (cursor.toNextSibling() && !(childExists = cursor.getName().getLocalPart().equals(fieldName))) ;
    }

    return childExists;
  }
}
