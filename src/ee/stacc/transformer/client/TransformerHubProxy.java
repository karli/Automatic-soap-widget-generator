package ee.stacc.transformer.client;

/**
 * Proxy class for communicating with the OpenAjax Hub library. 
 * @author Rainer Villido
 *
 */
public class TransformerHubProxy {
	
	public TransformerHubProxy() {
	}
	
	/**
	 * This method connects the widget to the hub.
	 */
	public native void connectToHub(TransformerWidget transformerWidget) /*-{
		$wnd.hubClient = new $wnd.OpenAjax.hub.IframeHubClient({
			HubClient: {
				onSecurityAlert: transformerWidget.@ee.stacc.transformer.client.TransformerWidget::clientSecurityAlertHandler(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)
			}
		}); 
		$wnd.hubClient.connect($wnd.externalConnectCompleted = function ( hubClient, success, error ) {
			transformerWidget.@ee.stacc.transformer.client.TransformerWidget::connectCompleted(Lcom/google/gwt/core/client/JavaScriptObject;ZLcom/google/gwt/core/client/JavaScriptObject;)(hubClient, success, error);
		});
	}-*/;
	
	/**
	 * To subscribe to all topics that are passed through the hub.
	 * @param transformerWidget	the transformer widget instance, used for callback methods.
	 */
	public native void subscribeToTopics(TransformerWidget transformerWidget) /*-{
		
		//Calling subscribe command in the Hub. The first parameter is the topic name which is ** meaning thtat we subscribe to all topics.
		//The second parameter is the callback method which is called when the transformer widget receives data from the hub. 
		//The thid parameter is 'this' which is not used. The fourth parameter is the callback method which is called when the transformer widget 
		//has finished connecting to the hub. The fifth parameter is to enable caching when pagebus extended openajax hub is used.
		$wnd.hubClient.subscribe(
			"**", 
			$wnd.externalOnIncomeData = function(topic, publisherData, subscriberData) {
				transformerWidget.@ee.stacc.transformer.client.TransformerWidget::onIncomeData(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)(topic, publisherData, subscriberData);
			},
			null,
			$wnd.externalOnComplete = function(item, success, errCode) {
				transformerWidget.@ee.stacc.transformer.client.TransformerWidget::onSubscribeComplete(Z)(success);
			},
			{PageBus: {cache: true}}
		);
	}-*/;
	
	/**
	 * To publish a message through the hub. 
	 * @param topic	topic of the message.
	 * @param msg	message to be sent.
	 */
	public native void publish(String topic, Object msg) /*-{
		$wnd.hubClient.publish(topic, msg);
	}-*/;
}
