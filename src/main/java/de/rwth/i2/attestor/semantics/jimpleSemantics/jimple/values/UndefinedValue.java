package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values;

import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleExecutable;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.TypeFactory;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;

/**
 * Undefined Values are placeholders for Values our simulation does not know,
 * e.g. most numbers. They always evaluate to undefined concrete values
 * 
 * @author Hannah Arndt
 *
 */
public class UndefinedValue implements Value {

	public UndefinedValue(){
	}

	@Override
	public ConcreteValue evaluateOn( JimpleExecutable executable ) throws NotSufficientlyMaterializedException{
		return executable.getUndefined();
	}

	public Type getType(){
		return TypeFactory.getInstance().getType( "undefined" );
	}

	@Override
	public boolean needsMaterialization( JimpleExecutable executable ){
		return false;
	}


	public String toString(){
		return "undefined";
	}

	@Override
	public ViolationPoints getPotentialViolationPoints() {
		
		return ViolationPoints.getEmptyViolationPoints();
	}

}
