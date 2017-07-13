package de.rwth.i2.attestor.tasks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.ConcreteValue;
import de.rwth.i2.attestor.types.Type;

/**
 * Implementation of values of variables as nodes in a heap configuration.
 *
 * @author Christoph, Hannah Arndt
 */
public class GeneralConcreteValue implements ConcreteValue {

    /**
     * The logger of this class.
     */
	private static final Logger logger = LogManager.getLogger( "GeneralConcreteValue" );

    /**
     * The internal value used for undefined values.
     */
	public static final int UNDEFINED = -1;

    /**
     * The node corresponding to this value.
     */
	private final int node;

    /**
     * The type of this value.
     */
	private final Type type;

    /**
     * @return The value corresponding to "undefined value".
     */
	public static GeneralConcreteValue getUndefined() {
		
		return new GeneralConcreteValue();
	}

	private GeneralConcreteValue() {
		
		this.node = UNDEFINED;
		type = GeneralType.getType("undefined");
	}

    /**
     * Initializes a new value
     * @param type Type of the underlying value.
     * @param node The ID of a node corresponding to the underlying value.
     */
	public GeneralConcreteValue(Type type, int node) {
		
			this.node = node;
			this.type = type;
	}

    /**
     * @return The node in a heap configuration underlying this value.
     */
	public int getNode() {

		if(isUndefined()) {

			logger.warn("Attempting to get node of undefined value.");
		}

		return node;
	}

    /**
     * @return A string representation for debugging purposes of this value.
     */
	public String toString() {

		if(node == UNDEFINED) {
			return "undefined";
		}

		return String.valueOf(node);
	}


	@Override
	public Type type() {
		
		return type;
	}

	@Override
	public boolean equals(ConcreteValue other) {
		
		if(other instanceof GeneralConcreteValue) {
			
			GeneralConcreteValue v = (GeneralConcreteValue) other;
			
			if(node != UNDEFINED && v.node != UNDEFINED) {
				
				return node == v.node;
			} else {
				
				return node == v.node;
			}
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		
		return node;
	}

	@Override
	public boolean isUndefined() {
		
		return node == UNDEFINED;
	}

}
