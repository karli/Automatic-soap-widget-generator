package ee.stacc.transformer.client.data.json;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import ee.stacc.transformer.client.data.DataFrame;
import ee.stacc.transformer.client.data.DataPackage;
import ee.stacc.transformer.client.data.InstanceFactory;

/**
 * Represent the JSON data frame.
 * @author Rainer Villido
 *
 */
public class JsonDataFrame extends DataFrame {
	
	//schema as a JSON object.
	private JSONObject jsonSchema;

	@Override
	public void updateSchema(String schemaTxt) {
		//update the JSON schema object according to the textual form of the schema.
		JSONObject jsonSchema = JSONParser.parse(schemaTxt).isObject();
		setJsonSchema(jsonSchema);
		GWT.log("Got Schema: "+jsonSchema,null);
	}
	
	@Override
	public DataPackage generateDataPackageInstance() {
		return new JsonDataPackage(this);
	}
	
	@Override
	public String getDataType() {
		return InstanceFactory.JSON_DATA_TYPE;
	}

	public JSONObject getJsonSchema() {
		return jsonSchema;
	}

	public void setJsonSchema(JSONObject schema) {
		this.jsonSchema = schema;
	}
}
