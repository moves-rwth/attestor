package de.rwth.i2.attestor.main.settings;

import de.rwth.i2.attestor.automata.HeapAutomaton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Collects all options that customize the state space generation.
 *
 * @author Hannah Arndt, Christoph
 */
public class OptionSettings {

	/**
	 * The logger of this class.
	 */
	private static final Logger logger = LogManager.getLogger( "OptionSettings");

	/**
	 * The maximal number of states before state space generation is given up.
	 */
	private int maxStateSpaceSize = 3000;

	/**
	 * The maximal number of nodes in a single heap configuration before state space generation is given up.
	 */
	private int maxStateSize = 50;

	/**
	 * The minimal distance between variables in a heap configuration and embeddings used for abstraction.
	 * Increasing this number allows to use a less aggressive abstraction.
	 */
	private int abstractionDistance = 0;

	/**
	 * Indicates whether the abstraction distance for the null node is set to 0 (otherwise or the specified distance is chosen).
	 */
	private boolean aggressiveNullAbstraction = true;

	/**
	 * The minimal number of nodes in a heap configuration before the minimal dereference depth is ignored
	 * and abstraction is performed as aggressive as possible.
	 */
	private int aggressiveAbstractionThreshold = 13;

	/**
	 * Enabling this option results in ignoring restrictions to abstractions for return statements
	 * to reduce the number of final states.
	 */
	private boolean aggressiveReturnAbstraction = true;
	
	/**
	 * Enabling this option results in dead variables (variables that are not accessed before being rewritten in the
	 * following) being deleted in order to enable more possible abstractions.
	 */
	private boolean removeDeadVariables = true;

	/**
	 * Enabling this option leads to using a program analysis based on indexed hyperedge replacement grammars.
	 */
	private boolean indexedMode = false;

	private HeapAutomaton stateLabelingAutomaton = null;

	private HeapAutomaton stateRefinementAutomaton = null;


	/**
	 * @return The maximal size of state spaces before state space generation is given up.
	 */
	public int getMaxStateSpaceSize() {
		return maxStateSpaceSize;
	}

	/**
	 * @param maxStateSpaceSize The maximal size of state spaces before state space generation is given up.
	 */
	public void setMaxStateSpaceSize(int maxStateSpaceSize) {
		this.maxStateSpaceSize = maxStateSpaceSize;
	}

	/**
	 * @return The maximal size of heap configurations before state space generation is given up.
	 */
	public int getMaxStateSize() {
		return maxStateSize;
	}

	/**
	 * @param maxStateSize The maximal size of heap configurations before state space generation is given up.
	 */
	public void setMaxStateSize(int maxStateSize) {
		this.maxStateSize = maxStateSize;
	}

	/**
	 * @return The minimal distance between variables and nodes in an embedding before abstraction is performed.
	 */
	public int getAbstractionDistance() {
		return abstractionDistance;
	}

    /**
     * @param abstractionDistance The minimal distance between variables and nodes in an
     *                            embedding before abstraction is performed.
     */
	public void setAbstractionDistance(int abstractionDistance) {
		assert( abstractionDistance >= 0 );
		this.abstractionDistance = abstractionDistance;
	}

    /**
     * @return True if and only if the set abstraction distance should be ignored for the null node (and instead set to 0).
     */
	public boolean getAggressiveNullAbstraction() {
		return aggressiveNullAbstraction;
	}

    /**
     *
     * @param aggressiveNullAbstraction True if and only if the abstraction distance
     *                            should be ignored for the null node.
     */
	public void setAggressiveNullAbstraction(boolean aggressiveNullAbstraction) {
		if(  abstractionDistance == 0 && aggressiveNullAbstraction){
			logger.warn("The option 'aggressiveNullAbstraction' will have no effect " +
					"since the dereference depth is already set to 0");
		}
		this.aggressiveNullAbstraction = aggressiveNullAbstraction;
	}

    /**
     * @return The minimal size of heap configurations before restrictions to potential abstractions are ignored.
     */
	public int getAggressiveAbstractionThreshold() {
		return aggressiveAbstractionThreshold;
	}

	public void setAggressiveAbstractionThreshold(int aggressiveAbstractionThreshold) {
		if(  abstractionDistance == 0 && aggressiveAbstractionThreshold > 0){
			logger.warn("The option 'aggressiveAbstractionThreshold' will have " +
                    "no effect since the minimal abstraction distance is 0");
		}
		this.aggressiveAbstractionThreshold = aggressiveAbstractionThreshold;
	}

    /**
     * @return True if and only if aggressive abstraction (ignoring minimal dereference depths) is performed before
     *         procedure returns.
     */
	public boolean isAggressiveReturnAbstraction() {
		return aggressiveReturnAbstraction;
	}

	public void setAggressiveReturnAbstraction(boolean aggressiveReturnAbstraction) {
		if(  abstractionDistance == 0 && aggressiveReturnAbstraction){
			logger.warn("The option 'aggressiveReturnAbstraction' will have no effect " +
					" since the abstraction distance is 0");
		}
		this.aggressiveReturnAbstraction = aggressiveReturnAbstraction;
	}

    /**
     * @return True if and only if dead variables are deleted from heap configurations whenever possible.
     */
	public boolean isRemoveDeadVariables() {
		return removeDeadVariables;
	}

    /**
     * @param removeDeadVariables True if and only if dead variables are deleted from
     *                            heap configurations whenever possible.
     */
	public void setRemoveDeadVariables(boolean removeDeadVariables) {
		this.removeDeadVariables = removeDeadVariables;
	}

    /**
     * @return True if and only if an indexed program analysis is performed.
     */
	public boolean isIndexedMode() {
		return indexedMode;
	}

    /**
     *
     * @param indexedMode True if and only if an indexed program analysis is performed.
     */
	public void setIndexedMode(boolean indexedMode) {
		this.indexedMode = indexedMode;
	}

	public void setStateLabelingAutomaton(HeapAutomaton stateLabelingAutomaton)	{

		this.stateLabelingAutomaton = stateLabelingAutomaton;
	}

	public HeapAutomaton getStateLabelingAutomaton() {

		return stateLabelingAutomaton;
	}

	public void setStateRefinementAutomaton(HeapAutomaton stateRefinementAutomaton)	{

		this.stateRefinementAutomaton = stateRefinementAutomaton;
	}

	public HeapAutomaton getStateRefinementAutomaton() {

		return stateRefinementAutomaton;
	}
}
