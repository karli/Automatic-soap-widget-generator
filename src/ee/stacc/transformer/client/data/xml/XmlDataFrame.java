package ee.stacc.transformer.client.data.xml;

import com.google.gwt.core.client.GWT;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;

import ee.stacc.transformer.client.data.DataFrame;
import ee.stacc.transformer.client.data.DataPackage;
import ee.stacc.transformer.client.data.InstanceFactory;

/**
 * A data frame representing XML messages.
 * @author Rainer Villido
 *
 */
public class XmlDataFrame extends DataFrame {
	
	private Document xmlSchema;

	@Override
	public void updateSchema(String schemaTxt) {
		Document xmlSchema = XMLParser.parse(schemaTxt);
		setXmlSchema(xmlSchema);
		GWT.log("Got schema: "+xmlSchema.toString(), null);
	}
	
	@Override
	public DataPackage generateDataPackageInstance() {
		return new XmlDataPackage(this);
	}
	
	@Override
	public String getDataType() {
		return InstanceFactory.XML_DATA_TYPE;
	}
	
	public Document getXmlSchema() {
		return xmlSchema;
	}

	public void setXmlSchema(Document xmlSchema) {
		this.xmlSchema = xmlSchema;
	}

}
