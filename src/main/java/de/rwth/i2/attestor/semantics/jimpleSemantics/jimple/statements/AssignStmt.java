package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;


import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleProgramState;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.JimpleUtil;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.VariablesUtil;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.*;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;
import de.rwth.i2.attestor.util.SingleElementUtil;

/**
 * AssignStmts model assignments of locals or fields to values e.g. x.y = z
 * 
 * @author hannah
 *
 */
public class AssignStmt extends Statement {

	private static final Logger logger = LogManager.getLogger( "AssignStmt" );
	/**
	 * the element to which something will be assigned (e.g. variable or field)
	 */
	private final SettableValue lhs;
	/**
	 * The expression that will be assigned
	 */
	private final Value rhs;
	/**
	 * the program counter of the successor state
	 */
	private final int nextPC;
	
	private final ViolationPoints potentialViolationPoints;
	
	private final Set<String> liveVariableNames;

	private final boolean removeDeadVariables;

	public AssignStmt( SettableValue lhs , Value rhs , int nextPC,
					   Set<String> liveVariableNames, boolean removeDeadVariables ){
		super();
		this.rhs = rhs;
		this.lhs = lhs;
		this.nextPC = nextPC;
		this.liveVariableNames = liveVariableNames;
		this.removeDeadVariables = removeDeadVariables;
		
		potentialViolationPoints = new ViolationPoints();
		potentialViolationPoints.addAll(lhs.getPotentialViolationPoints());
		potentialViolationPoints.addAll(rhs.getPotentialViolationPoints());
		
		
	}

	/**
	 * evaluates the rhs and assigns it to the left hand side. In case the rhs
	 * evaluates to undefined, the variable will be removed from the heap (It
	 * will not point to its old value). <br>
	 * If the types of the lhs and the rhs do not match, there will be a
	 * warning, but the assignment will still be realized.<br>
	 * 
	 * If the variable in rhs is not live in this statement, it will be removed from the heap
	 * to enable abstraction at this point.
	 * 
	 * @throws NotSufficientlyMaterializedException if rhs or lhs cannot be evaluated on the given heap
	 */
	@Override
	public Set<ProgramState> computeSuccessors( ProgramState programState ) throws NotSufficientlyMaterializedException {
		
		JimpleProgramState jimpleProgramState = (JimpleProgramState) programState;
		jimpleProgramState = JimpleUtil.deepCopy(jimpleProgramState);
		
		ConcreteValue concreteRHS;
		
		try {
			concreteRHS = rhs.evaluateOn( jimpleProgramState );
		} catch (NullPointerDereferenceException e) {
			logger.error( e.getErrorMessage(this) );
			concreteRHS = jimpleProgramState.getUndefined();
		}

		/*
		if( concreteRHS.isUndefined() ){
				logger.debug( "The value of the right hand side is undefined. Ignoring Assign." );
		}else{
			if( !( lhs.getType().equals( concreteRHS.type() ) ) ){
				String msg = "The type of the resulting ConcreteValue for rhs does not match ";
				msg += " with the type of the lhs";
				msg += "\n expected: " + lhs.getType() + " got: " + concreteRHS.type();
				logger.debug( msg );
			}
		}
		*/
		
		try {
		    lhs.evaluateOn(jimpleProgramState); // enforce materialization if necessary
			lhs.setValue( jimpleProgramState, concreteRHS );
		} catch (NullPointerDereferenceException e) {
			logger.error(e.getErrorMessage(this));
		}

		if(removeDeadVariables) {
			VariablesUtil.removeDeadVariables(rhs.toString(), jimpleProgramState, liveVariableNames);
			VariablesUtil.removeDeadVariables(lhs.toString(), jimpleProgramState, liveVariableNames);
		}

		JimpleProgramState result = JimpleUtil.deepCopy(jimpleProgramState);
		result.setProgramCounter(nextPC);
		
		return SingleElementUtil.createSet( result );
	}

	@Override
	public boolean needsMaterialization( ProgramState programState ){
		
		JimpleProgramState jimpleProgramState = (JimpleProgramState) programState;
		
		return rhs.needsMaterialization( jimpleProgramState ) || lhs.needsMaterialization( jimpleProgramState );
	}


	public String toString(){
		return lhs.toString() + " = " + rhs.toString() + ";";
	}

	@Override
	public boolean hasUniqueSuccessor() {
		
		return true;
	}
	
	@Override
	public ViolationPoints getPotentialViolationPoints() {
		
		return potentialViolationPoints;
	}

	@Override
	public Set<Integer> getSuccessorPCs() {
		
		return SingleElementUtil.createSet(nextPC);
	}
	
}
