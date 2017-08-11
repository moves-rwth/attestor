package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values;

import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import de.rwth.i2.attestor.types.Type;
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
	public ConcreteValue evaluateOn( JimpleProgramState programState ) throws NotSufficientlyMaterializedException{

		return programState.getConstant( "null" );
	}

	/**
	 * @return undefined type.
	 */
	public Type getType(){
		return Settings.getInstance().factory().getType( "undefined" );
	}

	@Override
	public boolean needsMaterialization( JimpleProgramState programState ){
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
