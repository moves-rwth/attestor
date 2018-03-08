package de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis;

import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;

/**
 * A procedure call wraps a method and an initial state together.
 * It is used both to reference this particular pair (e.g. to store 
 * dependencies) and to get the corresponding StateSpace from executing
 * the method on the initial state.
 * @author Hannah
 *
 */
public interface ProcedureCall {

	/**
	 * generates the stateSpace,
	 * registers it in the InterprocedureAnalysis as result of this call
	 * and adds the generated contracts to the method.
	 * Note that this stateSpace is not necessarily complete. The overall
	 * stateSpace for this call can change during the fixpoint iteration.
	 * @return the generated stateSpace
	 */
    StateSpace execute();
    Method getMethod();
    ProgramState getInput();
}
