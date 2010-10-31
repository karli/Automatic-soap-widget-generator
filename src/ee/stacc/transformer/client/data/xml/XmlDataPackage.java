package ee.stacc.transformer.client.data.xml;

import com.google.gwt.core.client.GWT;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;

import ee.stacc.transformer.client.data.DataPackage;

/**
 * Data package for generating XML messages. 
 * @author Rainer Villido
 *
 */
public class XmlDataPackage extends DataPackage {
	
	private XmlDataFrame dataFrame;
	private Node publishableDocument;

	public XmlDataPackage(XmlDataFrame dataFrame) {
		super(dataFrame);
		this.dataFrame = dataFrame;
	}

	/* (non-Javadoc)
	 * @see ee.stacc.translation.transformer.client.DataPackage#getObjectToPublish()
	 */
	@Override
	public Object getObjectToPublish() {
		GWT.log("Starting to parse XML", null);

		Document schema = dataFrame.getXmlSchema();
		
		XMLGenerator generator = new XMLGenerator(schema);
		Node xmlDoc = generator.generateXml(getDataValues());
		publishableDocument = xmlDoc;
		
		return xmlDoc.toString();
	}

	/* (non-Javadoc)
	 * @see ee.stacc.translation.transformer.client.DataPackage#toString()
	 */
	@Override
	public String toString() {
		if(isFinished() && publishableDocument != null)
			return publishableDocument.toString();
		else
			return getObjectToPublish().toString();
	}
	
	

}
