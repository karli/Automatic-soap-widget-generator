package soapproxy.application.mapping;

import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlContext;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlUtils;
import org.apache.xmlbeans.SchemaAnnotation;
import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.SchemaLocalElement;
import org.apache.xmlbeans.SchemaParticle;
import org.apache.xmlbeans.SchemaType;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.Element;

import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Part;
import javax.xml.namespace.QName;

public class DefaultMappingGenerator extends AbstractMappingGenerator {

  private WsdlContext wsdlContext;
  private String wsdlUri;
  private String operation;
  private String jsonSchemaUrl;
  public static final QName MODEL_REFERENCE_ATTRIBUTE = new QName("http://www.w3.org/ns/sawsdl", "modelReference");

  @Override
  public String getMapping(String wsdlUri, String operation, String jsonSchemaUrl) throws Exception {
    setWsdlUri(wsdlUri);
    setOperation(operation);
    setWsdlContext(new WsdlContext(wsdlUri));
    setJsonSchemaUrl(jsonSchemaUrl);
    return generateMapping();
  }

  protected String generateMapping() throws Exception {
    BindingOperation bindingOperation = getBindingOperation();
    return "<?xml version=\"1.0\" ?><frames>"
           + getOutputFrameAsXml(bindingOperation)
           + getInputFrameAsXml(bindingOperation)
           + "</frames>";
  }

  protected String getOutputFrameAsXml(BindingOperation bindingOperation) throws Exception {
    Element outputFrame = getOutputFrame(bindingOperation);
    String outputFrameAsXml = ((DOMElement)outputFrame).asXML();
    return outputFrameAsXml;
  }

  protected Element getOutputFrame(BindingOperation bindingOperation) throws Exception {
    Part[] outputParts = WsdlUtils.getOutputParts(bindingOperation);
    Element outputFrame = generateOutputFrame(outputParts);
    return outputFrame;
  }

  protected String getInputFrameAsXml(BindingOperation bindingOperation) throws Exception {
    Element inputFrame = getInputFrame(bindingOperation);
    String inputFrameAsXml = ((DOMElement)inputFrame).asXML();
    return inputFrameAsXml;
  }

  protected BindingOperation getBindingOperation() throws Exception {
    Definition definition = getWsdlContext().getDefinition();
    BindingOperation bindingOperation = WsdlUtils.findBindingOperation(definition, getOperation());
    return bindingOperation;
  }

  protected Element generateOutputFrame(Part[] outputParts) throws Exception {
    return generateFrame(outputParts, true, getOutputTopic(), null, MessageType.OUTPUT);
  }

  protected Element getInputFrame(BindingOperation bindingOperation) throws Exception {
    Part[] inputParts = WsdlUtils.getInputParts(bindingOperation);
    return generateFrame(inputParts, false, getInputTopic(), getJsonSchemaUrl(), MessageType.INPUT);
  }

  protected String getOutputTopic() {
    return "ee.stacc.soapwidgetgenerator." + getCommonTopic() + ".output";
  }

  protected String getInputTopic() {
    return "ee.stacc.soapwidgetgenerator." + getCommonTopic() + ".input";
  }

  protected String getCommonTopic() {
    String clearedWsdl = getWsdlUri().replaceAll("\\W", "-");
    return clearedWsdl + "." + getOperation();
  }

  private Element generateFrame(Part[] parts, boolean outgoingOnly, String topic, String schemaLocation, MessageType messageType) throws Exception {
    Element frame = new DOMElement("frame");
    addTopic(frame, outgoingOnly, topic);
    // TODO format might not only be json, but also simple string
    addFormat(frame);
    addSchema(frame, schemaLocation);
    Element mappings = new DOMElement("mappings");
    frame.appendChild(mappings);

    for(Part part : parts) {
      addPartMapping(mappings, part, messageType);
    }

    return frame;
  }

  protected void addSchema(Element frame, String schemaLocation) {
    if (schemaLocation != null) {
      DOMElement schema = new DOMElement("schema");
      schema.setText(schemaLocation);
      frame.appendChild(schema);
    }
  }

  private void addPartMapping(Element mappings, Part part, MessageType messageType) throws Exception {
    // get part type
    QName elementName = part.getElementName();
    if (elementName != null) {
      addMappingFromPartElement(mappings, part, messageType);
    } else {
      addMappingFromPartType(mappings, part, messageType);
    }
  }

  private void addMappingFromPartType(Element mappings, Part part, MessageType messageType) throws Exception {
    SchemaType schemaTypeForPart = WsdlUtils.getSchemaTypeForPart(getWsdlContext(), part);
    if (schemaTypeForPart.isPrimitiveType()) {
      addMappingFromPrimitivePartType(mappings, part, messageType);
    }
  }

  private void addMappingFromPartElement(Element mappings, Part part, MessageType messageType) throws Exception {
    SchemaGlobalElement element = getWsdlContext().getSchemaTypeLoader().findElement(part.getElementName());
    String name = element.getName().getLocalPart();
    SchemaType type = element.getType();
    // TODO: process element attributes. Ignore min/maxOccurs, because those attributes cannot be used when element's parent is schema
    addMappingFromType(mappings, type, "/" + name, messageType);
  }

  private void addMappingFromType(Element mappings, SchemaType type, String path, MessageType messageType) {
    if (type.isSimpleType() || type.isURType()) {
      addMappingFromSimpleType(mappings, type, path, messageType);
      return;
    }
    if (SchemaType.ELEMENT_CONTENT == type.getContentType() && type.getContentModel() != null) {
      addMappingFromParticle(mappings, path, type.getContentModel(), messageType);
    }
    
  }

  private void addMappingFromParticle(Element mappings, String path, SchemaParticle schemaParticle, MessageType messageType) {

    // determine element count
    int minOccurs = schemaParticle.getIntMinOccurs();
    int maxOccurs = schemaParticle.getIntMaxOccurs();

    if (minOccurs == 0) {
      //TODO: can be null or empty array?
    }

    if (maxOccurs > 1) {
      if (SchemaParticle.ELEMENT == schemaParticle.getParticleType() && !schemaParticle.getType().isPrimitiveType()) {
        path += "/" + schemaParticle.getName().getLocalPart();
      }
      // repeating element group
      mappings = addRepeatingElementGroup(mappings, path);
    }

    switch (schemaParticle.getParticleType()) {
      case SchemaParticle.ELEMENT:
        processElementParticle(mappings, path, schemaParticle, messageType);
        break;
      case SchemaParticle.SEQUENCE:
        processSequenceParticle(mappings, path, schemaParticle, messageType);
        break;
    }
  }

  private void processSequenceParticle(Element mappings, String path, SchemaParticle schemaParticle, MessageType messageType) {
    SchemaParticle[] spArray = schemaParticle.getParticleChildren();
    for (SchemaParticle sp : spArray) {
      addMappingFromParticle(mappings, path, sp, messageType);
    }
  }

  private void processElementParticle(Element mappings, String path, SchemaParticle schemaParticle, MessageType messageType) {
    SchemaLocalElement element = (SchemaLocalElement) schemaParticle;
    String elementPath = path + "/" + element.getName().getLocalPart();

    if (element.getType().isPrimitiveType()) {
      addMapping(mappings, elementPath, getGlobalReference(element), messageType);
    } else {
      addMappingFromType(mappings, element.getType(), path, messageType);
    }
  }

  private String getGlobalReference(SchemaLocalElement element) {
    String result = null;

    if (element.getAnnotation() != null) {
      for (SchemaAnnotation.Attribute attribute : element.getAnnotation().getAttributes()) {
        if (attribute.getName().equals(MODEL_REFERENCE_ATTRIBUTE)) {
          result = attribute.getValue();
          break;
        }
      }
    }
    return result;
  }

  protected Element addRepeatingElementGroup(Element mappings, String path) {
    Element repeatingElement = new DOMElement("repeating_element_group");
    repeatingElement.setAttribute("path", path);
    mappings.appendChild(repeatingElement);
    return repeatingElement;
  }

  private void addMappingFromSimpleType(Element mappings, SchemaType type, String path, MessageType messageType) {
    // TODO implement method
    //To change body of created methods use File | Settings | File Templates.
  }


  private void addMappingFromPrimitivePartType(Element mappings, Part part, MessageType messageType) throws Exception {
    String path = "/" + part.getName();
    QName globalReference = (QName)part.getExtensionAttribute(MODEL_REFERENCE_ATTRIBUTE);

    addMapping(mappings, path, globalReference.getLocalPart(), messageType);
  }

  protected void addMapping(Element mappings, String path, String globalReference, MessageType messageType) {
    addMapping(mappings, path, globalReference, messageType, null);
  }

  protected void addMapping(Element mappings, String path, String globalReference, MessageType messageType, String defaultValue) {

    if (path == null && globalReference == null){
      return;
    }
    
    Element mapping = new DOMElement("mapping");
    mappings.appendChild(mapping);

    if (globalReference != null) {
      DOMElement globalRefElement = new DOMElement("global_ref");
      globalRefElement.setText(globalReference);
      mapping.appendChild(globalRefElement);
    }

    if (path != null) {
      DOMElement pathElement = new DOMElement("path");
      pathElement.setText(path);
      mapping.appendChild(pathElement);
    }

    if (defaultValue != null) {
      DOMElement defaultValueElement = new DOMElement("default");
      defaultValueElement.setText(defaultValue);
      mapping.appendChild(defaultValueElement);
    }
  }

  protected void addMappingWithAttributes(Element mappings, String path, String globalReference) {
    Element mapping = new DOMElement("mapping");
    mappings.appendChild(mapping);

    if (globalReference != null) {
      mapping.setAttribute("global_ref", globalReference);
    }
    mapping.setAttribute("path", path);
  }

  protected void addFormat(Element frame) {
    DOMElement format = new DOMElement("format");
    format.setText("json");
    frame.appendChild(format);
  }

  protected void addTopic(Element frame, boolean outgoingOnly, String topicName) {
    DOMElement topic = new DOMElement("topic");
    if (outgoingOnly){
      topic.setAttribute("outgoing_only", "true");
    }
    topic.setText(topicName);
    frame.appendChild(topic);
  }

  public WsdlContext getWsdlContext() {
    return wsdlContext;
  }

  public String getWsdlUri() {
    return wsdlUri;
  }

  public String getOperation() {
    return operation;
  }

  public String getJsonSchemaUrl() {
    return jsonSchemaUrl;
  }

  public void setWsdlContext(WsdlContext wsdlContext) {
    this.wsdlContext = wsdlContext;
  }

  public void setWsdlUri(String wsdlUri) {
    this.wsdlUri = wsdlUri;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public void setJsonSchemaUrl(String jsonSchemaUrl) {
    this.jsonSchemaUrl = jsonSchemaUrl;
  }
}
