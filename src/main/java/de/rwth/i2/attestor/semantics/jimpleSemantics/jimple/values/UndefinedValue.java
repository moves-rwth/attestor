package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values;

import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import de.rwth.i2.attestor.types.Type;
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
	public ConcreteValue evaluateOn( JimpleProgramState programState ) throws NotSufficientlyMaterializedException{

		return programState.getUndefined();
	}

	public Type getType(){
		return Settings.getInstance().factory().getType( "undefined" );
	}

	@Override
	public boolean needsMaterialization( JimpleProgramState programState ){

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
