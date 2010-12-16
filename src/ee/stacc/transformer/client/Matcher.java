package ee.stacc.transformer.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;

import ee.stacc.transformer.client.data.CollectedDataGroup;
import ee.stacc.transformer.client.data.CollectedDataGroupsCollection;
import ee.stacc.transformer.client.data.CollectedDataValue;
import ee.stacc.transformer.client.data.DataFrame;
import ee.stacc.transformer.client.data.DataPackage;
import ee.stacc.transformer.client.data.InstanceFactory;
import ee.stacc.transformer.client.mapping.Mapping;
import ee.stacc.transformer.client.mapping.MappingElement;
import ee.stacc.transformer.client.mapping.RepeatingElementGroup;

/**
 * The main class for logic of aggregating data from messages.
 * @author Rainer Villido
 *
 */
public class Matcher {
	
	private Map<String, DataFrame> dataFrames = new HashMap<String, DataFrame>();	//Key: topic; Value: data frame with that topic.
	private Map<String, Set<DataFrame>> referenceMappingsToFrames = new HashMap<String, Set<DataFrame>>();	//Key: global reference to an ontology; Value: set of data frames that contain that global reference.
	private Map<String, List<DataPackage>> unfinishedDataPackages = new HashMap<String, List<DataPackage>>();	//Key: topic; value: unfinished data package
	private List<DataPackage> finishedDataPackages = new ArrayList<DataPackage>();	//List of finished data packages
	private String incomingTopic;	//Name of the topic of the received message.
	
	/**
	 * This operation loads all the global references to the map where each global reference has the set of data frames that contain that global reference.
	 * @param frames the frames from where to load mappings from.
	 */
	public void loadMappingsToMap(Map<String, DataFrame> frames) {
		//TODO: optimize so that this operation is done when loading mappings

    for(DataFrame frame: frames.values()) {
			Collection<Mapping> frameMappings = frame.getMappingsSet();
			processMappingsForFrame(frameMappings, frame);
		}
	}
	
	/**
	 * Go through all the mappings in the data frame and update the referenceMappingsToFrames variable with global
	 * references that are related to the data frame. 
	 * @param mappings	(sub) mappings that belong to the data frame.
	 * @param frame	the data frame that is being added to the map of data frames based on its global references.
	 */
	private void processMappingsForFrame(Collection<Mapping> mappings, DataFrame frame) {
		for(Mapping mapping: mappings) {
			if(mapping.isMappingElement() != null) {
				//If the mapping element is a regular atomic data element then 
				//add it to the map.
				MappingElement mappingElement = mapping.isMappingElement();
				String globalReference = mappingElement.getFirstGlobalReference();
				if(referenceMappingsToFrames.containsKey(globalReference)) {
					referenceMappingsToFrames.get(globalReference).add(frame);
				}
				else {
					//If the map does not contain frames with the same global reference then 
					//create a new list for the data frames with the same global reference.
					Set<DataFrame> framesList = new HashSet<DataFrame>();
					framesList.add(frame);
					referenceMappingsToFrames.put(globalReference, framesList);
				}
				
			}
			else if(mapping.isRepeatingMappingsGroup() != null) {
				//If the mapping is a repeatable mapping group (i.e. represents an array) then
				//get mappings of the repeatable mapping group and 
				//recursively process those mappings.
				RepeatingElementGroup mappingsGroup = mapping.isRepeatingMappingsGroup();
				Collection<Mapping> groupMappings = mappingsGroup.getMappingsSet();
				processMappingsForFrame(groupMappings, frame);
			}
		}
	}


	/**
	 * Update the data packages with new data and return the list of packages.
	 * @param topic	topic of the data.
	 * @param data	raw data.
	 * @return	list data packages updated with new data.
	 */
	public List<DataPackage> getUpdatedPackets(String topic, Object data) {
		GWT.log("Matcher starting to parse data on topic "+topic, null);
		
		incomingTopic = topic;
		
		//Get the data frame that corresponds to the topic
		DataFrame dataFrame = this.dataFrames.get(topic);
		
		if(dataFrame != null) {
			Collection<Mapping> mappings = dataFrame.getMappingsSet();
			String dataType = dataFrame.getDataType();
			
			//Process the data object with the corresponding mappings.
			extractMessageData(mappings, data, dataType);
			
			//Go through the list of unfinished data packages and find packages good for publishing.
			processUnfinishedDataPackages();
		}
		
		return finishedDataPackages;
	}

	/**
	 * To process the received message object with its corresponding mappings and aggregate all the information according to mappings.
	 * @param mappings	the mappings that correspond to the message.
	 * @param data	the message data object.
	 * @param dataType	the data type of the message (JSON, string etc).
	 */
	private void extractMessageData(Collection<Mapping> mappings, Object data, String dataType) {
		//TODO: Optimize so that it would not parse through the whole JSON object each time. Parse JSON object only once and find the according mapping for each value.
		//For each mapping in the data frame get the according data value
		for(Mapping mapping: mappings) {
			
			//Get the data value based on the data type and corresponding mapping.
			CollectedDataValue dataValue = InstanceFactory.getDataValue(data, dataType, mapping);
			
			if(dataValue != null) {
				//Update data packages with the data value 
				processDataValue(dataValue);
			}
		}
	}
	
	/**
	 * To process the data value according to the mappings. If the data value is an array then, process each element in the array.
	 * @param dataValue	data value to be processed.
	 */
	private void processDataValue(CollectedDataValue dataValue) {
		
		Mapping mapping = dataValue.getCollectedMapping();
		
		if(mapping.isMappingElement() != null) {
			//If the mapping indicates that the data value is an atomic data element then
			//update data packages with the atomic data element.
			
			//Get the global reference of the data element.
			String globalReference = mapping.isMappingElement().getFirstGlobalReference();
			
			if(referenceMappingsToFrames.containsKey(globalReference)) {
				//If there are any data frames that can use the data with given global reference then
				
				//add the incoming data to the data packages.
				updateDataPackagesWithNewData(dataValue);
			}
		}
		else if(dataValue.isCollectedDataGroupsCollection() != null) {
			//If the data value is an array, then update data packages with each element in the array.
			//The data value then contains all the data elements in the repeating element group
			//And are added together to data packages.
			for(CollectedDataGroup dataGroup: dataValue.isCollectedDataGroupsCollection().getDataGroups()) {
				updateDataPackagesWithNewData(dataGroup);
				
				for(CollectedDataGroupsCollection dataCollections: dataGroup.getCollectedDataGroups()) {
					processDataValue(dataCollections);
				}
				
			}			
		}
	}
	
	/**
	 * Updates all the data packages with the new data value. Only those data packages are updated
	 * that have the data value's global reference in their frames' mappings.
	 *    
	 * @param dataValue	data value to update the packages with.
	 */
	private void updateDataPackagesWithNewData(CollectedDataValue dataValue) {
		Set<DataFrame> frameSet = new HashSet<DataFrame>();	//all the data frames that contain the global reference in their mappings.
		
		if(dataValue.isCollectedDataGroup() != null) {
			//Create set of frames so that there wouldn't be any duplicate entries because
			//we don't want to duplicate data
			CollectedDataGroup dataGroup = dataValue.isCollectedDataGroup();
			for(String globalRef: dataGroup.getGlobalReferences()) {
				Set<DataFrame> frames = referenceMappingsToFrames.get(globalRef);
				frameSet.addAll(frames);
			}
		}
		else if(dataValue.isAtomicDataValue() != null) {
			String globalReference = dataValue.isAtomicDataValue().getGlobalReference();
			frameSet = referenceMappingsToFrames.get(globalReference);
		}
		
		for(DataFrame frame: frameSet) {
			
			//Skip the data frame with the given topic because it is the same data frame belonging to the
			//incoming data.
			if(frame.getTopic().equals(incomingTopic) || frame.isOutputOnly())
				continue;
			
			//If there is already a data package with the data frame in the unfinished data packages list
			//then update the existing package with new data.
			//Otherwise create a new package with updated data and add it to the unfinished packages list 
			if(unfinishedDataPackages.containsKey(frame.getTopic()) && unfinishedDataPackages.get(frame.getTopic()).size() > 0) {
				
				//If the data value has been added to any package yet.
				boolean hasDataValueBeenAddedToAnyPackage = false;	
				
				//temporary list of unfinished data packages.
				List<DataPackage> newUnfinishedPackages = new ArrayList<DataPackage>();
				
				//Previous list of unfinished data packages. Used not to mix currently added packages with the packages added before.
				List<DataPackage> oldUnfinishedPackages = unfinishedDataPackages.remove(frame.getTopic());
				
				//Go through the data packages in the previous list of unfinished data packages.
				for(DataPackage dataPackage: oldUnfinishedPackages) {
					
					//If this data package already contains enough data similar to the dataValue 
					if(dataPackage.containsEnoughData(dataValue)) {
						//Then add the package to the new list of unfinished data packages and take a new data package.
						newUnfinishedPackages.add(dataPackage);
					}
					else {
						//Otherwise add the data value to the data package
						addDataToPackage(dataValue, dataPackage, newUnfinishedPackages);
						hasDataValueBeenAddedToAnyPackage = true;
					}
					
				}
				
				//If in the data value was not added to any existing data package, then it means that
				//the existing data packages are all filled with that data and a new data package can be created.
				if(hasDataValueBeenAddedToAnyPackage == false) {
					//Create a new data package and add the data value to the package.
					DataPackage dataPackage = frame.generateDataPackageInstance();
					addDataToPackage(dataValue, dataPackage, newUnfinishedPackages);
				}
				
				//Add the temporary list of unfinished data packages to the permanent list of unfinished data packages.
				if(newUnfinishedPackages.size() > 0)
					unfinishedDataPackages.put(frame.getTopic(), newUnfinishedPackages);
			}
			else {
				//Create a new data package because there was no existing data packages with partial data.
				createNewDataPackage(frame, dataValue);
			}
		}
	}
	
	/**
	 * Add the data value to the data package, and add the data package either to the list of finished data packages or to the
	 * list of unfinished data packages.
	 * 
	 * @param dataValue	the data value to be added to the data package.
	 * @param dataPackage	the data package where to add the data value.
	 * @param newUnfinishedPackages	temporary list of unfinished data packages.
	 */
	private void addDataToPackage(CollectedDataValue dataValue, DataPackage dataPackage, List<DataPackage> newUnfinishedPackages) {
		
		dataPackage.addDataValue(dataValue);
		
		//If the data package is finished, then add it to the list of finished packages.
		//Otherwise add the package to the temporary list of unfinished packages. 
		if(dataPackage.isFinished())
			finishedDataPackages.add(dataPackage);
		else
			newUnfinishedPackages.add(dataPackage);
	}

	/**
	 * To create a new data package based on the frame, add the data value to the data package,
	 * and add the package either to the finished or unfinished list of data packages.
	 * @param frame	data frame of the data package to be generated.
	 * @param dataValue	data value to be added to the data package.
	 */
	private void createNewDataPackage(DataFrame frame, CollectedDataValue dataValue) {
		DataPackage dataPackage = frame.generateDataPackageInstance();
		dataPackage.addDataValue(dataValue);
		
		//If the data package is finished, then add it to the list of finished packages.
		//Otherwise add the package to the list of unfinished packages. 
		if(dataPackage.isFinished())
			finishedDataPackages.add(dataPackage);
		else
			addPackageToUnfinishedList(dataPackage);
	}
	
	/**
	 * Add a data package to the list of unfinished data packages.
	 * @param dataPackage
	 */
	private void addPackageToUnfinishedList(DataPackage dataPackage) {
		
		String topic = dataPackage.getDataFrame().getTopic();
		
		if(unfinishedDataPackages.containsKey(topic)) {
			unfinishedDataPackages.get(topic).add(dataPackage);
		}
		else {
			List<DataPackage> packageList = new ArrayList<DataPackage>();
			packageList.add(dataPackage);
			unfinishedDataPackages.put(topic, packageList);
		}
		
	}
	
	//to find data packages with arrays that can be published.
	private void processUnfinishedDataPackages() {
		//TODO: optimize so it wouldn't have to be done.
		Iterator<List<DataPackage>> dataPackageListsIterator = unfinishedDataPackages.values().iterator();
		while(dataPackageListsIterator.hasNext()) {
			List<DataPackage> dataPackageLists = dataPackageListsIterator.next();
			
			Iterator<DataPackage> dataPackageIterator = dataPackageLists.iterator();
			while(dataPackageIterator.hasNext()) {
				DataPackage dataPackage = dataPackageIterator.next();
				if(dataPackage.isReadyToBePublished()) {
					finishedDataPackages.add(dataPackage);
					dataPackageIterator.remove();
				}
			}
			
			if(dataPackageLists.size() <= 0)
				dataPackageListsIterator.remove();
		}
	}

  public void addDataFrames(Map<String, DataFrame> dataFrames) {
    this.dataFrames.putAll(dataFrames);

    //Make a map object from the mappings.
    loadMappingsToMap(dataFrames);
  }

  public Map<String, Set<DataFrame>> getReferenceMappingsToFrames() {
    return referenceMappingsToFrames;
  }

  public Map<String, DataFrame> getDataFrames() {
    return dataFrames;
  }
}
