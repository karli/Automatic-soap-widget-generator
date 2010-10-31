package ee.stacc.transformer.client.data.xml;

import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.DocumentFragment;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.Text;
import com.google.gwt.xml.client.XMLParser;

import ee.stacc.transformer.client.data.GeneratedDataValue;
import ee.stacc.transformer.client.data.AtomicDataValue;

/**
 * For generating XML documents based on a schema and data values.
 * TODO: support for different schema namespaces.
 * TODO: support for separate complex types (if definitions are not directly nested inside but are referenced).
 * TODO: support for all the schema elements.
 * 
 * @author Rainer Villido
 *
 */
public class XMLGenerator {
	
	//XSD tag names, element types etc
	public static final String XSD_ATTR_NAME = "name";
	public static final String XSD_ELEMENT = "element";
	public static final String XSD_ATTR_TYPE = "type";
	public static final String XSD_COMPLEX_TYPE = "complexType";
	public static final String XSD_SEQUENCE = "sequence";
	public static final String XSD_ATTRIBUTE = "attribute";
	
	public static final String XSD_BASE_SCHEMA_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema";
	
	private Document schema;
	private Map<String, GeneratedDataValue> dataValues;
	private String prefix;	//Namespace prefix.
	
	public XMLGenerator(Document schema) {
		this.schema = schema;
	}

	/**
	 * To generate an XML document based on the data values.
	 * @param dataValues	data to insert to the xml document.
	 * @return	xml document with populated data.
	 */
	public Node generateXml(Map<String, GeneratedDataValue> dataValues) {
		this.dataValues = dataValues;
		
		DocumentFragment xmlDoc = XMLParser.createDocument().createDocumentFragment();
		String path = "";
		
		Node root = schema.getFirstChild();
		NodeList nodes = root.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++){
			Node node = nodes.item(i);
			
			//Skip spam nodes
			if(node.getNodeType() == Node.TEXT_NODE)
				continue;
			
			prefix = node.getPrefix();
			if(prefix!=null)
				prefix = prefix+":";
			
			String nodeName = node.getNodeName();
			
			if(nodeName.equalsIgnoreCase(prefix + XSD_ELEMENT)) {
				Node element = processElement(node, path);
				xmlDoc.appendChild(element);
			}
		}
		
		return xmlDoc;
	}
	
	/**
	 * Method for processing an element tag.
	 * 
	 * @param node	the element node.
	 * @param path	current path.
	 * @return	xml contents of the element.
	 */
	private Node processElement(Node node, String path) {
		//<xsd:element>
		
		//Get the element name
		String elementName = node.getAttributes().getNamedItem(XSD_ATTR_NAME).getNodeValue();
		Document xmlDoc = XMLParser.createDocument();
		Element element = xmlDoc.createElement(elementName);
		
		path = path + "/" + elementName;
		
		if(node.getChildNodes().getLength() > 0) {
			NodeList elementNodes = node.getChildNodes();
			for(int j = 0; j < elementNodes.getLength(); j++) {
				Node elementNode = elementNodes.item(j);
				
				//Skip spam nodes
				if(elementNode.getNodeType() == Node.TEXT_NODE)
					continue;
				
				String elementNodename = elementNode.getNodeName();
				if(elementNodename.equalsIgnoreCase(prefix + XSD_COMPLEX_TYPE)) {
					processComplexTypeXml(element, elementNode, path);
				}
			}
			return element;
		}
		else if (node.getAttributes().getLength() > 0) {
			Node typeNode = node.getAttributes().getNamedItem(XSD_ATTR_TYPE);
			String typeName = typeNode.getNodeValue();
			
			//TODO:check if typeName is a xsd base type or is it a separate complex type
			
			//if it's a simple element
			DocumentFragment docFrag = xmlDoc.createDocumentFragment();
			
			String elementValue = getDataFromPath(path);
			Text textNode = xmlDoc.createTextNode(elementValue);
			element.appendChild(textNode);
			
			docFrag.appendChild(element);
			
			return docFrag;
		
			//TODO: implement Separate Complex Types

		}
		else {
			GWT.log("Something went wrong with parsing. "+node.toString(), null);
			return XMLParser.createDocument().createDocumentFragment();
		}
	}

	/**
	 * Method for processing complexType tag.
	 * @param superElement	the element that contains the complexType tag.
	 * @param complexTypeNode	the complexType tag's node.
	 * @param path	current path.
	 */
	private void processComplexTypeXml(Element superElement, Node complexTypeNode, String path) {
		//<xsd:complexType>
		
		DocumentFragment xmlDoc = XMLParser.createDocument().createDocumentFragment();
		//TODO: add element name to the xmlDoc
		NodeList innerNodes = complexTypeNode.getChildNodes();
		for(int i = 0;i < innerNodes.getLength(); i++) {
			Node innerNode = innerNodes.item(i);
			
			//Skip spam nodes
			if(innerNode.getNodeType() == Node.TEXT_NODE)
				continue;
			
			String innerNodeName = innerNode.getNodeName();
			if(innerNodeName.equalsIgnoreCase(prefix + XSD_SEQUENCE)) {
				Node sequence = processSequence(innerNode, path);
				xmlDoc.appendChild(sequence);
			}
			else if(innerNodeName.equalsIgnoreCase(prefix + XSD_ATTRIBUTE)) {
				//xsd:attribute
				insertAttribute(superElement, innerNode, path);
			}
			//TODO: deal with any other complexType elements
		}
		superElement.appendChild(xmlDoc);
	}
	
	/**
	 * Method for processing <attribute> tag.
	 * 
	 * @param superElement	the element that contains the attribute tag.
	 * @param attributeSchema	the attribute tag's node.
	 * @param path	current path.
	 */
	private void insertAttribute(Element superElement, Node attributeSchema, String path) {
		//<xsd:attribute>
		//TODO: deal with default values
		
		String attributeName = attributeSchema.getAttributes().getNamedItem(XSD_ATTR_NAME).getNodeValue();
		
		path = path + "/" + attributeName;
		String attributeValue = getDataFromPath(path);
		
		superElement.setAttribute(attributeName, attributeValue);
	}

	/**
	 * Method for processing sequence tag.
	 * @param sequenceNode
	 * @param path
	 * @return	xml document of elements in the sequence node.
	 */
	private Node processSequence(Node sequenceNode, String path) {
		//<xsd:sequence>
		
		DocumentFragment xmlDoc = XMLParser.createDocument().createDocumentFragment();
		
		NodeList innerNodes = sequenceNode.getChildNodes();
		for(int i = 0; i < innerNodes.getLength(); i++) {
			Node innerNode = innerNodes.item(i);
			
			//Skip spam nodes
			if(innerNode.getNodeType() == Node.TEXT_NODE)
				continue;
			
			Node subNode = processElement(innerNode, path);
			xmlDoc.appendChild(subNode);
		}
		
		return xmlDoc;
	}
	
	/**
	 * Method for retrieving data according to the path.
	 * @param path
	 * @return	String representation of the data.
	 */
	private String getDataFromPath(String path) {
		GeneratedDataValue dataValue = dataValues.get(path);
		if(dataValue != null) {
			String stringValue = ((AtomicDataValue)dataValue).getStringValue();	//TODO: check if it indeed is atomicDataValue
			return stringValue;
		}
		else {
			GWT.log("Mismatch between schema and mappings. Didn't find any values on this path: "+path, null);
			return "";
		}
	}
}
