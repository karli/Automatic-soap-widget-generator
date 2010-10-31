package ee.stacc.transformer.client.data;

import com.google.gwt.json.client.JSONValue;

/**
 * Abstract class for all the atomic data value classes.
 * 
 * If support for additional data formats (XML for example) 
 * is to be implemented in the future then 
 * a new class that extends AtomicDataValue interface has to be created to hold 
 * atomic data values in that new data format.
 * 
 * @author Rainer Villido
 *
 */
public abstract class AtomicDataValue implements CollectedDataValue, GeneratedDataValue {
	
	/**
	 * the global reference of the atomic data value
	 */
	private String globalReference;
	
	/**
	 * To get the data value in JSON format according to the type.
	 * @param type	JSON data type.
	 * @return	the data value in JSON.
	 */
	public abstract JSONValue getJson(String type);
	
	/**
	 * To get the data value in String.
	 * @return	data value in string.
	 */
	public abstract String getStringValue();

	/**
	 * To get the global reference of the atomic data value.
	 * @return	global reference of the atomic data value.
	 */
	public String getGlobalReference() {
		return this.globalReference;
	}
	
	public void setGlobalReference(String globalReference) {
		this.globalReference = globalReference;
	}
	
	@Override
	public AtomicDataValue isAtomicDataValue() {
		return this;
	}
	
	@Override
	public CollectedDataGroup isCollectedDataGroup() {
		return null;
	}

	@Override
	public CollectedDataGroupsCollection isCollectedDataGroupsCollection() {
		return null;
	}

	@Override
	public GeneratedDataGroupsCollection isGeneratedDataGroupsCollection() {
		return null;
	}
}
