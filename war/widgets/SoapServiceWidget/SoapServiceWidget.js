dojo.require("dojo.io.script");
dojo.require("dojox.rpc.Service");
dojo.require("dojox.rpc.JsonRPC");

function onSoapServiceData(topic, publisherData, subscriberData, smdUrl, hubClient, operation, outputTopic) {
  alert("onSoapServiceData");
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
  var deferred = services[operation](requestData);
  deferred.addCallback(function(result){
     // TODO publish returned value into hub 
    alert(result);
    hubClient.publish(outputTopic, result);
  });
  deferred.addErrback(function (){alert("Error")});
  return deferred;
}