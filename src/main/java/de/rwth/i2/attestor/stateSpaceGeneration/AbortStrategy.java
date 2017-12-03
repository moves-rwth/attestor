package de.rwth.i2.attestor.stateSpaceGeneration;

/**
 * A strategy that determines when to give up the state space generation.
 *
 * @author Christoph
 */
public interface AbortStrategy {

    /**
     * Checks whether further states may be generated.
     *
     * @param stateSpace The StateSpace that has been generated so far.
     */
    void checkAbort(StateSpace stateSpace) throws StateSpaceGenerationAbortedException;

}
