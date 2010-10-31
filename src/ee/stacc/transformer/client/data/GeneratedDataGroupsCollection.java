package ee.stacc.transformer.client.data;

import java.util.ArrayList;
import java.util.List;

/**
 * For keeping a collection of groups of data elements to be assembled to a new message.
 * 
 * @author Rainer Villido
 *
 */
public class GeneratedDataGroupsCollection implements GeneratedDataValue {
	
	/**
	 * List of data element groups that are finished, i.e. groups contain all the necessary data elements.
	 */
	private List<GeneratedDataGroup> finishedDataGroups = new ArrayList<GeneratedDataGroup>();
	
	/**
	 * List of data element groups that are not finished, i.e. some data elements are still missing from thos groups.
	 */
	private List<GeneratedDataGroup> unfinishedDataGroups = new ArrayList<GeneratedDataGroup>();
	
	/**
	 * Finds the first data group that doesn't have data value with the global ref.
	 * @param globalRef	global reference.
	 * @return	unfinished data group.
	 */
	public GeneratedDataGroup getUnfinishedDataGroup(String globalRef) {
		
		//Return the first data group that doesn't have data with the global ref.
		for(GeneratedDataGroup dataGroup: unfinishedDataGroups) {
			if(dataGroup.hasData(globalRef) == false)
				return dataGroup;
		}
		
		return null;
	}
	
	/**
	 * To check if the collection of data element groups contain only data groups that are finished, i.e. contain
	 * all the necessary data so that a new message can be created.
	 * 
	 * @return	true if the collection has only data groups that are finished.
	 */
	public boolean hasOnlyFinishedDataGroups() {
		return unfinishedDataGroups.size() <= 0 && finishedDataGroups.size() > 0;
	}
	
	/**
	 * Moves the data group from the unfinished data groups list to the finished data groups list.
	 * @param group	data group to move.
	 */
	public void moveGroupToFinishedList(GeneratedDataGroup group) {
		unfinishedDataGroups.remove(group);
		finishedDataGroups.add(group);
	}
	
	/**
	 * To add new data group that is not finished.
	 * @param dataGroup
	 */
	public void addUnfinishedDataGroup(GeneratedDataGroup dataGroup) {
		unfinishedDataGroups.add(dataGroup);
	}

	/**
	 * To retrieve the list of data element groups that are finished.
	 * @return
	 */
	public List<GeneratedDataGroup> getFinishedDataGroups() {
		return finishedDataGroups;
	}

	/**
	 * To retrieve the list of data element groups that are not finished.
	 * @return the unfinishedDataGroups
	 */
	public List<GeneratedDataGroup> getUnfinishedDataGroups() {
		return unfinishedDataGroups;
	}

	@Override
	public GeneratedDataGroupsCollection isGeneratedDataGroupsCollection() {
		return this;
	}

	
}
