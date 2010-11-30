package soapproxy.application.mapping;

import com.eviware.soapui.impl.wsdl.support.soap.SoapUtils;
import com.eviware.soapui.impl.wsdl.support.soap.SoapVersion;
import org.apache.xmlbeans.XmlObject;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import soapproxy.util.SoapMessageBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.wsdl.BindingOperation;

public class SMBMappingGenerator extends DefaultMappingGenerator {

  private SoapMessageBuilder smb;

  public SMBMappingGenerator(String wsdlUri, String operationName, HttpServletRequest httpServletRequest) {
    super(wsdlUri, operationName, httpServletRequest);
    setSmb(new SoapMessageBuilder(getWsdlContext()));
  }

  @Override
  public Element getOutputFrame(BindingOperation bindingOperation) throws Exception {
    return generateFrame(getSmb().buildSoapMessageFromOutput(bindingOperation, true), true, getOutputTopic(), null);
  }

  @Override
  public Element getInputFrame(BindingOperation bindingOperation) throws Exception {
    return generateFrame(getSmb().buildSoapMessageFromInput(bindingOperation, true), false, getInputTopic(), getInputSchemaLocation());
  }

  private Element generateFrame(String soapMessage, boolean outgoingOnly, String topic, String schemaLocation) throws Exception {
    DOMElement frame = new DOMElement("frame");
    addTopic(frame, outgoingOnly, topic);
    // TODO format might not only be json, but also simple string
    addFormat(frame);
    addSchema(frame, schemaLocation);
    Element mappings = new DOMElement("mappings");
    frame.appendChild(mappings);

    addMappings(mappings, getBodyElement(soapMessage), "");

    return frame;
  }

  private void addMappings(Element mappings, Element messageElement, String currentPath) {

    boolean repeatingElementFlag = false;
    if (!messageElement.hasChildNodes()) {
      return;
    }

    NodeList nodeList = messageElement.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node instanceof Comment) {
        if (node.getNodeValue() != null && node.getNodeValue().contains("or more repetitions:")) {
          repeatingElementFlag = true;
        }
      }
      if (node instanceof Element) {
        Element element = (Element) node;
        String path = currentPath + "/" + element.getLocalName();
        if (repeatingElementFlag) {
          // add repeating element
          Element repeatingElement = addRepeatingElementGroup(mappings, path);
          // is it a leaf node?
          if (hasChildElements(element)) {
            // no
            addMappings(repeatingElement, element, path);
          } else {
            // repeating element that is of a simple type
            addMapping(repeatingElement, null, getGlobalReference(element));
          }
          // unset flag
          repeatingElementFlag = false;
        } else {
          if (hasChildElements(element)) {
            addMappings(mappings, element, path);
          } else {
            addMapping(mappings, path, getGlobalReference(element));
          }
        }
      }
    }

  }

  private boolean hasChildElements(Element element) {
    if (element.hasChildNodes()) {
      NodeList nodeList = element.getChildNodes();
      for (int i = 0; i < nodeList.getLength(); i++) {
        if (nodeList.item(i) instanceof Element) {
          return true;
        }
      }
    }
    return false;  //To change body of created methods use File | Settings | File Templates.
  }


  private String getGlobalReference(Element element) {
    if (element.hasAttributeNS(MODEL_REFERENCE_ATTRIBUTE.getNamespaceURI(), MODEL_REFERENCE_ATTRIBUTE.getLocalPart())) {
      return element.getAttributeNodeNS(MODEL_REFERENCE_ATTRIBUTE.getNamespaceURI(), MODEL_REFERENCE_ATTRIBUTE.getLocalPart()).getNodeValue();
    }
    return null;
  }

  private Element getBodyElement(String soapMessage) throws Exception {
    XmlObject soapMessageObject = XmlObject.Factory.parse(soapMessage);
    XmlObject bodyObject = SoapUtils.getBodyElement(soapMessageObject, SoapVersion.Soap11);
    return (Element) bodyObject.getDomNode();
  }

  public SoapMessageBuilder getSmb() {
    return smb;
  }

  public void setSmb(SoapMessageBuilder smb) {
    this.smb = smb;
  }
}