package ee.stacc.transformer.client.data;

import java.util.HashSet;
import java.util.Set;

import ee.stacc.transformer.client.mapping.RepeatingElementGroup;

/**
 * For keeping data elements grouped together that are collected from a repeatable element group in a message.
 * If an object in an array has more than one element then those elements are grouped together in this class.
 * 
 * For example, if a message contains an array of coordinates, containing pairs of latitude and longitude,
 * then for each pair of coordinates, there would be a separate instance of CollectedDataGroup with
 * collected data elements of latitude and longitude.       
 * 
 * @author Rainer Villido
 *
 */
public class CollectedDataGroup implements CollectedDataValue {
	
	/**
	 * Collection of collected atomic data values that are grouped together (e.g. latitude and longitude). 
	 */
	private Set<AtomicDataValue> collectedAtomicData = new HashSet<AtomicDataValue>();
	
	//Collected data value groups
	private Set<CollectedDataGroupsCollection> collectedDataGroups = new HashSet<CollectedDataGroupsCollection>();
	
	/**
	 * Collection of global references of data contained in this data group.
	 */
	private Set<String> globalReferences = new HashSet<String>();
	
	/**
	 * Mappings that correspond to the group of data in the received message where the data was collected.
	 */
	private RepeatingElementGroup collectedMapping;
	
	public CollectedDataGroup(RepeatingElementGroup collectedMapping) {
		this.collectedMapping = collectedMapping;
	}
	
	@Override
	public CollectedDataGroup isCollectedDataGroup() {
		return this;
	}
	
	/**
	 * Add new data element to the group.
	 * @param globalReference	the global reference of the data element.
	 * @param dataValue	the data value that was collected from a message.
	 */
	public void addDataValue(String globalReference, CollectedDataValue dataValue) {
		globalReferences.add(globalReference);
		
		if(dataValue.isAtomicDataValue() != null) {
			collectedAtomicData.add(dataValue.isAtomicDataValue());
		}
		if(dataValue.isCollectedDataGroupsCollection() != null) {
			collectedDataGroups.add(dataValue.isCollectedDataGroupsCollection());
		}
	}

	/**
	 * @return the collectedAtomicData
	 */
	public Set<AtomicDataValue> getCollectedAtomicData() {
		return collectedAtomicData;
	}

	/**
	 * @return the collectedDataGroups
	 */
	public Set<CollectedDataGroupsCollection> getCollectedDataGroups() {
		return collectedDataGroups;
	}

	/**
	 * @return the globalReferences
	 */
	public Set<String> getGlobalReferences() {
		return globalReferences;
	}

	@Override
	public CollectedDataGroupsCollection isCollectedDataGroupsCollection() {
		return null;
	}

	@Override
	public AtomicDataValue isAtomicDataValue() {
		return null;
	}

	@Override
	public RepeatingElementGroup getCollectedMapping() {
		return collectedMapping;
	}
	

	@Override
	public String toString() {
		String output = collectedMapping.getPath() + ": ";
		
		for(CollectedDataValue dataValue: collectedAtomicData) {
			output = output + dataValue.toString() + "| ";
		}
		
		return output;
	}
}
