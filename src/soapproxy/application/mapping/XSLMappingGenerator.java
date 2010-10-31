package soapproxy.application.mapping;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

public class XSLMappingGenerator extends AbstractMappingGenerator {
  public XSLMappingGenerator(String wsdlUri, String operationName) {
    super(wsdlUri, operationName);
  }

  @Override
  public String getMapping() throws IOException {
    String wsdl = readWsdl();
    String mapping = null;
    try {
      mapping = generateMapping(wsdl);
    }
    catch (TransformerException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }

    return mapping;
  }

  private String generateMapping(String wsdl) throws FileNotFoundException, TransformerException {
    String path = this.getClass().getResource(".").getPath().replace("%20", " ");
		File parent = new File(path).getParentFile();
		File xsltFile = new File(parent, "xsl/transform.xsl");
		// read the source xml
		Source xmlSource = new StreamSource(new StringReader(wsdl));
		// read the transformation file
		Source xsltSource = new StreamSource(new FileInputStream(xsltFile));
		// set system id
		xsltSource.setSystemId(parent.getAbsolutePath()+"/xsl/");
		// result will be saved to...
		ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
		Result result = new StreamResult(resultStream);

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer(xsltSource);

		// do the transformation
		transformer.transform(xmlSource, result);
    
    return resultStream.toString();
  }

}
