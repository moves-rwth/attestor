package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values;

import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.Types;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;

/**
 * represents the constant null
 * @author Hannah Arndt
 *
 */
public class NullConstant implements Value {

	/**
	 * gets the element of programState that represents null
	 */
	@Override
	public ConcreteValue evaluateOn( ProgramState programState ) throws NotSufficientlyMaterializedException{

		return programState.getConstant(Constants.NULL);
	}

	/**
	 * @return undefined type.
	 */
	public Type getType(){
		return Types.UNDEFINED;
	}

	@Override
	public boolean needsMaterialization( ProgramState programState ){
		return false;
	}


	public String toString(){
		return Constants.NULL;
	}

	@Override
	public ViolationPoints getPotentialViolationPoints() {
		
		return ViolationPoints.getEmptyViolationPoints();
	}

}
