package soapproxy.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import soapproxy.application.schema.DefaultJsonSchemaConverter;
import soapproxy.application.schema.JsonSchemaConverter;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Controller
public class JsonSchemaController {
  private static final String DEFAULT_JAVASCRIPT_TYPE = "text/javascript";

  // TODO investigate spring 3 to even furhter simplify controller method
  @RequestMapping("/json-schema")
  public ModelAndView getJsonSchema(@RequestParam("wsdl") String wsdl, @RequestParam("operation") String operation, HttpServletResponse httpServletResponse) throws Exception {
    httpServletResponse.setContentType(DEFAULT_JAVASCRIPT_TYPE);

    PrintWriter out = httpServletResponse.getWriter();
    JsonSchemaConverter converter = new DefaultJsonSchemaConverter(wsdl, operation, DefaultJsonSchemaConverter.MessageType.INPUT_MESSAGE);
    out.write(converter.getJsonSchema());
    return null;
  }
}
