package soapproxy.application.mapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public abstract class AbstractMappingGenerator implements MappingGenerator {
  protected String wsdlUri;
  protected String operationName;

  public AbstractMappingGenerator(String wsdlUri, String operationName) {
    this.wsdlUri = wsdlUri;
    this.operationName = operationName;
  }

  public abstract String getMapping() throws Exception;

  protected String readWsdl() throws IOException {
    // read wsdl document
    URL url = new URL(wsdlUri);
    InputStream is = url.openStream();
    BufferedReader buff = new BufferedReader(new InputStreamReader(is));
    String nextLine;
    StringBuilder sb = new StringBuilder();
    while (true){
      nextLine = buff.readLine();
      if (nextLine !=null){
        sb.append(nextLine);
      }
        else{
        break;
      }
    }
    return sb.toString();
  }
}
