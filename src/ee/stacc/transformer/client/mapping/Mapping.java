package ee.stacc.transformer.client.mapping;

/**
 * A mapping element for representing data values.
 * 
 * @author Rainer Villido
 *
 */
public abstract class Mapping {
	
	//Path is optional when the data doesn't have any structure
	private String path;
	
	/**
	 * if the mapping represents an atomic data value.
	 * @return	mapping element of an atomic data value, null otherwise
	 */
	public abstract MappingElement isMappingElement();
	
	/**
	 * If the mapping element represents a repeating mapping group 
	 * @return	repeating mappings group element representing the data value, null otherwise.
	 */
	public abstract RepeatingMappingsGroup isRepeatingMappingsGroup();
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public String toString() {
		return path;
	}
}
