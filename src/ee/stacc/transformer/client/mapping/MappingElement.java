package ee.stacc.transformer.client.mapping;

/**
 * A mapping element representing an atomic data value.
 * @author Rainer Villido
 *
 */
public class MappingElement extends Mapping{
	
	private String globalReference;	//Global reference of the data value
	private boolean hasDefaultValue;	//if any default values are defined.
	
	public MappingElement(String globalReference) {
		this.globalReference = globalReference;
	}
	
	public MappingElement(String path, String globalReference) {
		this.globalReference = globalReference;
		setPath(path);
	}

	public String getGlobalReference() {
		return globalReference;
	}

	@Override
	public MappingElement isMappingElement() {
		return this;
	}

	@Override
	public RepeatingMappingsGroup isRepeatingMappingsGroup() {
		return null;
	}

	/**
	 * @return the defaultValue
	 */
	public boolean hasDefaultValue() {
		return hasDefaultValue;
	}

	/**
	 * @param hasDefaultValue the defaultValue to set
	 */
	public void setHasDefaultValue(boolean hasDefaultValue) {
		this.hasDefaultValue = hasDefaultValue;
	}
}
