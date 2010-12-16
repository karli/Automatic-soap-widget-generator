package ee.stacc.transformer.client.mapping;

import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import ee.stacc.transformer.client.data.DataFrame;
import ee.stacc.transformer.client.data.InstanceFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DataFrameLoader {
	public static final String TOPIC = "topic";
	public static final String FORMAT = "format";
	public static final String SCHEMA = "schema";
	public static final String MAPPINGS = "mappings";
	public static final String MAPPING = "mapping";
	public static final String GLOBAL_REF = "global_ref";
	public static final String PATH = "path";
	public static final String REPEATING_ELEMENT_GROUP = "repeating_element_group";
	public static final String OUTGOING_ONLY = "outgoing_only";
	public static final String TRUE = "true";
	public static final String CONSTANT = "constant";
	public static final String VALUE = "value";
	public static final String DEFAULT = "default";

  private Element frameElement;
  private DataFrame dataFrame;

  public DataFrameLoader(Element frameElement) {
    this.frameElement = frameElement;
  }

  private void initDataFrame() {
    //Get the data type of the messages specified in the mapping configuration.
    String dataType = frameElement.getElementsByTagName(FORMAT).item(0).getFirstChild().getNodeValue();

    //Instantiate the data frameElement according to the data type.
    dataFrame = InstanceFactory.getDataFrame(dataType);

    //Get the topic
    Element topicNode = (Element) frameElement.getElementsByTagName(TOPIC).item(0);
    String topic = topicNode.getFirstChild().getNodeValue();
    dataFrame.setTopic(topic);

    if(topicNode.hasAttribute(OUTGOING_ONLY) && TRUE.equalsIgnoreCase(topicNode.getAttribute(OUTGOING_ONLY))) {
      dataFrame.setOutputOnly(true);
    }

    //Schema element is optional. Load the schema
    if(frameElement.getElementsByTagName(SCHEMA).getLength() > 0) {
      String schemaUrl = frameElement.getElementsByTagName(SCHEMA).item(0).getFirstChild().getNodeValue();
      dataFrame.setSchemaURL(schemaUrl);
    }

    //Load the mapping elements
    NodeList nodes = frameElement.getElementsByTagName(MAPPINGS).item(0).getChildNodes();
    loadMappingsToDataFrame(nodes);

    //dataFrame.setMappings(mappings);
    dataFrame.updateMappingsSet();
  }

  public DataFrame getDataFrame() {
    if (dataFrame == null) {
      initDataFrame();
    }
    return dataFrame;
  }

  	/**
	 * To process a list of mapping nodes and load all the data in the nodes.
	 * @param nodes	list of mapping nodes.
     */
	private void loadMappingsToDataFrame(NodeList nodes) {
		Map<String, Mapping> mappings = dataFrame.getMappings();

		for(int i = 0;i < nodes.getLength();i++) {

			if(nodes.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;	//If it's just spam then don't process the node

			Element node = (Element)nodes.item(i);

			if(node.getNodeName().equalsIgnoreCase(MAPPING)) {
				//If the node is a mapping element
				MappingElement mappingElement = retrieveMappingElementFromNode(node);
        for (String globalRef : mappingElement.getGlobalReference()) {
          mappings.put(globalRef, mappingElement);
        }
			}
			else if(node.getNodeName().equalsIgnoreCase(REPEATING_ELEMENT_GROUP)) {
				//If the node is a repeating element group
				loadRepeatingElementGroupToDataFrame(node);
			}
			else if (node.getNodeName().equalsIgnoreCase(CONSTANT)) {
				//If the node is a constant.
				processConstant(node);
			}
		}
	}

	private void loadRepeatingElementGroupToDataFrame(Element repeatingGroupNode) {
		loadRepeatingElementGroupToDataFrame(repeatingGroupNode, null);
	}

	/**
	 * To load a constant value.
	 * @param node	node of the constant value.
   */
	private void processConstant(Element node) {
		String path = node.getAttribute(PATH);
		String value = node.getAttribute(VALUE);

		dataFrame.getConstantValues().put(path, value);
	}

	/**
	 * To load mappings from a repeating element group.
	 *
	 * @param repeatingElementGroupNode	repeating element group node.
   * @param parentMappingsGroup	mappings of the parent repeating mapping group.
   */
	private void loadRepeatingElementGroupToDataFrame(Element repeatingElementGroupNode, RepeatingElementGroup parentMappingsGroup) {

    String path = null;
    if (repeatingElementGroupNode.hasAttribute(PATH)) {
      path = repeatingElementGroupNode.getAttribute(PATH);
    }

		RepeatingElementGroup mappingsGroup = new RepeatingElementGroup(path, dataFrame, parentMappingsGroup);

		//Go through every child node in the repeating element group.
		NodeList nodes = repeatingElementGroupNode.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++) {

			if(nodes.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;	//If it's just spam then don't process the node

			Element node = (Element)nodes.item(i);

			if(node.getNodeName().equalsIgnoreCase(MAPPING)) {
				//If the node is a regular mapping node
				MappingElement mappingElement = retrieveMappingElementFromNode(node);
				String globalRef = mappingElement.getFirstGlobalReference();

				//Put the mapping element to the element group's mappings map
				mappingsGroup.propagateMapping(mappingElement, globalRef);
			}
			else if(node.getNodeName().equalsIgnoreCase(REPEATING_ELEMENT_GROUP)) {
				//If the node is also (recursively) a repeating element group.
				loadRepeatingElementGroupToDataFrame(node, mappingsGroup);
			}
			else if (node.getNodeName().equalsIgnoreCase(CONSTANT)) {
				//if the node is a constant.
				processConstant(node);
			}
		}
	}

	/**
	 * To load a regular mapping element from a node.
	 * @param node	node representing the mapping element.
	 * @return	the mapping element in the node.
	 */
	private MappingElement retrieveMappingElementFromNode(Element node) {

		//Get the global reference
		String globalRef;
		if(node.getElementsByTagName(GLOBAL_REF).getLength() > 0)
			globalRef =  node.getElementsByTagName(GLOBAL_REF).item(0).getFirstChild().getNodeValue();
		else
			globalRef =  node.getAttribute(GLOBAL_REF);

		//Get the path. Path is optional if the data doesn't have any structure.
		String path =  "";
		if(node.getElementsByTagName(PATH).getLength() > 0) {
			path =  node.getElementsByTagName(PATH).item(0).getFirstChild().getNodeValue();
		}
		else if(node.getAttribute(PATH) != null)
			path = node.getAttribute(PATH);

		MappingElement mapping = new MappingElement(path, getGlobalReferences(globalRef));

		//Load a default value if set.
		if(node.getElementsByTagName(DEFAULT).getLength() > 0) {
			String defaultValue = node.getElementsByTagName(DEFAULT).item(0).getFirstChild().getNodeValue();
			processDefaultValue(defaultValue, mapping);
		}
		else if(node.getAttribute(DEFAULT) != null) {
			String defaultValue = node.getAttribute(DEFAULT);
			processDefaultValue(defaultValue, mapping);
		}

		return mapping;
	}

  /**
   *
   * @param globalRef string of space-separated global references
   * @return list of global references
   */
  private List<String> getGlobalReferences(String globalRef) {
    if (globalRef == null) {
      return new ArrayList<String>();
    }
    // split string by space
    return Arrays.asList(globalRef.split("\\s+"));
  }


  private void processDefaultValue(String defaultValue, MappingElement mapping) {
		mapping.setHasDefaultValue(true);
		dataFrame.getConstantValues().put(mapping.getPath(), defaultValue);
	}
}