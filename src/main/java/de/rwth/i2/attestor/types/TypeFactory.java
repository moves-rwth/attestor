package de.rwth.i2.attestor.types;

/**
 * 
 * Abstract singleton factory to create Type objects.
 * What kind of Type are actually created is determined
 * by subclassing.
 *
 * TODO move this into Settings.factory()
 * 
 * @author Christoph
 *
 */
public abstract class TypeFactory {

	/**
	 * The unique instance of this factory.
	 */
	private static TypeFactory instance = null;
	
	
	/**
	 * @return A reference to the unique TypeFactory object.
	 */
	public static TypeFactory getInstance() {

		if(instance == null) {
			
			new DefaultTypeFactory();
		}
		
		return instance;
	}
	
	protected TypeFactory() {
		
		instance = this;
	}
	
	/**
	 * @param name String representation of the requested type
	 * @return The Type corresponding to the provided name.
	 */
	public abstract Type getType(String name);
}
