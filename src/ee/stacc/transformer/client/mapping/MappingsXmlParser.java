package ee.stacc.transformer.client.mapping;

import java.util.*;

import com.google.gwt.core.client.GWT;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

import ee.stacc.transformer.client.data.DataFrame;
import ee.stacc.transformer.client.data.InstanceFactory;

/**
 * @author Rainer Villido
 *
 */
public class MappingsXmlParser {
	
	//Element names in the mappings XML document 
	public static final String FRAME = "frame";
	
	/**
	 * Method for loading content from the mappings configuration file to the mappings data structure.
	 * 
	 * @param xml	xml document containing mappings.
	 * @return	map of data frames. (Key is topic).
	 */
	public static Map<String, DataFrame> loadDataFrames(Document xml) {
		GWT.log("Starting to parse XML", null);
		
		Map<String, DataFrame> dataFrames = new HashMap<String, DataFrame>();
		NodeList frames = xml.getElementsByTagName(FRAME);
		
		//Go through all the frame elements in mappings.
		for(int i = 0; i < frames.getLength();i++) {
			
			if(frames.item(i).getNodeType() == Node.TEXT_NODE)
				continue;	//If it's just spam then don't process the node
			
			Element frame = (Element)frames.item(i);
			
			//Load all the data from the frame element
      DataFrameLoader dataFrameLoader = new DataFrameLoader(frame);
      DataFrame dataFrame = dataFrameLoader.getDataFrame();
			dataFrames.put(dataFrame.getTopic(), dataFrame);
		}
		
		return dataFrames;
	}
}
