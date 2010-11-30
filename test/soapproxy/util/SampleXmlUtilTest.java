package soapproxy.util;

import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlContext;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlUtils;
import org.junit.Test;

import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;

public class SampleXmlUtilTest {

  @Test
  public void convertsAttributesToElementsAndAddsGlobalReferences() throws Exception {
    String operationName = "parse";
    String path = this.getClass().getResource(".").getPath().replace("%20", " ");
    String wsdlUri = path + "annotated_ner.wsdl";
    WsdlContext wsdlContext = new WsdlContext(wsdlUri);
    Definition definition = wsdlContext.getDefinition();
    BindingOperation bindingOperation = WsdlUtils.findBindingOperation(definition, operationName);
    SoapMessageBuilder smb = new SoapMessageBuilder(wsdlContext);
    String soapMessage = smb.buildSoapMessageFromOutput(bindingOperation, true);

  }
}
