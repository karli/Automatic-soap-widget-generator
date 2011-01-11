package ee.stacc.transformer.client.data.string;

import com.google.gwt.json.client.JSONValue;

import ee.stacc.transformer.client.data.AtomicDataValue;
import ee.stacc.transformer.client.data.CollectedDataValue;
import ee.stacc.transformer.client.data.json.JsonDataValue;
import ee.stacc.transformer.client.mapping.Mapping;
import ee.stacc.transformer.client.mapping.MappingElement;



/**
 * To hold atomic data values in String format.
 * 
 * @author Rainer Villido
 *
 */
public class StringDataValue extends AtomicDataValue {
	
	private String value;

  public StringDataValue(String data, MappingElement mapping) {
    super(mapping);
    this.value = data;
  }
	
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public JSONValue getJson(String type) {
		return JsonDataValue.generateJSONValue(value, type);
	}

	@Override
	public String getStringValue() {
		return value;
	}

	/**
	 * Convert the data object to a string data value. 
	 * @param data	data value.
	 * @param mapping
	 * @return
	 */
	public static CollectedDataValue getStringDataValueInstance(Object data, Mapping mapping) {
		//consider the whole data value to be the string.
		
		if(mapping.isMappingElement() != null) {
			StringDataValue dataValue = new StringDataValue((String)data, mapping.isMappingElement());
			return dataValue;
		}
		
		return null;
	}

}
