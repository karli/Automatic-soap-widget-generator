package soapproxy.web;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import soapproxy.components.smd.SmdGenerator;
import soapproxy.components.smd.SmdGeneratorImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Controller
public class SmdController {
  private final Logger LOG = Logger.getLogger(getClass());
  private static final String DEFAULT_JAVASCRIPT_TYPE = "text/javascript";

  @RequestMapping("/smd")
  protected ModelAndView getSmd(@RequestParam("wsdl") String wsdlDocumentUrl,
                                @RequestParam("operation") String operationName,
                                HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
    LOG.debug("Got request for SMD document");
    httpServletResponse.setContentType(DEFAULT_JAVASCRIPT_TYPE);

    String baseUrl = getBaseUrl(httpServletRequest);
    SmdGenerator smdGenerator = new SmdGeneratorImpl();
    String smd = smdGenerator.getSmd(baseUrl, wsdlDocumentUrl, operationName);

    PrintWriter out = httpServletResponse.getWriter();
    String callbackFunction = httpServletRequest.getParameter("callback");
    if (callbackFunction != null) {
      out.write(callbackFunction + "(" + smd + ");");
    } else {
      out.write(smd);
    }
    return null;
  }

  private String getBaseUrl(HttpServletRequest httpServletRequest) {
    String baseUrl = httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName() + ":" +
                httpServletRequest.getServerPort() + httpServletRequest.getContextPath();
    return baseUrl;
  }


}
