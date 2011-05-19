
// init the following global variables
var managedHub = null;
var mashupArea = null;
 // container to store soap widgets that have already been generated. serves the purpose of avoiding duplicates
var proxyWidgets = [];



/**
 * Sets up the application environment in a very simplistic way.
 * After setting up the env, it is possible to generate soap proxy widgets and add OAH widgets to the managedHub.
 * @param tunnelUrl
 * @param transformerWidgetUrl
 */
function setUpEnvironment(tunnelUrl, transformerWidgetUrl) {
  mashupArea = document.getElementById("mashupArea");
  /*
   * Create a Managed Hub instance
   */
  managedHub = new OpenAjax.hub.ManagedHub(
        {
          onPublish:       onMHPublish,
          onSubscribe:     onMHSubscribe,
          onUnsubscribe:   onMHUnsubscribe,
          onSecurityAlert: onMHSecurityAlert
        }
  );

  //Initiate Transformer Widget
  var transformerWidget = document.createElement( "span" );
  mashupArea.appendChild(transformerWidget);
  var container4 = new OpenAjax.hub.IframeContainer(managedHub , "transformerWidget",
    {
      Container: {
        onSecurityAlert: onClientSecurityAlert,
        onConnect:       onClientConnect,
        onDisconnect:    onClientDisconnect
      },
      IframeContainer: {
        // DOM element that is parent of this container:
        parent:      transformerWidget,
        // Container's iframe will have these CSS styles:
        iframeAttrs: { id: "smallHidden" },
        // Container's iframe loads the following URL:
        uri: transformerWidgetUrl,
        // Tunnel URL required by IframeHubClient. This particular tunnel URL
        // is the one that corresponds to release/all/OpenAjaxManagedHub-all.js:
        tunnelURI:  tunnelUrl
      }
    }
  );
}

function generateWidget(wsdlUri, operation, ProxyWidgetUri) {
  function onClientSecurityAlert(source, alertType) {  /* Handle client-side security alerts */
  }

  function onClientConnect(container) {        /* Called when client connects */
  }

  function onClientDisconnect(container) {     /* Called when client disconnects */
  }

  var widgetUri = ProxyWidgetUri + '?wsdl=' + wsdlUri + '&operation=' + operation;

  var serviceWidgetName = widgetUri;

  if (proxyWidgets[serviceWidgetName] != null) {
    alert('Avoiding adding duplicate service widget for ' + serviceWidgetName);
    return;
  }

  // Soap Service Widget
  proxyWidgets[serviceWidgetName] = document.createElement("span");
  mashupArea.appendChild(proxyWidgets[serviceWidgetName]);
  var container = new OpenAjax.hub.IframeContainer(managedHub, serviceWidgetName,
  {
    Container: {
      onSecurityAlert: onClientSecurityAlert,
      onConnect:       onClientConnect,
      onDisconnect:    onClientDisconnect
    },
    IframeContainer: {
      // DOM element that is parent of this container:
      parent:      proxyWidgets[serviceWidgetName],
      // Container's iframe will have these CSS styles:
      iframeAttrs: { id: "smallHidden" },
      // Container's iframe loads the following URL:
      uri: widgetUri,
      // Tunnel URL required by IframeHubClient. This particular tunnel URL
      // is the one that corresponds to release/all/OpenAjaxManagedHub-all.js:
      tunnelURI:  tunnel
    }
  }
          );
}

/*
 * Open Ajax Hub Security Manager Callbacks
 *
 * Can be overloaded to enable different functionality
 */
function onMHPublish(topic, data, publishContainer, subscribeContainer) {
  /* Callback for publish requests. This example approves all publish requests. */
  return true;
}
function onMHSubscribe(topic, container) {
  /* Callback for subscribe requests. This example approves all subscribe requests. */
  return true;
}
function onMHUnsubscribe(topic, container) {
  /* Callback for unsubscribe requests. This example approves all subscribe requests. */
  return true;
}
function onMHSecurityAlert(source, alertType) {  /* Callback for security alerts */  }


/*
 * Open Ajax Hub client side Callbacks.
 *
 * Can also be overloaded.
 */
function onClientSecurityAlert(source, alertType) {  /* Handle client-side security alerts */  }
function onClientConnect(container) {        /* Called when client connects */   }
function onClientDisconnect(container) {     /* Called when client disconnects */ }