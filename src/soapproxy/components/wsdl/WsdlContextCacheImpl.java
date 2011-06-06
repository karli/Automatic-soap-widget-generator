package soapproxy.components.wsdl;

import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class WsdlContextCacheImpl implements WsdlContextCache {

  Map<String, WsdlContext> wsdlContextStore = new HashMap<String, WsdlContext>();

  @Override
  public WsdlContext getContextForWsdlDocument(String wsdlDocumentUrl) {
    if (!wsdlContextStore.containsKey(wsdlDocumentUrl)) {
      WsdlContext newContext = new WsdlContext(wsdlDocumentUrl);
      wsdlContextStore.put(wsdlDocumentUrl, newContext);
    }
    return wsdlContextStore.get(wsdlDocumentUrl);
  }
}
