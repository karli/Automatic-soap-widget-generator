package soapproxy.util;

import nu.xom.*;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.TextNode;
import soapproxy.components.proxy.JsonRpc2SoapConverterImpl;

import java.io.StringReader;

public class Xml2JsonConverter {

  private boolean ignoreNamespaces = true;
  private boolean ignoreAttributes = true;

  public boolean isIgnoreNamespaces() {
    return ignoreNamespaces;
  }

  public void setIgnoreNamespaces(boolean ignoreNamespaces) {
    this.ignoreNamespaces = ignoreNamespaces;
  }

  public boolean isIgnoreAttributes() {
    return ignoreAttributes;
  }

  public void setIgnoreAttributes(boolean ignoreAttributes) {
    this.ignoreAttributes = ignoreAttributes;
  }

  public JsonNode convert(String xml) {
    JsonNode result;
    try {
      Document doc = new Builder().build(new StringReader(xml));
      Element root = doc.getRootElement();
      if (isNullNode(root)) {
        result = JsonNodeFactory.instance.nullNode();
      } else {
        // we assume that it must be a object node
        result = new ObjectNode(JsonNodeFactory.instance);
        setOrAccumulate((ObjectNode)result, root); 
      }
    } catch (Exception e) {
      result = JsonNodeFactory.instance.nullNode();
    }

    return result;
  }

  private JsonNode processElement(Element element) {
    if (isNullNode(element)) {
      return JsonNodeFactory.instance.nullNode();
    }
    JsonNode node = JsonNodeFactory.instance.nullNode();
    // process children
    if (element.getChildElements().size() > 0 || element.getAttributeCount() > 0) {
      ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
      node = objectNode;
      addAttributesAsChildren(objectNode, element);
      if (element.getChildElements().size() == 0) {
        // add value to a special value node
        objectNode.put(JsonRpc2SoapConverterImpl.VALUE_ELEMENT_NAME, element.getValue());
      } else {
        for (int i = 0; i < element.getChildCount(); i++) {
          Node child = element.getChild(i);
          if (child instanceof Element) {
            setOrAccumulate(objectNode, (Element)child);
          }
        }
      }
    } else {
      int childCount = element.getChildCount();
      for (int i = 0; i < childCount; i++) {
        Node child = element.getChild(i);
        if (child instanceof Text) {
          Text text = (Text) child;
          if (StringUtils.isNotBlank(StringUtils.strip(text.getValue()))) {
            node = new TextNode(StringUtils.strip(text.getValue()));
            // no mixed content allowed!
            break;
          }
        }
      }
    }

    return node;
  }

  private void setOrAccumulate(ObjectNode node, Element element) {
    String name = removeNamespacePrefix(element.getQualifiedName());
    // such field does not exist
    JsonNode existingNode = node.get(name);
    if (existingNode == null) {
      node.put(name, processElement(element));
    } else {
      ArrayNode arrayNode;
      if (!existingNode.isArray()) {
        arrayNode = new ArrayNode(JsonNodeFactory.instance);
        arrayNode.add(existingNode);
        node.put(name, arrayNode);
      } else {
        arrayNode = (ArrayNode)existingNode;
      }
      arrayNode.add(processElement(element));
    }
  }

  private void addAttributesAsChildren(ObjectNode newNode, Element element) {
    // process attributes
    for (int i = 0; i < element.getAttributeCount(); i++) {
      Attribute attr = element.getAttribute(i);
      newNode.put(JsonRpc2SoapConverterImpl.ATTRIBUTE_ELEMENT_PREFIX + attr.getLocalName(), attr.getValue());
    }
  }

  private String removeNamespacePrefix(String name) {
    if (isIgnoreNamespaces()) {
      int colon = name.indexOf(':');
      return colon != -1 ? name.substring(colon + 1) : name;
    }
    return name;
  }

  private boolean isNullNode(Element element) {
    if (element.getChildCount() == 0) {
      return true;
    }
    return false;
  }
}
