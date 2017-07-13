package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values;

import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleExecutable;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;

/**
 * represents expressions of form new List() or new List( args ),
 * but only generates the new element without calling the constructor.
 * This is separated by jimple.
 * 
 * @author Hannah Arndt
 *
 */
public class NewExpr implements Value {

	private final Type type;

	public NewExpr( Type type ){
		this.type = type;
	}

	/**
	 * inserts a new element of the expected type into the executable.
	 * @return the newly inserted element
	 */
	@Override
	public ConcreteValue evaluateOn( JimpleExecutable executable ) throws NotSufficientlyMaterializedException{

		return executable.insertNewElement( type );
	}

	@Override
	public Type getType(){
		return this.type;
	}

	@Override
	public boolean needsMaterialization( JimpleExecutable executable ){
		
		return false;
	}

	/**
	 * @return "new type()"
	 */
	public String toString(){
		return "new " + type + "()";
	}

	@Override
	public ViolationPoints getPotentialViolationPoints() {
		
		return ViolationPoints.getEmptyViolationPoints();
	}

}
