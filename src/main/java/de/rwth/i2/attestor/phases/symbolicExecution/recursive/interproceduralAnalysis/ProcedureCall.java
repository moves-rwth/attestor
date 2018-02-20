package de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis;

import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;

public interface ProcedureCall {

	/**
	 * generates the stateSpace,
	 * registers it in the InterprocedureAnalysis as result of this call
	 * and adds the generated contracts to the method
	 * @return the generated stateSpace
	 */
    StateSpace execute();
    Method getMethod();
    ProgramState getInput();
}
