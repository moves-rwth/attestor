package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values;

import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleExecutable;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.TypeFactory;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;

/**
 * represents the constant null
 * @author Hannah Arndt
 *
 */
public class NullConstant implements Value {

	/**
	 * gets the element of executable that represents null
	 */
	@Override
	public ConcreteValue evaluateOn( JimpleExecutable executable ) throws NotSufficientlyMaterializedException{

		return executable.getConstant( "null" );
	}

	/**
	 * @return undefined type.
	 */
	public Type getType(){
		return TypeFactory.getInstance().getType( "undefined" );
	}

	@Override
	public boolean needsMaterialization( JimpleExecutable executable ){
		return false;
	}


	public String toString(){
		return "null";
	}

	@Override
	public ViolationPoints getPotentialViolationPoints() {
		
		return ViolationPoints.getEmptyViolationPoints();
	}

}
