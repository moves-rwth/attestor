package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values;

import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleProgramState;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	 * @return undefined if the origin evaluates to undefined or if the corresponding
	 * selector is missing at the origin element. The correct element otherwise.
	 */
	@Override
	public ConcreteValue evaluateOn( JimpleProgramState programState )
			throws NotSufficientlyMaterializedException, NullPointerDereferenceException {

		ConcreteValue concreteOrigin = originValue.evaluateOn( programState );
		
		if( concreteOrigin.isUndefined() ){
			
            logger.trace( "The origin evaluated to undefined. Returning undefined." );
			return programState.getUndefined();
		} else {
			
			if(programState.getConstant(Constants.NULL).equals(concreteOrigin)) {
				throw new NullPointerDereferenceException(originValue);
			}
			
			ConcreteValue res = programState.getSelectorTarget( concreteOrigin, fieldName );

			/*
			if( !res.type().equals( this.type ) ){
				
				String msg = "The type of the resulting ConcreteValue does not match.";
				msg += "\n expected: " + this.type + " got: " + res.type();
				logger.debug( msg );
			}
			*/

			return res;
		}
	}

	/**
	 * Sets the value of the field in programState to concreteTarget. <br>
	 * In this case the heap is not changed by the expression.<br>
	 * Also logs a warning if the type of concreteTarget does not match the 
	 * expected type for the field.
	 * @param programState the heap on which the assignment is performed
	 * @param concreteTarget the value that is assigned to the heap
	 * @throws NullPointerDereferenceException if the evaluation of the originValue 
	 * results in a null pointer dereference.
	 */
	@Override
	public void setValue(JimpleProgramState programState, ConcreteValue concreteTarget )
			throws NotSufficientlyMaterializedException, NullPointerDereferenceException{

		/*
		if( !concreteTarget.type().equals( this.type ) ){
			String msg = "The type of the resulting ConcreteValue does not match.";
			msg += "\n expected: " + this.type + " got: " + concreteTarget.type();
			logger.debug( msg );
		}
		*/

		ConcreteValue concreteOrigin = originValue.evaluateOn( programState );
		if( concreteOrigin.isUndefined() ){
            logger.warn( "Origin evaluated to undefined. Field is not reassigned" );
		}else{
			programState.setSelector( concreteOrigin, fieldName, concreteTarget );
		}
	}

	@Override
	public boolean needsMaterialization( JimpleProgramState programState ){
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
