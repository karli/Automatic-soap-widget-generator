package ee.stacc.transformer.client.data;

import java.util.ArrayList;
import java.util.List;

import ee.stacc.transformer.client.mapping.RepeatingElementGroup;

/**
 * For keeping an array of data element groups that are collected from a message that contains repeatable data element groups.
 * 
 * For example, if a message contains an array of coordinates with pairs of latitude and longitude,
 * then those pairs are grouped together into CollectedDataGroup objects. This class keeps a collection of those CollectedDataGroup
 * objects. 
 * 
 * @author Rainer Villido
 *
 */
public class CollectedDataGroupsCollection implements CollectedDataValue {
	
	private RepeatingElementGroup collectedMapping;
	
	public CollectedDataGroupsCollection(RepeatingElementGroup collectedMapping) {
		this.collectedMapping = collectedMapping;
	}
	
	/**
	 * Collection of groups of data elements.
	 */
	private List<CollectedDataGroup> dataGroups = new ArrayList<CollectedDataGroup>();

	/**
	 * @return the dataGroups
	 */
	public List<CollectedDataGroup> getDataGroups() {
		return dataGroups;
	}
	
	/**
	 * To add new data elements group that is collected from a message that contains repeatable data elements. 
	 * @param collectedDataGroup
	 */
	public void addCollectedDataGroup(CollectedDataGroup collectedDataGroup) {
		dataGroups.add(collectedDataGroup);
	}

	@Override
	public CollectedDataGroup isCollectedDataGroup() {
		return null;
	}

	@Override
	public CollectedDataGroupsCollection isCollectedDataGroupsCollection() {
		return this;
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
		return collectedMapping.getPath() + " w " + dataGroups.size() + " els";
	}
}
