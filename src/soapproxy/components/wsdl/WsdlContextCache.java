package soapproxy.components.wsdl;

import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlContext;

public interface WsdlContextCache {
  WsdlContext getContextForWsdlDocument(String wsdlDocumentUrl);
}
