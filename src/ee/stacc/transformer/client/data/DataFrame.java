package ee.stacc.transformer.client.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.xml.client.Document;

import ee.stacc.transformer.client.SchemaRPCResponseHandler;
import ee.stacc.transformer.client.mapping.Mapping;
import ee.stacc.transformer.client.mapping.MappingsXmlParser;

/**
 * Represents a frame element in the mappings configuration. 
 * It contains the mappings and other information relating to 
 * the data frame and the related messages. 
 * 
 * @author Rainer Villido
 *
 */
public abstract class DataFrame {
	
	private String topic;	//Topic of the data frame.
	private String schemaURL;	//Schema url. Optional
	private boolean outputOnly = false;	//If the messages with the topic are not received by any widget.
	
	//Mappings are duplicated to the Map and Set for better efficiency
	private Map<String, Mapping> mappings = new HashMap<String, Mapping>();	//Key: Global reference
	private Set<Mapping> mappingsSet = new HashSet<Mapping>();	//Root level mappings
	private Map<String, String> constantValues = new HashMap<String, String>();	//Holding constant (default) values defined in mappings. Key: path
	
	public DataFrame() {
	}
	
	public DataFrame(String topic, String shemaURL) {
		this.topic = topic;
		this.schemaURL = shemaURL;
	}
	
	/**
	 * To add schema to the data frame
	 * @param schemaTxt	text representation of the schema
	 */
	public abstract void updateSchema(String schemaTxt);
	
	/**
	 * To generate new DataPackage instance based on the data type.
	 * @return	new instance of the DataPackage realization.
	 */
	public abstract DataPackage generateDataPackageInstance();
	
	/**
	 * To return the data type of the data frame.
	 * @return	data type.
	 */
	public abstract String getDataType();

  /**
	 * Fetch the schema according to the schema url.
	 */
	public void loadSchema() {
		//First check if schema url is given. If url is not given then don't do anything.
		if(schemaURL!=null && schemaURL.isEmpty() == false) {
			GWT.log("Fetching schema from "+schemaURL, null);
			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(schemaURL));
			SchemaRPCResponseHandler responseHandler = new SchemaRPCResponseHandler(this);
			
			try {
				builder.sendRequest(null, responseHandler);
			}
			catch (RequestException e) {
				GWT.log("Could not connect to server", e);        
			}
		}
		
	}
	
	public Mapping getMapping(String globalRef) {
		return mappings.get(globalRef);
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getSchemaURL() {
		return schemaURL;
	}

	//set the schema url.
	public void setSchemaURL(String schemaURL) {
		if(schemaURL.startsWith("http"))
			this.schemaURL = schemaURL;
		else
			this.schemaURL = GWT.getHostPageBaseURL()+schemaURL;
	}

	public Map<String, Mapping> getMappings() {
		return this.mappings;		
	}
	
	/**
	 * Load mappings to the mappings set.
	 */
	public void updateMappingsSet() {
		mappingsSet.addAll(mappings.values());
	}
	
	public String toString() {
		return topic;
	}

	public Set<Mapping> getMappingsSet() {
		return mappingsSet;
	}

	/**
	 * @return the outputOnly
	 */
	public boolean isOutputOnly() {
		return outputOnly;
	}

	/**
	 * @param outputOnly the outputOnly to set
	 */
	public void setOutputOnly(boolean outputOnly) {
		this.outputOnly = outputOnly;
	}

	/**
	 * @return the constantValues
	 */
	public Map<String, String> getConstantValues() {
		return constantValues;
	}

  public void addMapping(Mapping mapping, String globalRef) {
    this.mappings.put(globalRef, mapping);
    this.mappingsSet.add(mapping);
  }
}
