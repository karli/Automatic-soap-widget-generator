package soapproxy.web;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import soapproxy.application.mapping.MappingGenerator;
import soapproxy.application.mapping.SMBMappingGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class MappingController extends AbstractController {
  private static final String DEFAULT_XML_TYPE = "text/xml";

  @Override
  protected ModelAndView handleRequestInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

    httpServletResponse.setContentType(DEFAULT_XML_TYPE);

    String wsdlUri = httpServletRequest.getParameter("wsdl");
    String operationName = httpServletRequest.getParameter("operation");

    PrintWriter out = httpServletResponse.getWriter();

    MappingGenerator mappingGenerator = new SMBMappingGenerator(wsdlUri, operationName);

    out.print(mappingGenerator.getMapping());
//    String test = "<?xml version=\"1.0\" encoding=\"windows-1257\"?>\n" +
//                  "<blah><mappings xmlns:sawsdl=\"http://www.w3.org/ns/sawsdl\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ws=\"http://schemas.xmlsoap.org/wsdl/\">\n" +
//                  "</mappings>\n" +
//                  "<mappings xmlns:sawsdl=\"http://www.w3.org/ns/sawsdl\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ws=\"http://schemas.xmlsoap.org/wsdl/\">\n" +
//                  "</mappings></blah>";
//    out.print(test);
    return null;
  }
}