package ee.stacc.transformer.client.mapping;

import java.util.ArrayList;
import java.util.List;

/**
 * A mapping element representing an atomic data value.
 * @author Rainer Villido
 *
 */
public class MappingElement extends Mapping{
	
	private List<String> globalReference = new ArrayList<String>();	//Global reference of the data value
	private boolean hasDefaultValue;	//if any default values are defined.
	
	public MappingElement(String globalReference) {
		this.globalReference.add(globalReference);
	}
	
	public MappingElement(String path, String globalReference) {
		this.globalReference.add(globalReference);
		setPath(path);
	}

  public MappingElement(String path, List<String> globalReferences) {
    setGlobalReference(globalReferences);
    setPath(path);
  }

  // return the first global reference item
	public String getFirstGlobalReference() {
    if (globalReference.size() > 0) {
      return globalReference.get(0);
    }
		return null;
	}

  public List<String> getGlobalReference() {
    return globalReference;
  }

  public void setGlobalReference(List<String> globalReference) {
    this.globalReference = globalReference;
  }

  @Override
	public MappingElement isMappingElement() {
		return this;
	}

	@Override
	public RepeatingElementGroup isRepeatingMappingsGroup() {
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
