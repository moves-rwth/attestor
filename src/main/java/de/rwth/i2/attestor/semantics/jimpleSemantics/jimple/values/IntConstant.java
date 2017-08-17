package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values;

import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * IntConstants represent access to constants of type int
 * @author Hannah Arndt
 *
 */
public class IntConstant implements Value {
	private static final Logger logger = LogManager.getLogger( "IntConstant" );

	private final int intValue;
	private final Type type = Settings.getInstance().factory().getType( "int" );

	public IntConstant( int value ){
		this.intValue = value;
	}

	@Override
	public ConcreteValue evaluateOn( JimpleProgramState programState ) throws NotSufficientlyMaterializedException{

		ConcreteValue res = programState.getConstant( "" + intValue );
		if( res.type() != this.type ){
			String msg = "The type of the resulting ConcreteValue does not match.";
			msg += "\n expected: " + this.type + " got: " + res.type();
			logger.debug( msg );
		}
		return res;
	}



	@Override
	public Type getType(){
		return this.type;
	}

	public String toString(){
		return "" + intValue;
	}

	@Override
	public boolean needsMaterialization(JimpleProgramState programState) {

		return false;
	}

	@Override
	public ViolationPoints getPotentialViolationPoints() {
		
		return ViolationPoints.getEmptyViolationPoints();
	}
}
