package ee.stacc.transformer.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;

import ee.stacc.transformer.client.data.DataFrame;
import ee.stacc.transformer.client.data.DataPackage;

/**
 * The main class for the transformer widget.
 * @author Rainer Villido
 *
 */
public class TransformerWidget implements EntryPoint {
	
	//The name of the mappings file where all the mappings are located.
	private static final String MAPPINGS_FILE = "mappings.xml";
  private static final String TOPIC_ADD_URL_MAPPING = "ee.stacc.transformer.mapping.add.url";
  private static final String TOPIC_REMOVE_URL_MAPPING = "ee.stacc.transformer.mapping.remove.url";

	//Proxy class for communicating with the hub.
	private TransformerHubProxy hubProxy = new TransformerHubProxy();
	
	//Matcher is doing the main transformation and aggregation logic.
	private Matcher matcher;
	
	//Lately published topics are recent topics that the transformer widget has published. It is used to keep the transformer for recursively transforming its own messages. 
	private Map<String, Integer> latelyPublishedTopics = new HashMap<String, Integer>();

  @Override
	public void onModuleLoad() {
		matcher = new Matcher();
		
		//Load all the mappings from the mappings file.
		fetchMappings();
		
		//Connect to the hub after all the initialization has been done
		hubProxy.connectToHub(this);
	}

	/**
	 * OpenAjax method for handling security alerts
	 * @param source	The Container or HubClient instance that raised the security alert
	 * @param alertType	OpenAjax.hub.SecurityAlert	- The alert type
	 */
	public void clientSecurityAlertHandler(JavaScriptObject source, JavaScriptObject alertType) {
		GWT.log("Seccurity alert", null);
	}
	
	/**
	 * An onComplete callback function is invoked when an asynchronous API call,
	 * such as HubClient.connect() or HubClient.subscribe(), completes.
	 * @param hubClient	Item on which the completed operation was invoked
	 * @param success	If operation succeeded (item is active) then true, else false
	 * @param error	OpenAjax.hub.Error - If success != true, then contains a string error code, else is undefined
	 */
	public void connectCompleted (JavaScriptObject hubClient, boolean success, JavaScriptObject error ) {
		GWT.log("Hub Connect Completed: "+success,null);
		if (success) {
			
			//Subscribe to topics in the hub.
			hubProxy.subscribeToTopics(this);
		}
	}
	
	/**
	 * When the transformer widget has successfully subscribed to all the topics and is now ready to transform data.
	 * @param success
	 */
	public void onSubscribeComplete(boolean success) {
		GWT.log("Completed subscribing to all the topics: "+success, null);
		
		//Publish that the transformer widget is ready.
		hubProxy.publish("ee.stacc.transformer.hasfinished", "true");
	}
	
	/**
	 * The callback method which is called every time the transformer widget receives a message from the hub.
	 * @param topic	the name of the topic of the message that is being received.
	 * @param publisherData	the message which is being received (sent by some widget).
	 * @param subscriberData	the same object which was specified when subscribing to topics was done.
	 */
	public void onIncomeData(String topic, Object publisherData, Object subscriberData) {
		GWT.log("onIncomeData: "+topic+", data: "+publisherData+", subscriberData: "+subscriberData,null);

    if (TOPIC_ADD_URL_MAPPING.equals(topic)) {
      addUrlMapping(publisherData);
      return;
    }

    if (TOPIC_REMOVE_URL_MAPPING.equals(topic)) {
      // TODO remove mapping
      return;
    }
		
		//Check if the transformer widget has not just published the same message. It is to keep the transformer from falling into endless recursive loop.
		if(hasNotLatelyPublishedTopic(topic)) {
			
			logToWidgetBody("Received topic: "+topic);
			
			try {
				//Get all the data packages which have been finished after aggregating data from the publisherData.
				List<DataPackage> packages = matcher.getUpdatedPackets(topic, publisherData);
				GWT.log("Got "+packages.size()+" of packages", null);
				
				//Publish all the finished packages ready to be sent to other widgets.
				publishPackages(packages);
				
				//Clear the packages buffer
				packages.clear();
			} 
			catch (Exception e) {
				GWT.log("Error in handling the received message",e);
				logToWidgetBody("Error in handling the recieved message");
				logToWidgetBody(e.getLocalizedMessage());
			}
			
		}
		else {
			GWT.log("Is not handling data because has just published data under the same topic. This is probably transformed data.", null);
			//Is not handling data because has just published data under the same topic. This is probably transformed data.
			//Remove the topic from recently published topics list.
			substractLatelyPublishedTopic(topic);
		}
		
	}

  private void addUrlMapping(Object publisherData) {
    // We expect a simple string that represents the url of mapping document
    String mappingUrl = (String)publisherData;
    fetchMappingsFromUrl(mappingUrl);
  }

  /**
	 * To publish packages to the hub for other widgets.
	 * @param packages	list of packages to publish.
	 */
	private void publishPackages(List<DataPackage> packages) {
		
		//Publish each package from the packages list.
		for(DataPackage dataPackage: packages) {
			
			try {
				String topic = dataPackage.getDataFrame().getTopic();
				Object objectToPublish = dataPackage.getObjectToPublish();
				
				//Puts the topic to the 'latelyPublishedTopicsList' that the transformer widget is about to publish.
				addLatelyPublishedTopic(topic);
				
				GWT.log("Publishing the object under the topic "+topic, null);
				hubProxy.publish(topic, objectToPublish);
				
				logToWidgetBody("Published topic: "+topic);
			} 
			catch (Exception e) {
				GWT.log("Error publishing message.",e);
				logToWidgetBody("Error publishing message: "+dataPackage.getDataFrame().getTopic());
				logToWidgetBody(e.getLocalizedMessage());
			}
		}
	}
	
	/**
	 * To add a topic to the list of lately published topics which is used to temporarily keep topics in order to
	 * keep the transformer widget from falling into endless recursive loop.
	 * If a topic is in the list then the transformer wont process messages received from that topic.
	 * @param topic	name of the topic.
	 */
	private void addLatelyPublishedTopic(String topic) {
		if(latelyPublishedTopics.containsKey(topic)) {
			int topicCount = latelyPublishedTopics.get(topic).intValue() + 1;
			latelyPublishedTopics.put(topic, new Integer(topicCount));
		}
		else {
			latelyPublishedTopics.put(topic, new Integer(1));
		}
	}
	
	/**
	 * To remove a topic from the list of lately published topics.
	 * The transformer widget can then start processing messages with that topic.
	 * @param topic	name of the topic.
	 */
	private void substractLatelyPublishedTopic(String topic) {
		if(latelyPublishedTopics.containsKey(topic)) {
			int topicCount = latelyPublishedTopics.get(topic).intValue() - 1;
			if(topicCount > 0)
				latelyPublishedTopics.put(topic, new Integer(topicCount));
			else
				latelyPublishedTopics.remove(topic);
		}
	}
	
	/**
	 * To check if the transformer widget has just published a message with that topic.
	 * @param topic	the name of the topic.
	 * @return	if the transformer widget has recently published messages with that topic.
	 */
	private boolean hasNotLatelyPublishedTopic(String topic) {
		if(latelyPublishedTopics.containsKey(topic)) {
			//return true if the number of published packages under the topic is zero. 
			return latelyPublishedTopics.get(topic).intValue() <= 0;
		}
		else
			return true;
	}
	
	/**
	 * To load mappings from the mappings file.
	 */
	private void fetchMappings() {
		
		String url = GWT.getHostPageBaseURL() + MAPPINGS_FILE;
    fetchMappingsFromUrl(url);
	}

  private void fetchMappingsFromUrl(String url) {
    GWT.log("Fetching data from "+url, null);

    //Make a RPC call to fetch the mappings file
    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(url));
    MappingsRPCResponseHandler mappingsRequestCallback = new MappingsRPCResponseHandler(this);

    try {
      builder.sendRequest(null, mappingsRequestCallback);
    }
    catch (RequestException e) {
      GWT.log("Could not connect to server", e);
    }
  }


  /**
	 * To load mappings from the XML string. This is a callback method of the RPC call that loads the mappings file.
	 * @param mappingsXml	the string representation of the mappings XML file.
	 */
	public void processMappings(String mappingsXml) {
		GWT.log("Received following mappings XML: " + mappingsXml, null);
		
		try {
			//Parse the xml
			Document xml = XMLParser.parse(mappingsXml);
			
			//Load data frames from the xml
			Map<String, DataFrame> dataFrames = DataFrame.loadDataFrames(xml);
      matcher.addDataFrames(dataFrames);

			//Load schemas for each data frame
			for(DataFrame dataFrame: dataFrames.values()) {
				if(dataFrame.isOutputOnly() == false && dataFrame.getSchemaURL()!=null && dataFrame.getSchemaURL().isEmpty() == false)
					dataFrame.loadSchema();
			}
		} 
		catch (Exception e) {
			GWT.log("Error loading mappings",e);
			logToWidgetBody("Error loading mappings: ");
			logToWidgetBody(e.getLocalizedMessage());
		}
	}
	
	/**
	 * Method for logging messages to the transformer widget's body. For developing purposes only.
	 * @param msg	message to log.
	 */
	public static void logToWidgetBody(String msg) {
		RootPanel.get("messageArea").add(new Label(msg));
	}

}
