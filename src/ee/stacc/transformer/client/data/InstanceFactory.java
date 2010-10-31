package ee.stacc.transformer.client.data;

import ee.stacc.transformer.client.data.json.JsonDataFrame;
import ee.stacc.transformer.client.data.json.JsonDataValue;
import ee.stacc.transformer.client.data.string.StringDataFrame;
import ee.stacc.transformer.client.data.string.StringDataValue;
import ee.stacc.transformer.client.data.xml.XmlDataFrame;
import ee.stacc.transformer.client.mapping.Mapping;

/**
 * Factory class for generating DataFrame instances and CollectedDataValue instances
 * according to the data types.
 * 
 * If support for a new data type is to be implemented, then those methods here have to
 * be extended to return object instances of the new data type.
 * 
 * @author Rainer Villido
 *
 */
public class InstanceFactory {
	
	//Implemented data types
	public static final String JSON_DATA_TYPE = "json";
	public static final String XML_DATA_TYPE = "xml";
	public static final String STRING_DATA_TYPE = "string";
	
	/**
	 * Method for creating new instances of DataFrame implementations based on the data type.
	 * 
	 * If support for a new data type is to be implemented, then this method must be updated to
	 * return a new instance of the implemented DataFrame class which implements the new data types.
	 * 
	 * @param dataType	the name of the data type.
	 * @return	DataFrame instance according to the data type.
	 */
	public static DataFrame getDataFrame(String dataType) {
		if(dataType.equalsIgnoreCase(JSON_DATA_TYPE)) {
			return new JsonDataFrame();
		}
		else if(dataType.equalsIgnoreCase(STRING_DATA_TYPE)) {
			return new StringDataFrame();
		}
		else if(dataType.equalsIgnoreCase(XML_DATA_TYPE)) {
			return new XmlDataFrame();
		}
		else {
			//TODO: implement for other data types
			return null;
		}
	}
	
	/**
	 * Extract the data value from the raw data object. 
	 * @param data	raw data.
	 * @param dataType	data type of the raw data.
	 * @param path	path of how to extract the value if the raw data is structured somehow (e.g. JSON or XML).
	 * @return	data value (json, xml, string etc data value).
	 */
	public static CollectedDataValue getDataValue (Object data, String dataType, Mapping mapping) {
		if(dataType.equals(JSON_DATA_TYPE)) {
			//If the incoming data is a JSON object
			return JsonDataValue.getJsonDataValueInstance(data, mapping);
		}
		else if(dataType.equals(STRING_DATA_TYPE)) {
			//If the incoming data is a String object than
			return StringDataValue.getStringDataValueInstance(data, mapping);
		}
		else {
			//TODO: implement for any other data types
		}
		return null;
	}

}
