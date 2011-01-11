package ee.stacc.transformer.client.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ee.stacc.transformer.client.mapping.Mapping;
import ee.stacc.transformer.client.mapping.MappingElement;
import ee.stacc.transformer.client.mapping.RepeatingElementGroup;

/**
 * Data package keeps data about messages that are being generated. It stores aggregated data values necessary
 * to for creating new messages.
 * 
 * If support for another data format (XML for example) is to be implemented, 
 * then a new class must be implemented that extends the DataPackage class.
 * 
 * @author Rainer Villido
 *
 */
public abstract class DataPackage {
	private DataFrame dataFrame;	//The data frame representing the message.
	private Map<String, GeneratedDataValue> dataValues = new HashMap<String, GeneratedDataValue>();	//Key: Path. Contains data values in this data package.

	public DataPackage(DataFrame dataFrame) {
		this.dataFrame = dataFrame;
	}
	
	/**
	 * To get the message object that can be published to the hub.
	 * @return	message object.
	 */
	public abstract Object getObjectToPublish();
	
	/**
	 * To add the data value as a whole group because of repeating mappings group.
	 * @param dataValue
	 * @param mapping
	 * @param globalRef
	 */
	private void addDataValueToGroup(AtomicDataValue dataValue, RepeatingElementGroup mapping, String globalRef) {
		//If mappings contain repeatable element groups then add this value as a group.
		
		//Get the path.
		String groupPath = mapping.getPath();
		
		//if the map of data values contains a collection with this path, then use this collection
		//Otherwise create a new collection.
		GeneratedDataGroupsCollection dataCollection;
		if(dataValues.containsKey(groupPath) == false) {
			dataCollection = new GeneratedDataGroupsCollection();
			dataValues.put(groupPath, dataCollection);
		}
		else
			dataCollection = dataValues.get(groupPath).isGeneratedDataGroupsCollection();
		
		//Get unfinished data group according to the global reference.
		GeneratedDataGroup group = dataCollection.getUnfinishedDataGroup(globalRef);
		
		//If there are no data groups with that global reference, then create a new one.
		if(group == null) {
			group = new GeneratedDataGroup(mapping.isRepeatingElementGroup());
			dataCollection.addUnfinishedDataGroup(group);
		}
		
		//Add the data value to the group.
		group.addDataValue(dataValue, globalRef);
		
		//IF the group is now filled with data (contains all the necessary values), then move it to the list of finished list of groups.
		if(group.isFilledWithData()) {
			dataCollection.moveGroupToFinishedList(group);
		}
		
	}
	
	/**
	 * To add a new data value to the data package.
	 * @param dataValue	new data value to add.
	 */
	public void addDataValue(CollectedDataValue dataValue) {
		
		if(dataValue.isAtomicDataValue() != null) {
			//If the data value is an atomic data value, then add it straight as an atomic data value
			AtomicDataValue atomicDataValue = dataValue.isAtomicDataValue();
			addAtomicDataToPackage(atomicDataValue);
		}
		else if (dataValue.isCollectedDataGroup() != null) {
			//If it's a collected data group, then process the group and add necessary data values.
			CollectedDataGroup dataGroup = dataValue.isCollectedDataGroup();
			
			addDataGroupToPackage(dataGroup);
		}
	}
	
	/**
	 * To add a new atomic data value to the data package.
	 * @param atomicDataValue
	 */
	private void addAtomicDataToPackage(AtomicDataValue atomicDataValue) {
		String globalRef = atomicDataValue.getGlobalReference();
		Mapping outgoingMapping = getDataFrame().getMapping(globalRef);
		
		if(outgoingMapping == null) {
			//If a mapping with the global reference was not found, 
			//then it means that this data value cannot be added to this data package.
			//Therefore break this operation.
			return;
		}
		
		if(outgoingMapping.isMappingElement() != null) {
			//If mappings doesn't contain repeatable data groups then just simply add the value to the package.
			String path = outgoingMapping.getPath();
			getDataValues().put(path, atomicDataValue);
		}
		else if(outgoingMapping.isRepeatingElementGroup() != null) {
			//If mappings contain repeatable element groups then add this value as a group.
			addDataValueToGroup(atomicDataValue, outgoingMapping.isRepeatingElementGroup(), globalRef);
		}
	}
	
	/**
	 * To add a new data values group to the data package.
	 * @param collectedDataGroup
	 */
	private void addDataGroupToPackage(CollectedDataGroup collectedDataGroup) {
		//For each global reference in the group, find what data values are needed by this data package (and then add them).
    // TODO: add support for recursive data groups
		for(CollectedDataValue collectedDataValue: collectedDataGroup.getCollectedAtomicData()) {
			//Add the data value to the package.
			addDataValue(collectedDataValue);
		}
	}
	
	/**
	 * Check if the data package is truly finished to be published.
	 * Note that if it contains arrays, then it's not finished, because there is always room for another element in an array.
	 * 
	 * @return if the data package is truly ready to be published. 
	 */
	public boolean isFinished() {
		//TODO: this is not the best way to check if it's finished. Make it more reliable.
		
		//Check if it has repeatable elements. If it does then there is always room for more elements.
		for(GeneratedDataValue dataValue: dataValues.values()) {
			if(dataValue.isGeneratedDataGroupsCollection() != null)
				return false;
		}
		
		//Package is finished if it has the same amount of data values than it's frame's mappings.
		return (dataValues.size() == dataFrame.getMappingsSet().size());
	}
	
	/**
	 * For checking if the package contains a value with that global reference.
	 * @param globalRef
	 * @return
	 */
	public boolean containsEnoughData(String globalRef) {
		Mapping mapping = dataFrame.getMapping(globalRef);
		
		if(mapping == null)
			return false;
		
		String path = mapping.getPath();
		if(dataValues.containsKey(path) == false)
			return false;
		
		else if(mapping.isMappingElement() != null) {
			return true;
		}
		else if(mapping.isRepeatingElementGroup() != null) {
			return false;
		}
		else
			return false;
		
	}
	
	/**
	 * To check if the data package contains enough data that no values from the collected data value are needed.
	 * @param dataValue	collected data value.
	 * @return	if you don't need to add any elements from this data value anymore. 
	 */
	public boolean containsEnoughData(CollectedDataValue dataValue) {
		
		if(dataValue.isAtomicDataValue() != null) {
			String globalReference = dataValue.isAtomicDataValue().getGlobalReference();
			return containsEnoughData(globalReference);
		}
		else if(dataValue.isCollectedDataGroup() != null) {
			
			for(CollectedDataValue collectedDataValue: dataValue.isCollectedDataGroup().getCollectedAtomicData()) {
				//TODO: implement a more reliable way of checking that in case of CollectedDataCroup.
				if(collectedDataValue.isAtomicDataValue()!=null) {
					String globalRef = collectedDataValue.isAtomicDataValue().getGlobalReference();
					if(containsEnoughData(globalRef))
						return true;
				}
			}
			
			return false;
		}
		else
			return false;
	}
	
	/**
	 * Checks if the data package is ready to be published to the hub.
	 * @return	true if the data package is ready to be published.
	 */
	public boolean isReadyToBePublished() {

		//Check is all the necessary data has been aggregated.
		return areDataValuesPublishable(dataValues, dataFrame.getMappingsSet());
	}
	
	/**
	 * Check is all the necessary data has been aggregated.
	 * @param dataValues	map of data values. Key is path.
	 * @param mappings	collection of mappings corresponding to these data values.
	 * @return
	 */
	private boolean areDataValuesPublishable(Map<String, GeneratedDataValue> dataValues, Collection<Mapping> mappings) {
		//Check is all the necessary data has been aggregated.
		
		for(Mapping mapping: mappings) {
			
			if(mapping.isMappingElement() != null) {
				//For each atomic mapping element, check if there is corresponding data value or default data value.
				MappingElement mappingElement = mapping.isMappingElement();
				if(mappingElement.hasDefaultValue() == false) {
					//if there is a default value, then a data value is not needed.
					//Otherwise check if there exists a corresponding data value 
					String path = mappingElement.getPath();
					
					//If there isn't any data values associated with that mapping
          // then the data package is ready only if mapping is optional
					if(dataValues.containsKey(path) == false)
						return mapping.isOptional();
				}
			}
			else if(mapping.isRepeatingElementGroup() != null) {
				//For each repeatable mappings group, check if all of its data collections are ready.
				//If all the data collections are ready, then the data package is ready to be published.
				
				String repeatableElementPath = mapping.isRepeatingElementGroup().getPath();
				if(dataValues.containsKey(repeatableElementPath) == false)
					return mapping.isOptional();
				
				GeneratedDataGroupsCollection dataCollection = dataValues.get(repeatableElementPath).isGeneratedDataGroupsCollection();
				
				if(dataCollection.hasOnlyFinishedDataGroups() == false) {
					//If there are some data groups that are not finished
					//then go through all the unfinished data groups
					//and check if all of them can have default values instead.
					List<GeneratedDataGroup> unfinishedDataGroups = dataCollection.getUnfinishedDataGroups();
					for(GeneratedDataGroup dataGroup: unfinishedDataGroups) {
						Map<String, GeneratedDataValue> generatedDataValues = dataGroup.getAssembledData();
						Collection<Mapping> repeatableElementGroupMappings = mapping.isRepeatingElementGroup().getMappings().values();
						if(areDataValuesPublishable(generatedDataValues, repeatableElementGroupMappings) == false)
							return false;
					}
				}
			}
		}
		
		return true;
	}
	
	public DataFrame getDataFrame() {
		return dataFrame;
	}

	public Map<String, GeneratedDataValue> getDataValues() {
		return dataValues;
	}

}
