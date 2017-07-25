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
		
		if(initialStates.isEmpty()
				|| generator.program == null || generator.materializer == null
				|| generator.canonicalizationStrategy == null || generator.abortStrategy == null) {
			throw new IllegalStateException("StateSpaceGenerator not completely initialized");
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
		generator.materializer = materializationStrategy;
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
     * @param inclusionStrategy The strategy used for discharging the inclusion problem.
     * @return The builder.
     */
	public SSGBuilder setInclusionStrategy(InclusionStrategy inclusionStrategy) {
		generator.inclusionStrategy = inclusionStrategy;
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
	
}
