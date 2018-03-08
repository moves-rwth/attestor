package de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis;

import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;

/**
 * partialStateSpaces wrap an (unfinished) stateSpace
 * together with a state within this stateSpace which is
 * expected to be continued during fixpoint iteration.
 * (This state corresponds to a method invoke and will be
 * continued whenever more contracts for this procedureCall
 * are obtained)
 * @author Hannah
 *
 */
public interface PartialStateSpace {

	/**
	 * restarts the stateSpace generation with the
	 * represented state as the first unexplored state.
	 * Should be called whenever new contracts for the
	 * corresponding procedureCall have been found.
	 * @param call the procedureCall generating the
	 * given stateSpace (i.e. the code analysed by
	 * this stateSpace).
	 */
    void continueExecution(ProcedureCall call);
    /**
     * (note that the stateSpace can still be changing,
     * i.e the stateSpace before and after calling continueExecution
     * is not identical)
     * @return the (unfinished) stateSpace represented
     * by this partialStateSpace 
     */
    StateSpace unfinishedStateSpace();
}
