package de.rwth.i2.attestor.types;

import de.rwth.i2.attestor.util.SingleElementUtil;

import java.util.Map;

/**
 * A simple implementation of types.
 * For every name of types exactly one object is stored.
 * These are accessed through {@link GeneralType#getType(String)}.
 *
 * @author Christoph
 */
public class GeneralType implements Type {

    /**
     * The type object corresponding to the type 'null'.
     */
	private static final GeneralType nullType;

    /**
     * Stores the unique type object for every name.
     */
	private static final Map<String, GeneralType> existingTypes;
	
	static{
		nullType = new GeneralType( "NULL" );
		existingTypes = SingleElementUtil.createMap( "NULL", nullType );
	}

    /**
     * Provides the type object with the requested name.
     * If no type object with the requested name exists, a new object will be created.
     *
     * @param name The name of the requested type.
     * @return The type with the requested name.
     */
	public static synchronized GeneralType getType(String name ){
		if( !existingTypes.containsKey( name ) ){
			
			GeneralType res = new GeneralType( name );
			existingTypes.put( name, res );
		}
		
		return existingTypes.get( name );
	}

    /**
     * The name of the type.
     */
    private String name;

    /**
     * @param name The name of the type to be created.
     */
	private GeneralType(String name) {
		
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
	
	
}
