package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;


import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.VariablesUtil;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.ConcreteValue;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.NullPointerDereferenceException;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.SettableValue;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Value;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.SemanticsObserver;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;
import de.rwth.i2.attestor.util.SingleElementUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

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

	public AssignStmt( SettableValue lhs , Value rhs , int nextPC, Set<String> liveVariableNames){
		super();
		this.rhs = rhs;
		this.lhs = lhs;
		this.nextPC = nextPC;
		this.liveVariableNames = liveVariableNames;

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
	public Set<ProgramState> computeSuccessors(ProgramState programState, SemanticsObserver options)
			throws NotSufficientlyMaterializedException {

		options.update(this, programState);

		programState = programState.clone();
		ConcreteValue concreteRHS;
		
		try {
			concreteRHS = rhs.evaluateOn( programState );
		} catch (NullPointerDereferenceException e) {
			logger.error( e.getErrorMessage(this) );
			concreteRHS = programState.getUndefined();
		}

		try {
		    lhs.evaluateOn(programState); // enforce materialization if necessary
			lhs.setValue(programState, concreteRHS );
		} catch (NullPointerDereferenceException e) {
			logger.error(e.getErrorMessage(this));
		}

		if(options.isDeadVariableEliminationEnabled()) {
			VariablesUtil.removeDeadVariables(rhs.toString(), programState, liveVariableNames);
			VariablesUtil.removeDeadVariables(lhs.toString(), programState, liveVariableNames);
		}

		ProgramState result = programState.clone();
		result.setProgramCounter(nextPC);
		
		return SingleElementUtil.createSet( result );
	}

	@Override
	public boolean needsMaterialization( ProgramState programState ){
		
		return rhs.needsMaterialization( programState ) || lhs.needsMaterialization( programState );
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
