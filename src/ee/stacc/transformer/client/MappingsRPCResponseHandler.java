package ee.stacc.transformer.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

/**
 * Handler for loading mappings from an XML file.
 * 
 * @author Rainer Villido
 *
 */
public class MappingsRPCResponseHandler implements RequestCallback {
	
	private TransformerWidget transformerWidget;

	public MappingsRPCResponseHandler(TransformerWidget transformerWidget) {
		this.transformerWidget = transformerWidget;
	}

	@Override
	public void onResponseReceived(Request request, Response response) {
		//If the mappings xml file was fetched, then process the xml file.
		if (200 == response.getStatusCode()) {
			String mappingsXML = response.getText();
			transformerWidget.processMappings(mappingsXML);
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
