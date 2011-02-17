package soapproxy.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import soapproxy.application.mapping.MappingGenerator;
import soapproxy.application.mapping.SMBMappingGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Controller
public class MappingController{
  private static final String DEFAULT_XML_TYPE = "text/xml";

  @RequestMapping("/mapping")
  protected ModelAndView getMapping(@RequestParam("wsdl") String wsdl, @RequestParam("operation") String operation, HttpServletRequest request, HttpServletResponse response) throws Exception {

    response.setContentType(DEFAULT_XML_TYPE);

    PrintWriter out = response.getWriter();

    MappingGenerator mappingGenerator = new SMBMappingGenerator(wsdl, operation, request);

    out.print(mappingGenerator.getMapping());
    return null;
  }
}