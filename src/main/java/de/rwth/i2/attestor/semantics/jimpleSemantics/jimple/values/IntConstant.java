package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleExecutable;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.TypeFactory;
import de.rwth.i2.attestor.util.DebugMode;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;

/**
 * IntConstants represent access to constants of type int
 * @author Hannah Arndt
 *
 */
public class IntConstant implements Value {
	private static final Logger logger = LogManager.getLogger( "IntConstant" );

	private final int intValue;
	private final Type type = TypeFactory.getInstance().getType( "int" );

	public IntConstant( int value ){
		this.intValue = value;
	}

	/**
	 * returns the heap element corresponding to constant.
	 * Logs a warning if the element type is not as expected.
	 */
	@Override
	public ConcreteValue evaluateOn( JimpleExecutable executable ) throws NotSufficientlyMaterializedException{

		ConcreteValue res = executable.getConstant( "" + intValue );
		if( DebugMode.ENABLED && res.type() != this.type ){
			String msg = "The type of the resulting ConcreteValue does not match.";
			msg += "\n expected: " + this.type + " got: " + res.type();
			logger.warn( msg );
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
	public boolean needsMaterialization(JimpleExecutable executable) {

		return false;
	}

	@Override
	public ViolationPoints getPotentialViolationPoints() {
		
		return ViolationPoints.getEmptyViolationPoints();
	}
}
