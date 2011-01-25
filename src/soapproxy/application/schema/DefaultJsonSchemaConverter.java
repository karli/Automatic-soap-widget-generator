package soapproxy.application.schema;

import com.eviware.soapui.impl.wsdl.support.soap.SoapUtils;
import com.eviware.soapui.impl.wsdl.support.soap.SoapVersion;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlContext;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlUtils;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import soapproxy.util.SampleXmlUtil;
import soapproxy.util.SoapMessageBuilder;

import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Part;
import java.util.Stack;

public class DefaultJsonSchemaConverter implements JsonSchemaConverter {
  private String wsdlUri;
  private String operationName;
  private MessageType messageType;
  private WsdlContext wsdlContext;

  public static enum MessageType {
    INPUT_MESSAGE, OUTPUT_MESSAGE, FAULT_MESSAGE
  }

  public DefaultJsonSchemaConverter(String wsdlUri, String operationName, MessageType messageType) {
    this.wsdlUri = wsdlUri;
    this.operationName = operationName;
    this.messageType = messageType;
    wsdlContext = new WsdlContext(wsdlUri);
  }

  @Override
  public String getJsonSchema() throws Exception {
    ObjectNode root = new ObjectNode(JsonNodeFactory.instance);
    root.put("type", "object");
    ObjectNode properties = root.putObject("properties");

    addMessageToSchema(properties);
//    for (Part part : getParts()) {
//      addPartToProperties(properties, part);
//    }
    return root.toString();
  }

  private void addMessageToSchema(ObjectNode properties) throws Exception {
    SoapMessageBuilder smb = new SoapMessageBuilder(wsdlContext);
    String soapMessage = null;
    BindingOperation bindingOperation = getBindingOperation();
    if (MessageType.INPUT_MESSAGE.equals(messageType)) {
      soapMessage = smb.buildSoapMessageFromInput(bindingOperation, true);
    }
    else if (MessageType.OUTPUT_MESSAGE.equals(messageType)) {
      soapMessage = smb.buildSoapMessageFromOutput(bindingOperation, true);
    }

    XmlObject soapMessageObject = XmlObject.Factory.parse(soapMessage);
    XmlObject bodyObject = SoapUtils.getBodyElement(soapMessageObject, SoapVersion.Soap11);
    XmlCursor cursor = bodyObject.newCursor();
    cursor.toNextToken();
    // if rpc then skip first body element
//    if (WsdlUtils.isRpc(wsdlContext.getDefinition(), bindingOperation)) {
//      while(!cursor.toNextToken().isText());
//    }
//    cursor.toNextToken();
    addToJsonSchema2(cursor, properties);
    properties.toString();
  }

  /**
   * cursor position before: <parent>^...<elem>...</elem>...</parent>
   */
  private void addToJsonSchema(XmlCursor cursor, ObjectNode root) {
    ObjectNode newNode = null;
    while (!cursor.toNextToken().isNone()) {
      if (cursor.isComment()) {
        if ("Zero or more repetitions:".equals(cursor.getTextValue())) {
          root.put("type", "array");
          root = root.putObject("items");

        }
      }
      if (cursor.isStart()) {
//        cursor.push();
        // add to schema
        root.put("type", "object");
        ObjectNode properties = root.putObject("properties");
        newNode = properties.putObject(cursor.getName().getLocalPart());
        //move to the TEXT token

//        cursor.pop();
      }
      if (cursor.isText() && newNode != null){
        addToJsonSchema(cursor, newNode);
      }
    }
  }

  private void addToJsonSchema2(XmlCursor cursor, ObjectNode properties) {
    Stack<ObjectNode> propertiesStack = new Stack<ObjectNode>();
    propertiesStack.push(properties);
    boolean required = true;
    while (!cursor.toNextToken().isNone()) {
      if (cursor.isComment()) {
        if ("Zero or more repetitions:".equals(cursor.getTextValue())) {
          properties.put("type", "array");
          properties = properties.putObject("items");
        }
        if ("Optional:".equals(cursor.getTextValue())) {
          required = false;
        }
      }
      if (cursor.isStart()) {
        // add to schema
        ObjectNode currentProps = propertiesStack.lastElement();
        ObjectNode newNode = currentProps.putObject(cursor.getName().getLocalPart());

        // set required if needed
        if (required) {
          newNode.put("required", true);
        }
        else {
          // reset
          required = true;
        }
        cursor.push();
        if (cursor.toFirstChild()) {
          newNode.put("type", "object");
          propertiesStack.push(newNode.putObject("properties"));
          cursor.pop();
        } else {
          newNode.put("type","string");
          // dummy
          propertiesStack.push(null);
        }
        //move to the TEXT token
      }
      if (cursor.isEnd() && propertiesStack.size() > 0){
        propertiesStack.pop();
      }
    }
  }

  private void addPartToProperties(ObjectNode properties, Part part) throws Exception {
    SoapMessageBuilder smb = new SoapMessageBuilder(wsdlContext);
    XmlObject object = XmlObject.Factory.newInstance();
    XmlCursor cursor = object.newCursor();
    cursor.toFirstChild();

    smb.createElementForPart(part, cursor, getSampleXmlUtil());

    object.xmlText();

//    // get part type
//    SchemaType partType = WsdlUtils.getSchemaTypeForPart(wsdlContext, part);
//    // if part is of primitive type, then no extra type resolution is needed
//    if (partType.isPrimitiveType()) {
//      addPrimitivePart(part, partType, properties);
//    }
  }

  private SampleXmlUtil getSampleXmlUtil() throws Exception {
    boolean inputSoapEncoded = WsdlUtils.isInputSoapEncoded(getBindingOperation());
    SampleXmlUtil xmlGenerator = new SampleXmlUtil(inputSoapEncoded);
    return xmlGenerator;
  }

  private void addPrimitivePart(Part part, SchemaType schemaType, ObjectNode propertiesNode) {
    // get name attribute
    ObjectNode fieldNode = propertiesNode.putObject(part.getName());
//    fieldNode.put("type", resolvePrimitiveType(schemaType.getBuiltinTypeCode()));
  }

  private String resolvePrimitiveType(int typeCode) {
    String result = null;
    if (SchemaType.BTC_STRING == typeCode) {
      result = "string";
    }
    return result;
  }

  private Part[] getParts() throws Exception {
    BindingOperation bindingOperation = getBindingOperation();
    Part[] parts = null;
    if (MessageType.INPUT_MESSAGE.equals(messageType)) {
      parts = WsdlUtils.getInputParts(bindingOperation);
    }
    else if (MessageType.OUTPUT_MESSAGE.equals(messageType)) {
      parts = WsdlUtils.getOutputParts(bindingOperation);
    }
    return parts;
  }

  private BindingOperation getBindingOperation() throws Exception {
    Definition definition = wsdlContext.getDefinition();
    BindingOperation bindingOperation = WsdlUtils.findBindingOperation(definition, operationName);
    return bindingOperation;
  }
}
