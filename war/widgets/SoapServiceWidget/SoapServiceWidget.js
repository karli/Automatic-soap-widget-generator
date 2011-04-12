dojo.require("dojo.io.script");
dojo.require("dojox.rpc.Service");
dojo.require("dojox.rpc.JsonRPC");

function onSoapServiceData(topic, publisherData, subscriberData, smdUrl, hubClient, operation, outputTopic) {
  loadSmd(smdUrl, publisherData, hubClient, operation, outputTopic);
}

function loadSmd(smdUrl, publisherData, hubClient, operation, outputTopic){
  var smdDeferred = dojo.io.script.get({
    url:smdUrl,
    jsonp:"callback"});
  smdDeferred.addCallback(function(result) {
    callService(result, publisherData, operation, hubClient, outputTopic);
  });
}

function callService(smd, requestData, operation, hubClient, outputTopic){
  var services = new dojox.rpc.Service(smd);
  // lets try to specify the request ID
  var d = new Date();
  services._requestId = d.valueOf();
  var deferred = services[operation](requestData);
  deferred.addCallback(function(result){
    hubClient.publish(outputTopic, result);
  });
  deferred.addErrback(function (){alert("Error")});
  return deferred;
}