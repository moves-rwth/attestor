package de.rwth.i2.attestor.stateSpaceGeneration;

import de.rwth.i2.attestor.stateSpaceGeneration.stateSpace.StateSpace;

/**
 *
 * A strategy that determines when to give up the state space generation.
 *
 * @author Christoph
 */
public interface AbortStrategy {

    /**
     * Checks whether further states may be generated.
     * @param stateSpace The OldStateSpace that has been generated so far.
     * @return true if and only if further states may be generated.
     */
	boolean isAllowedToContinue(StateSpace stateSpace);

}
