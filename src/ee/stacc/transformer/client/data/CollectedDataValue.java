package ee.stacc.transformer.client.data;

import ee.stacc.transformer.client.mapping.Mapping;


/**
 * Interface for all the data values that are collected form a message object.
 * 
 * @author Rainer Villido
 *
 */
public interface CollectedDataValue {
		
	/**
	 * If the object is CollectedDataGroup then return the object as the CollectedDataGroup object. Return null otherwise. 
	 * @return CollectedDataGroup object or null.
	 */
	public CollectedDataGroup isCollectedDataGroup();
	
	/**
	 * If the object is CollectedDataGroupsCollection then return the object as the CollectedDataGroupsCollection object. Return null otherwise. 
	 * @return CollectedDataGroupsCollection object or null.
	 */
	public CollectedDataGroupsCollection isCollectedDataGroupsCollection();
	
	/**
	 * If the object is AtomicDataValue then return the object as the AtomicDataValue object. Return null otherwise. 
	 * @return AtomicDataValue object or null.
	 */
	public AtomicDataValue isAtomicDataValue();
	
	/**
	 * To get the mapping corresponding to the data value collected from a message.
	 * @return	mapping of the data value.
	 */
	public Mapping getCollectedMapping();
	
}
