package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import java.util.Set;

import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleExecutable;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.JimpleUtil;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import de.rwth.i2.attestor.util.SingleElementUtil;

/**
 * GotoStmt models the statement goto pc
 * 
 * @author Hannah Arndt
 *
 */
public class GotoStmt extends Statement {

	/**
	 * the program counter of the successor state
	 */
	private final int nextPC;

	public GotoStmt( int nextPC ){
		this.nextPC = nextPC;
	}

	@Override
	public boolean needsMaterialization( ProgramState heap ){
		return false;
	}


	public String toString(){
		return "goto " + nextPC + ";";
	}

	@Override
	public Set<ProgramState> computeSuccessors(ProgramState state) {
		
		return JimpleUtil.createSingletonAndUpdatePC( (JimpleExecutable) state , nextPC);		
	}

	@Override
	public boolean hasUniqueSuccessor() {

		return true;
	}
	
	@Override
	public ViolationPoints getPotentialViolationPoints() {
		
		return ViolationPoints.getEmptyViolationPoints();
	}
	
	@Override
	public Set<Integer> getSuccessorPCs() {
		
		return SingleElementUtil.createSet(nextPC);
	}

}
