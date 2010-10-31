package ee.stacc.transformer.client.data;


/**
 * Interface for all the data values that are assembled together to form a new message.
 * 
 * @author Rainer Villido
 *
 */
public interface GeneratedDataValue {
	
	/**
	 * If the object is GeneratedDataGroupsCollection then return the object as the GeneratedDataGroupsCollection object. Return null otherwise. 
	 * @return GeneratedDataGroupsCollection object or null.
	 */
	public GeneratedDataGroupsCollection isGeneratedDataGroupsCollection();
}
