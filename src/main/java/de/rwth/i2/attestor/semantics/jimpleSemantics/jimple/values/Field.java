package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleExecutable;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.DebugMode;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;

/**
 * fields model selectors of objects, e.g. curr.next or x.left.right
 * or in general expr.field, where expr specifies the origin of the selector
 * and field its name. <br>
 * In jimple expr must be a local variable, i.e. x.left.right is not possible.
 * It is however supported by this class.
 * 
 * @author Hannah Arndt
 *
 */
public class Field implements SettableValue {

	private static final Logger logger = LogManager.getLogger( "Field" );

	/**
	 * the element to which the field belongs
	 */
    private final Value originValue;
	/**
	 * the name of the field / selector
	 */
    private final String fieldName;
	/**
	 * the expected type of this expression
	 */
	private final Type type;
	
	private final ViolationPoints potentialViolationPoints;

	public Field( Type type, Value originValue, String fieldName ){

		this.type = type;
		this.originValue = originValue;
		this.fieldName = fieldName;
		
		potentialViolationPoints = new ViolationPoints(originValue.toString(), fieldName);
	}

	@Override
	public Type getType(){
		return this.type;
	}

	/**
	 * evaluates the expression defining the origin and gets the element referenced by 
	 * the field starting at this origin element. <br>
	 * Logs a warning if the origin is undefined or the actual type does not match the
	 * expected one.
	 * @return undefined if the origin evaluates to undefined or if the corresponding
	 * selector is missing at the origin element. The correct element otherwise.
	 */
	@Override
	public ConcreteValue evaluateOn( JimpleExecutable executable ) throws NotSufficientlyMaterializedException, NullPointerDereferenceException {

		ConcreteValue concreteOrigin = originValue.evaluateOn( executable );
		
		if( concreteOrigin.isUndefined() ){
			
			if( DebugMode.ENABLED ){
				
				logger.warn( "the origin evaluated to undefined. returning undefined." );
			}
			return executable.getUndefined();
		} else {
			
			
			
			if(executable.getConstant("null").equals(concreteOrigin)) {
				throw new NullPointerDereferenceException(originValue);
			}
			
			ConcreteValue res = executable.getSelectorTarget( concreteOrigin, fieldName );
			if( DebugMode.ENABLED && !res.type().equals( this.type ) ){
				
				String msg = "The type of the resulting ConcreteValue does not match.";
				msg += "\n expected: " + this.type + " got: " + res.type();
				logger.warn( msg );
			}

			return res;
		}
	}

	/**
	 * sets the value of the field in executable to concreteTarget. <br>
	 * Logs a warning if the origin evaluates to undefined. In this case the 
	 * heap is not changed by the expression.<br>
	 * Also logs a warning if the type of concreteTarget does not match the 
	 * expected type for the field.
	 * @param executable the heap on which the assignment is performed
	 * @param concreteTarget the value that is assigned to the heap
	 * @throws NullPointerDereferenceException if the evaluation of the originValue 
	 * results in a null pointer dereferenciation
	 */
	@Override
	public void setValue( JimpleExecutable executable, ConcreteValue concreteTarget )
			throws NotSufficientlyMaterializedException, NullPointerDereferenceException{
		
		if( DebugMode.ENABLED && !concreteTarget.type().equals( this.type ) ){
			String msg = "The type of the resulting ConcreteValue does not match.";
			msg += "\n expected: " + this.type + " got: " + concreteTarget.type();
			logger.warn( msg );
		}

		ConcreteValue concreteOrigin = originValue.evaluateOn( executable );
		if( concreteOrigin.isUndefined() ){
			if( DebugMode.ENABLED ){
				logger.warn( "origin evaluated to undefined. field is not reassigned" );
			}
		}else{
			executable.setSelector( concreteOrigin, fieldName, concreteTarget );
		}
	}

	@Override
	public boolean needsMaterialization( JimpleExecutable executable ){
		return true;
	}


	/**
	 * "origin.field"
	 */
	public String toString(){
		return originValue + "." + fieldName;
	}

	@Override
	public ViolationPoints getPotentialViolationPoints() {
		
		return potentialViolationPoints;
	}
}
