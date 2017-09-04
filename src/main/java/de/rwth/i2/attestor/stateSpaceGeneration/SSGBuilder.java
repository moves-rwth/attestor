package de.rwth.i2.attestor.stateSpaceGeneration;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides methods to safely initialize a StateSpaceGenerator.
 * The only means to create a new StateSpaceGenerator is through the static method
 * {@link StateSpaceGenerator#builder()}.
 *
 * @author Christoph
 *
 */
public class SSGBuilder {
	
	/**
	 * The initial state passed to the state space generation
	 */
	private List<ProgramState> initialStates;
	
	/**
	 * Internal instance of the StateSpaceGenerator under
	 * construction by this builder
	 */
	private final StateSpaceGenerator generator;
	
	/**
	 * Creates a new builder representing an everywhere
	 * uninitialized StateSpaceGenerator.
	 */
    SSGBuilder() {
		initialStates = new ArrayList<>();
		generator = new StateSpaceGenerator();
	}
	
	/**
	 * Attempts to construct a new StateSpaceGenerator.
	 * If the initialization is incomplete or invalid
	 * calling this method causes an IllegalStateException.
	 * 
	 * @return StateSpaceGenerator initialized by the previously called
	 * methods of this builder
	 */
	public StateSpaceGenerator build() {

	    if(initialStates.isEmpty())	 {
	    	throw new IllegalStateException("StateSpaceGenerator: No initial states.");
		}

		if(generator.program == null) {
			throw new IllegalStateException("StateSpaceGenerator: No program.");
		}

		if(generator.materializationStrategy == null) {
			throw new IllegalStateException("StateSpaceGenerator: No materialization strategy.");
		}

		if(generator.canonicalizationStrategy== null) {
			throw new IllegalStateException("StateSpaceGenerator: No canonicalization strategy.");
		}

		if(generator.abortStrategy == null) {
			throw new IllegalStateException("StateSpaceGenerator: No abort strategy.");
		}

		if(generator.stateLabelingStrategy == null)	{
	    	throw new IllegalStateException("StateSpaceGenerator: No state labeling strategy.");
		}

		if(generator.stateRefinementStrategy == null)	{
			throw new IllegalStateException("StateSpaceGenerator: No state refinement strategy.");
		}

		if(generator.totalStatesCounter == null) {
			throw new IllegalStateException("StateSpaceGenerator: No state counter.");
		}

		for (ProgramState state : initialStates) {
			state.setProgramCounter(0);
			generator.stateSpace.addInitialState(state);
			generator.unexploredConfigurations.add(state);
		}

		return generator;
	}

    /**
     * @param initialState The initial state from which all reachable states are computed by
     *                     the state space generation.
     * @return The builder.
     */
	public SSGBuilder addInitialState(ProgramState initialState) {
		initialStates.add(initialState);
		return this;
	}

	/**
	 * @param initialStates The initial states from which all reachable states are computed by
	 *                     the state space generation.
	 * @return The builder.
	 */
	public SSGBuilder addInitialStates(List<ProgramState> initialStates) {
		this.initialStates.addAll(initialStates);
		return this;
	}

    /**
     * @param program The program that is executed to generate the state space.
     * @return The builder.
     */
	public SSGBuilder setProgram(Program program) {
		generator.program = program;
		return this;
	}

	/**
	 * @param materializationStrategy The strategy used for materialization.
	 * @return The builder.
	 */
	public SSGBuilder setMaterializationStrategy(MaterializationStrategy materializationStrategy) {
		generator.materializationStrategy = materializationStrategy;
		return this;
	}

    /**
     * @param canonicalizationStrategy The strategy used for canonicalization.
     * @return The builder.
     */
	public SSGBuilder setCanonizationStrategy(CanonicalizationStrategy canonicalizationStrategy) {
		generator.canonicalizationStrategy = canonicalizationStrategy;
		return this;
	}

    /**
     * @param abortStrategy The strategy used for aborting the state space generation.
     * @return The builder.
     */
	public SSGBuilder setAbortStrategy(AbortStrategy abortStrategy) {
		generator.abortStrategy = abortStrategy;
		return this;
	}

    /**
     * @param stateLabelingStrategy The strategy used to label states with atomic propositions.
     * @return The builder.
     */
	public SSGBuilder setStateLabelingStrategy(StateLabelingStrategy stateLabelingStrategy) {
		generator.stateLabelingStrategy = stateLabelingStrategy;
		return this;
	}

	public SSGBuilder setStateRefinementStrategy(StateRefinementStrategy stateRefinementStrategy) {
		generator.stateRefinementStrategy = stateRefinementStrategy;
		return this;
	}

	public SSGBuilder setStateCounter(StateSpaceGenerator.TotalStatesCounter stateCounter) {
		generator.totalStatesCounter = stateCounter;
		return this;
	}
	
}
