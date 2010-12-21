function generateWidget(wsdlUri, operation, soapServiceWidgetUri, existingServiceWidgets) {
  function onClientSecurityAlert(source, alertType) {  /* Handle client-side security alerts */
  }

  function onClientConnect(container) {        /* Called when client connects */
  }

  function onClientDisconnect(container) {     /* Called when client disconnects */
  }

  var widgetUri = soapServiceWidgetUri + '?wsdl=' + wsdlUri + '&operation=' + operation;

  var serviceWidgetName = widgetUri;

  if (existingServiceWidgets[serviceWidgetName] != null) {
    alert('Avoiding adding duplicate service widget for ' + serviceWidgetName);
    return;
  }

  // Soap Service Widget
  existingServiceWidgets[serviceWidgetName] = document.createElement("span");
  mashupArea.appendChild(existingServiceWidgets[serviceWidgetName]);
  var container = new OpenAjax.hub.IframeContainer(managedHub, serviceWidgetName,
  {
    Container: {
      onSecurityAlert: onClientSecurityAlert,
      onConnect:       onClientConnect,
      onDisconnect:    onClientDisconnect
    },
    IframeContainer: {
      // DOM element that is parent of this container:
      parent:      existingServiceWidgets[serviceWidgetName],
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