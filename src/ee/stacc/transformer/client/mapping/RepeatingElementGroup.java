package ee.stacc.transformer.client.mapping;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ee.stacc.transformer.client.data.DataFrame;

/**
 * Mapping element representing repeating element groups.
 * @author Rainer Villido
 *
 */
public class RepeatingElementGroup extends Mapping {
	
	RepeatingElementGroup parentMappingsGroup;
	DataFrame dataFrame;
	
	//Mappings that the repeating element group contains.
	private Map<String, Mapping> mappings = new HashMap<String, Mapping>();	//Key: global reference
	
	//Mappings contained only in this group.
	private Set<Mapping> mappingsSet = new HashSet<Mapping>();
	
	public RepeatingElementGroup(String path, DataFrame dataFrame, RepeatingElementGroup parentMappingsGroup) {
		setPath(path);
		this.dataFrame = dataFrame;
		this.parentMappingsGroup = parentMappingsGroup;
	}

  /**
	 * To add a mapping to the repeating mappings group and 
	 * to all of the ancestors of the repeating mappings group.
	 * 
	 * @param mapping	mapping to add to the hierarchy.
	 * @param globalRef	global reference of the mapping.
	 */
	public void propagateMapping(Mapping mapping, String globalRef) {
		mappings.put(globalRef, mapping);
		mappingsSet.add(mapping);
		
		if(parentMappingsGroup != null)
			parentMappingsGroup.propagateMapping(this, globalRef);
		else
			dataFrame.getMappings().put(globalRef, this);
	}
	
	public Mapping getMapping(String globalRef) {
		return mappings.get(globalRef);
	}

	@Override
	public MappingElement isMappingElement() {
		return null;
	}

	@Override
	public RepeatingElementGroup isRepeatingMappingsGroup() {
		return this;
	}

	public Map<String, Mapping> getMappings() {
		return mappings;
	}

	/**
	 * @return the mappingsSet
	 */
	public Set<Mapping> getMappingsSet() {
		return mappingsSet;
	}
}
