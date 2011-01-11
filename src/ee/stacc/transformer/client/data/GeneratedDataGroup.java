package ee.stacc.transformer.client.data;

import java.util.HashMap;
import java.util.Map;

import ee.stacc.transformer.client.mapping.Mapping;
import ee.stacc.transformer.client.mapping.RepeatingElementGroup;

/**
 * For holding a group of data elements for assembling a new message.
 * 
 * @author Rainer Villido
 *
 */
public class GeneratedDataGroup {
	
	/**
	 * Mappings that represent the group of data elements to be assembled.
	 */
	private RepeatingElementGroup groupMappings;
	
	/**
	 * Data elements grouped together to be assembled to a new message. The map key is global reference of a data element.
	 */
	private Map<String, GeneratedDataValue> assembledData = new HashMap<String, GeneratedDataValue>();	//key: path
	
	/**
	 * Constructor.
	 * @param groupMappings	mappings that represent the group of data elements to be assembled to a new message.
	 */
	public GeneratedDataGroup(RepeatingElementGroup groupMappings) {
		this.groupMappings = groupMappings;
	}
	
	/**
	 * To check if this data group has received all the data for each mapping.
	 * @return	true if the group has received enough data.
	 */
	public boolean isFilledWithData() {
		
		//Return true if mappings and data collections' sizes match.
		//TODO: this is probably not true in case of recursive data
		return (groupMappings.getMappings().size() == assembledData.size());
	}
	
	/**
	 * To add a new data value to the group.
	 * @param dataValue	the data value to be added.
	 * @param globalRef	the global reference of the data value.
	 */
	public void addDataValue(AtomicDataValue dataValue, String globalRef) {
		Mapping mapping = groupMappings.getMapping(globalRef);
		
		addDataValue(dataValue, globalRef, mapping);
	}

	/**
	 * To add a collected data value with the global reference and the mapping.
	 * @param dataValue
	 * @param globalRef
	 * @param mapping
	 */
	private void addDataValue(AtomicDataValue dataValue, String globalRef, Mapping mapping) {
		if(mapping.isMappingElement() != null) {
			String path = mapping.getPath();
			assembledData.put(path, dataValue);
		}
		else if (mapping.isRepeatingElementGroup() != null) {//TODO: also check if data value is CollectedDataGroupsCollection
			RepeatingElementGroup mappingGroup = mapping.isRepeatingElementGroup();
			Mapping subMapping = mappingGroup.getMapping(globalRef);
			addDataValue(dataValue, globalRef, subMapping);
		}
	}
	
	/**
	 * To check whether the data group contains data with the global reference.
	 * @param globalRef	global reference of the data to be checked.
	 * @return	true if the data group contains data with the global reference.
	 */
	public boolean hasData(String globalRef) {
		Mapping mapping = groupMappings.getMapping(globalRef);
		String path = mapping.getPath();
		
		//Returns true if the data group contains data on that path.
		return assembledData.containsKey(path);
	}

	/**
	 * @return the assembledData
	 */
	public Map<String, GeneratedDataValue> getAssembledData() {
		return assembledData;
	}

}
