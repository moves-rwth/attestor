package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleProgramState;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.JimpleUtil;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import de.rwth.i2.attestor.util.SingleElementUtil;

import java.util.Set;

/**
 * Skip models Statements which we do not translate and who have a single
 * successor
 * 
 * @author Hannah Arndt
 *
 */
public class Skip extends Statement {

	/**
	 * the program counter of the successor state
	 */
    private final int nextPC;

	public Skip( int nextPC  ){
		this.nextPC = nextPC;
	}

	@Override
	public boolean needsMaterialization( ProgramState executable ){

		return false;
	}


	public String toString(){
		return "Skip;";
	}

	@Override
	public Set<ProgramState> computeSuccessors(ProgramState programState) {
		
		JimpleProgramState result = JimpleUtil.shallowCopyExecutable( (JimpleProgramState) programState);
		result.setProgramCounter(nextPC);
		return SingleElementUtil.createSet( result );
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
