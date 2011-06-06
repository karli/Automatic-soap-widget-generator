package soapproxy.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import soapproxy.components.schema.DefaultJsonSchemaGenerator;
import soapproxy.components.schema.JsonSchemaGenerator;
import soapproxy.components.wsdl.WsdlContextCache;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Controller
public class JsonSchemaController {
  private static final String DEFAULT_JAVASCRIPT_TYPE = "text/javascript";

  @Autowired
  private WsdlContextCache wsdlContextCache;

  // TODO investigate spring 3 to even furhter simplify controller method
  @RequestMapping("/json-schema")
  public ModelAndView getJsonSchema(@RequestParam("wsdl") String wsdl, @RequestParam("operation") String operation, HttpServletResponse httpServletResponse) throws Exception {
    httpServletResponse.setContentType(DEFAULT_JAVASCRIPT_TYPE);

    PrintWriter out = httpServletResponse.getWriter();
    JsonSchemaGenerator jsonSchemaGenerator = new DefaultJsonSchemaGenerator(wsdl, operation, DefaultJsonSchemaGenerator.MessageType.INPUT_MESSAGE, wsdlContextCache);
    out.write(jsonSchemaGenerator.getJsonSchema());
    return null;
  }
}
