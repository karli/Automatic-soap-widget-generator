package ee.stacc.transformer.client.data.json;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import ee.stacc.transformer.client.data.AtomicDataValue;
import ee.stacc.transformer.client.data.CollectedDataValue;
import ee.stacc.transformer.client.mapping.Mapping;
import ee.stacc.transformer.client.mapping.MappingElement;


/**
 * To hold atomic data values in JSON format.
 * 
 * @author Rainer Villido
 *
 */
public class JsonDataValue extends AtomicDataValue {
	
	//JSON schema elements.
	public static String STRING = "string";
	public static String BOOLEAN = "boolean";
	public static String NUMBER = "number";
	public static String INTEGER = "integer";
	
	private JSONValue value;
	
	public JsonDataValue(JSONValue value, MappingElement collectedMapping) {
		this.value = value;
		this.collectedMapping = collectedMapping;
	}

	/**
	 * To generate JSON value from a string according to the type.
	 * @param data
	 * @param type
	 * @return
	 */
	public static JSONValue generateJSONValue(String data, String type) {
		if(STRING.equalsIgnoreCase(type))
			return new JSONString(data);
		else if (NUMBER.equalsIgnoreCase(type) || INTEGER.equalsIgnoreCase(type)) {
			try {
				double value = Double.parseDouble(data);
				return new JSONNumber(value);
			} catch (NumberFormatException e) {
				GWT.log("Error generating JSONValue", e);
				return new JSONString(data); 
			}
		}
		else if (BOOLEAN.equalsIgnoreCase(type)) {
			//boolean value = Boolean.getBoolean(data); Gives compilation errors
			boolean value = "true".equalsIgnoreCase(data);
			return JSONBoolean.getInstance(value);
		}
		else
			return new JSONString(data);
	}
	
	/**
	 * To create a new instance of a JsonDataValue from the message object according
	 * to the mapping of the message.
	 *  
	 * @param data	the message data object.
	 * @param mapping	the mapping corresponding to the data value.
	 * @return	data value extracted from the message object according to the mapping.
	 */
	public static CollectedDataValue getJsonDataValueInstance(Object data, Mapping mapping) {
		//Then parse the object as a JSON object and return data value that is JSON
		String path = mapping.getPath();
		JSONValue jsonDataValue = JSONGenerator.getJsonDataValue((JavaScriptObject)data, path, mapping);
		
		if(jsonDataValue == null) {
			GWT.log("Mismatch between data and mappings. No data at "+path, null);
			return null;
		}
    return JSONGenerator.getJsonDataValue(jsonDataValue, mapping);
	}
	
	@Override
	public JSONValue getJson(String type) {
		//To get a JSON object according to the type.
		if(STRING.equalsIgnoreCase(type) && value.isString() != null)
			return value;
		else if (BOOLEAN.equalsIgnoreCase(type) && value.isBoolean() != null)
			return value;
		else if ((NUMBER.equalsIgnoreCase(type) || (INTEGER.equalsIgnoreCase(type))) && value.isNumber() != null)
			return value;
		else
			return generateJSONValue(value.toString(), type);
	}
	
	@Override
	public String getStringValue() {
		return value.toString();
	}
	
	public String toString() {
		return value.toString();
	}
	
	public void setValue(JSONValue value) {
		this.value = value;
	}
  
}
