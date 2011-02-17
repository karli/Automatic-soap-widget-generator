package soapproxy.web;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import soapproxy.application.Json2Soap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

@Controller
public class ProxyController {
  private static final String DEFAULT_JAVASCRIPT_TYPE = "text/javascript";

  @RequestMapping("/proxy")
  protected ModelAndView handleRequest(@RequestParam("wsdl") String wsdl,
                                       @RequestParam("operation") String operation,
                                       HttpServletRequest httpServletRequest,
                                       HttpServletResponse httpServletResponse) throws Exception {
    // read request parameters
    httpServletResponse.setContentType(DEFAULT_JAVASCRIPT_TYPE);
    PrintWriter out = httpServletResponse.getWriter();

    JsonNode jsonRpcRequest = getJsonRpcRequest(httpServletRequest);
    JsonNode requestParams = jsonRpcRequest.get("params");

    Json2Soap j2s = new Json2Soap();
    String jsonResponse = j2s.convert(requestParams.toString(), wsdl, operation);

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
