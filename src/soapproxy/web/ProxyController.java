package soapproxy.web;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import soapproxy.application.Json2Soap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

public class ProxyController extends AbstractController {
  private static final String DEFAULT_JSON_CONTENT_TYPE = "application/json";
  private static final String DEFAULT_JAVASCRIPT_TYPE = "text/javascript";
  public static final Logger logger = Logger.getLogger(ProxyController.class);

  @Override
  protected ModelAndView handleRequestInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
    // read request parameters
    httpServletResponse.setContentType(DEFAULT_JAVASCRIPT_TYPE);
    PrintWriter out = httpServletResponse.getWriter();

    String wsdlUri = httpServletRequest.getParameter("wsdl");
    String operationName = httpServletRequest.getParameter("operation");
    
    JsonNode jsonRpcRequest = getJsonRpcRequest(httpServletRequest);
    JsonNode requestParams = jsonRpcRequest.get("params");

    Json2Soap j2s = new Json2Soap();
    //    String wsdlUri = "http://localhost/webservice/soap/soap.php?wsdl";
    //    String operationName = "sayHello";
    String jsonResponse = j2s.convert(requestParams.toString(), wsdlUri, operationName);

    out.write(httpServletRequest.getParameter("callback") + "(");
    out.write("{\"result\":" + jsonResponse + ",\"id\":\"0\",\"error\":null,\"jsonrpc\":\"2.0\"}");
    out.write(");");
    return null;
  }

  private JsonNode getJsonRpcRequest(HttpServletRequest httpServletRequest) throws IOException {
    Map paramMap = httpServletRequest.getParameterMap();
    String jsonRequest = null;
    for (Iterator i = paramMap.entrySet().iterator(); i.hasNext();) {
      Map.Entry<String,String[]> current = (Map.Entry<String, String[]>)i.next();
      if (current.getValue()[0].equals("")) {
        jsonRequest = current.getKey();
        break;
      }
    }

    if (jsonRequest != null) {
      ObjectMapper mapper = new ObjectMapper();
      // convert the transformation result to JSON
      return mapper.readValue(jsonRequest, JsonNode.class);
    } 
    return null;
  }


}
