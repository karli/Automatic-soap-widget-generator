package soapproxy.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Controller
public class SmdController {
  private static final String DEFAULT_JAVASCRIPT_TYPE = "text/javascript";

  @RequestMapping("/smd")
  protected ModelAndView getSmd(@RequestParam("wsdl") String wsdl,
                                @RequestParam("operation") String operation,
                                HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
    httpServletResponse.setContentType(DEFAULT_JAVASCRIPT_TYPE);

    String baseUrl = httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName() + ":" +
                httpServletRequest.getServerPort() + httpServletRequest.getContextPath();

    PrintWriter out = httpServletResponse.getWriter();
    out.write(httpServletRequest.getParameter("callback") + "(");
    out.write("{\n" +
              "    transport:\"JSONP\", // We will use POST as the transport\n" +
              "    envelope:\"JSON-RPC-2.0\", // We will use JSON-RPC\n" +
              "    SMDVersion:\"2.0\",\n" +
              "    services: {\n" +
              "      " + operation + ": {\n" +
              "        // this defines the URL to connect for the services\n" +
              "        target:   \"" + baseUrl + "/proxy?wsdl=" + wsdl + "&operation=" + operation + "\"\n" +
              "      }\n" +
              "    }\n" +
              "  }");
    out.write(");");
    return null;
  }
}
