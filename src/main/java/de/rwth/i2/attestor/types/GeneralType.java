package de.rwth.i2.attestor.types;

import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.util.SingleElementUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A simple implementation of types.
 * For every name of types exactly one object is stored.
 * These are accessed through {@link GeneralType#getType(String)}.
 *
 * @author Christoph
 */
public final class GeneralType implements Type {

	public static final class GeneralTypeFactory {

		private final Map<String, Type> knownTypes = new HashMap<>();

		public GeneralTypeFactory() {
			knownTypes.put(TypeNames.NULL, Types.NULL);
			knownTypes.put(TypeNames.UNDEFINED, Types.UNDEFINED);
			knownTypes.put(TypeNames.INT, Types.INT);
		}

		public Type get(String name) {

			Type result = knownTypes.get(name);
			if(result == null) {
				result = new GeneralType(name);
				knownTypes.put(name, result);
			}
			return result;
		}
	}

	private final Map<String,String> selectorLabelNames = new HashMap<>();

    /**
     * Provides the type object with the requested name.
     * If no type object with the requested name exists, a new object will be created.
     *
     * @param name The name of the requested type.
     * @return The type with the requested name.
     */
	public static synchronized GeneralType getType(String name ){
		return null;
	}

    /**
     * The name of the type.
     */
    private String name;

    /**
     * @param name The name of the type to be created.
     */
	protected  GeneralType(String name) {
		
		this.name = name;
	}

	/*
	 * Shows only the last component of the type name for readability.
	 * e.g. if the type is "de.rwth.i2.attestor.package.subpackage.List"
	 * then the result will be "List"
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String[] components = this.name.split( "\\." );
		
		if(components.length > 0) {
			return components[ components.length -1 ];	
		} else {
			return this.name;
			//return "";
		}
		
	}

    /**
     * Checks whether two types are equal.
     * @param other Another type.
     * @return True if and only if two objects refer to the same type.
     */
	public boolean typeEquals( Object other ){

		return this == other;
	}

	@Override
	public boolean hasSelectorLabel(String name) {
		return selectorLabelNames.containsKey(name);
	}

	@Override
	public void addSelectorLabel(String name, String defaultValue) {

		if(Types.isConstantType(this)) {
			throw new IllegalStateException("Cannot assign selector labels to node of constant type.");
		}
		selectorLabelNames.put(name, defaultValue);
	}

	@Override
	public Map<String, String> getSelectorLabels() {
		return selectorLabelNames;
	}

	@Override
	public boolean isPrimitiveType(String name) {
		return !selectorLabelNames.get(name).equals(Constants.NULL);
	}
}
