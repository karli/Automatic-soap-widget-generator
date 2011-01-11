package ee.stacc.transformer.client.data.json;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

import ee.stacc.transformer.client.data.DataPackage;

/**
 * For generating messages in JSON format.
 * 
 * @author Rainer Villido
 *
 */
public class JsonDataPackage extends DataPackage {
	
	public JsonDataPackage(JsonDataFrame dataFrame) {
		super(dataFrame);
	}

	@Override
	public Object getObjectToPublish() {
		//To publish this package as a javascript object.
		JSONGenerator jsonGenerator = new JSONGenerator(this);
		JSONObject json = jsonGenerator.transformToJSON().isObject();
		
		GWT.log("Transformed to JSON: "+json.toString(), null);
		
		return json.getJavaScriptObject();
	}

	@Override
	public String toString() {
    if (getObjectToPublish() != null) {
      return new JSONObject((JavaScriptObject)getObjectToPublish()).toString();
    }
    else {
      return null;
    }
	}

	public JSONObject getJsonSchema() {
		return ((JsonDataFrame)getDataFrame()).getJsonSchema();
	}
}
