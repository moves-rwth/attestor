package de.rwth.i2.attestor.strategies;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.stateSpaceGeneration.AbortStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;

/**
 *
 * Implementation of an AbortStrategy that stops the state space generation whenever
 * either the size of the state space or the size of the largest state in the state space exceeds
 * a customized threshold.
 *
 * @author Christoph
 */
public class StateSpaceBoundedAbortStrategy implements AbortStrategy {

    /**
     * The logger of this class.
     */
	private static final Logger logger = LogManager.getLogger( "BoundedAbortStrategy" );

    /**
     * The last time this strategy has been called.
     */
	private static long timeStamp = 0;

    /**
     * The value representing an infinite threshold.
     */
	private static final int NO_MAXIMUM = -1;

    /**
     * The threshold for the maximal size of the state space.
     */
	private final int maxStateSpaceSize;

    /**
     * The threshold for the maximal size of states in the state space.
     */
	private final int maxStateSize;

    /**
     * Initializes the thresholds for this strategy.
     * @param maxStateSpaceSize The threshold for the maximal size of the state space.
     * @param maxStateSize The threshold for the maximal size of states in the state space.
     */
	public StateSpaceBoundedAbortStrategy(int maxStateSpaceSize, int maxStateSize) {
		
		this.maxStateSpaceSize = maxStateSpaceSize;
		this.maxStateSize = maxStateSize;
	}
	
	@Override
	public boolean isAllowedToContinue(StateSpace stateSpace) {
		
		if (maxStateSpaceSize != NO_MAXIMUM && stateSpace.getMaximalStateSize() > maxStateSize) {
			
			logger.error("Exceeded " + maxStateSize + " nodes in a heap configuration of a single state.");
			
			return false;
		}
		
		if (maxStateSpaceSize != NO_MAXIMUM && stateSpace.getStates().size() > maxStateSpaceSize) {
			
			logger.error("Exceeded " + maxStateSpaceSize + " states.");
			
			return false;
		}
	
		
		return true;
	}

}
