package ee.stacc.transformer.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

import ee.stacc.transformer.client.data.DataFrame;

/**
 * For handling fetched schema files.
 * 
 * @author Rainer Villido
 *
 */
public class SchemaRPCResponseHandler implements RequestCallback {
	
	DataFrame dataFrame;
	
	public SchemaRPCResponseHandler(DataFrame dataFrame) {
		this.dataFrame = dataFrame;
	}

	@Override
	public void onResponseReceived(Request request, Response response) {
		//If the schema file was fetched, then load the schema to the data frame.
		if (200 == response.getStatusCode()) {
			String schema = response.getText();
			
			try {
				dataFrame.updateSchema(schema);
			} 
			catch (Exception e) {
				GWT.log("Error parsing schema",e);
				TransformerWidget.logToWidgetBody("Error parsing schema");
				TransformerWidget.logToWidgetBody(schema);
				TransformerWidget.logToWidgetBody(e.getLocalizedMessage());
			}
		} 
		else {
			GWT.log("Error getting response: "+response.getStatusText(), null);
		}
	}
	
	@Override
	public void onError(Request request, Throwable exception) {
		GWT.log("Couldn't connect to server", exception);
	}
}
