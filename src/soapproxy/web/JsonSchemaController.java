package soapproxy.web;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import soapproxy.application.schema.DefaultJsonSchemaConverter;
import soapproxy.application.schema.JsonSchemaConverter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class JsonSchemaController extends AbstractController {
  private static final String DEFAULT_JAVASCRIPT_TYPE = "text/javascript";
  @Override
  protected ModelAndView handleRequestInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
    httpServletResponse.setContentType(DEFAULT_JAVASCRIPT_TYPE);

    // get the request params
    String wsdlUri = httpServletRequest.getParameter("wsdl");
    String operationName = httpServletRequest.getParameter("operation");
    // TODO: use provided message type
    String message = httpServletRequest.getParameter("message");

    PrintWriter out = httpServletResponse.getWriter();
    JsonSchemaConverter converter = new DefaultJsonSchemaConverter(wsdlUri, operationName, DefaultJsonSchemaConverter.MessageType.INPUT_MESSAGE);
    out.write(converter.getJsonSchema());
//    out.write(httpServletRequest.getParameter("callback") + "(");
//    out.write("{\"description\":\"A schema for name of a person\",\n" +
//              "\t\"type\":\"object\",\n" +
//              "\t\"properties\":{\n" +
//              "\t\t\"name\":{\"type\":\"string\"}\n" +
//              "\t}\n" +
//              "}");
//    out.write(");");
    return null;
  }
}
