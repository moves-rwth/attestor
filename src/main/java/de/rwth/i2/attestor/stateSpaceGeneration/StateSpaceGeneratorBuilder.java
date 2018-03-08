package de.rwth.i2.attestor.stateSpaceGeneration;


import de.rwth.i2.attestor.grammar.materialization.strategies.MaterializationStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides methodExecution to safely initialize a StateSpaceGenerator.
 *
 * @author Christoph
 */
public class StateSpaceGeneratorBuilder {

    /**
     * The initial state passed to the state space generation
     */
    protected final List<ProgramState> initialStates;



    /**
     * Internal instance of the StateSpaceGenerator under
     * construction by this builder
     */
    protected final StateSpaceGenerator generator;



    private StateSpace initialStateSpace = null;

    /**
     * Creates a new builder representing an everywhere
     * uninitialized StateSpaceGenerator.
     */
    StateSpaceGeneratorBuilder() {

        initialStates = new ArrayList<>();
        generator = new StateSpaceGenerator();
    }


    /**
     * Attempts to construct a new StateSpaceGenerator.
     * If the initialization is incomplete or invalid
     * calling this method causes an IllegalStateException.
     *
     * @return StateSpaceGenerator initialized by the previously called
     * methodExecution of this builder
     */
    public StateSpaceGenerator build() {

        if (initialStates.isEmpty()) {
            throw new IllegalStateException("StateSpaceGenerator: No initial states.");
        }

        if (generator.program == null) {
            throw new IllegalStateException("StateSpaceGenerator: No program.");
        }

        if (generator.materializationStrategy == null) {
            throw new IllegalStateException("StateSpaceGenerator: No materialization strategy.");
        }

        if (generator.canonicalizationStrategy == null) {
            throw new IllegalStateException("StateSpaceGenerator: No canonicalization strategy.");
        }

        if (generator.abortStrategy == null) {
            throw new IllegalStateException("StateSpaceGenerator: No abort strategy.");
        }

        if (generator.stateLabelingStrategy == null) {
            throw new IllegalStateException("StateSpaceGenerator: No state labeling strategy.");
        }

        if (generator.stateRefinementStrategy == null) {
            throw new IllegalStateException("StateSpaceGenerator: No state refinement strategy.");
        }

        if (generator.totalStatesCounter == null) {
            throw new IllegalStateException("StateSpaceGenerator: No state counter.");
        }

        if (generator.stateExplorationStrategy == null) {
            throw new IllegalStateException("StateSpaceGenerator: No state exploration strategy.");
        }

        if (generator.stateSpaceSupplier == null) {
            throw new IllegalStateException("StateSpaceGenerator: No supplier for state spaces.");
        }

        if (generator.postProcessingStrategy == null) {
            throw new IllegalStateException("StateSpaceGenerator: No post-processing strategy.");
        }

        if(generator.finalStateStrategy == null) {
            throw new IllegalStateException("StateSpaceGenerator: No final state strategy.");
        }

        if(generator.stateRectificationStrategy == null) {
            throw new IllegalStateException("StateSpaceGenerator: No admissibility strategy.");
        }

        if(initialStateSpace == null) {
            generator.stateSpace = generator.stateSpaceSupplier.get();
        } else {
            generator.stateSpace = initialStateSpace;
        }

        for (ProgramState state : initialStates) {

            if(initialStateSpace == null) {
                state.setProgramCounter(0);
                generator.stateSpace.addInitialState(state);
            }
            generator.stateLabelingStrategy.computeAtomicPropositions(state);
            generator.stateExplorationStrategy.addUnexploredState(state, false);
        }

        return generator;
    }

    /**
     * @param initialState The initial state from which all reachable states are computed by
     *                     the state space generation.
     * @return The builder.
     */
    public StateSpaceGeneratorBuilder addInitialState(ProgramState initialState) {

        initialStates.add(initialState);
        return this;
    }

    /**
     * @param initialStates The initial states from which all reachable states are computed by
     *                      the state space generation.
     * @return The builder.
     */
    public StateSpaceGeneratorBuilder addInitialStates(List<ProgramState> initialStates) {

        this.initialStates.addAll(initialStates);
        return this;
    }

    /**
     * @param program The program that is executed to generate the state space.
     * @return The builder.
     */
    public StateSpaceGeneratorBuilder setProgram(Program program) {

        generator.program = program;
        return this;
    }

    /**
     * @param materializationStrategy The strategy used for materialization.
     * @return The builder.
     */
    public StateSpaceGeneratorBuilder setMaterializationStrategy(MaterializationStrategy materializationStrategy) {

        generator.materializationStrategy = new StateMaterializationStrategy(materializationStrategy);
        return this;
    }

    /**
     * @param canonicalizationStrategy The strategy used for canonicalization.
     * @return The builder.
     */
    public StateSpaceGeneratorBuilder setCanonizationStrategy(StateCanonicalizationStrategy canonicalizationStrategy) {

        generator.canonicalizationStrategy = canonicalizationStrategy;
        return this;
    }

    public StateSpaceGeneratorBuilder setStateRectificationStrategy(StateRectificationStrategy stateRectificationStrategy) {

        generator.stateRectificationStrategy = stateRectificationStrategy;
        return this;
    }

    /**
     * @param abortStrategy The strategy used for aborting the state space generation.
     * @return The builder.
     */
    public StateSpaceGeneratorBuilder setAbortStrategy(AbortStrategy abortStrategy) {

        generator.abortStrategy = abortStrategy;
        return this;
    }

    /**
     * @param stateLabelingStrategy The strategy used to label states with atomic propositions.
     * @return The builder.
     */
    public StateSpaceGeneratorBuilder setStateLabelingStrategy(StateLabelingStrategy stateLabelingStrategy) {

        generator.stateLabelingStrategy = stateLabelingStrategy;
        return this;
    }

    /**
     * @param stateRefinementStrategy The strategy to refine states before continuing the symbolic execution.
     * @return The builder.
     */
    public StateSpaceGeneratorBuilder setStateRefinementStrategy(StateRefinementStrategy stateRefinementStrategy) {

        generator.stateRefinementStrategy = stateRefinementStrategy;
        return this;
    }

    /**
     * @param stateCounter The global counter for the total number of states generated so far.
     * @return The builder.
     */
    public StateSpaceGeneratorBuilder setStateCounter(StateSpaceGenerator.TotalStatesCounter stateCounter) {

        generator.totalStatesCounter = stateCounter;
        return this;
    }

    /**
     * @param strategy A strategy that determines how successors of a given state are explored.
     * @return The builder.
     */
    public StateSpaceGeneratorBuilder setStateExplorationStrategy(StateExplorationStrategy strategy) {

        generator.stateExplorationStrategy = strategy;
        return this;
    }

    /**
     * @param stateSpaceSupplier The function determining which instances of state spaces are generated
     * @return The builder.
     */
    public StateSpaceGeneratorBuilder setStateSpaceSupplier(StateSpaceSupplier stateSpaceSupplier) {

        generator.stateSpaceSupplier = stateSpaceSupplier;
        return this;
    }

    /**
     * @param postProcessingStrategy A strategy to optimize the state space after state space generation terminated.
     *                               This strategy is applied for each generated state space, including procedure
     *                               calls.
     * @return The builder.
     */
    public StateSpaceGeneratorBuilder setPostProcessingStrategy(PostProcessingStrategy postProcessingStrategy) {

        generator.postProcessingStrategy = postProcessingStrategy;
        return this;
    }

    /**
     * Optional method to determine a (possibly non-empty) initial state space used for state space generation.
     * @param initialStateSpace The state space to use instead of a fresh one.
     * @return The builder.
     */
    public StateSpaceGeneratorBuilder setInitialStateSpace(StateSpace initialStateSpace) {

        this.initialStateSpace = initialStateSpace;
        return this;
    }

    public StateSpaceGeneratorBuilder setFinalStateStrategy(FinalStateStrategy finalStateStrategy) {

        generator.finalStateStrategy = finalStateStrategy;
        return this;
    }

    public StateSpaceGeneratorBuilder setAlwaysCanonicalize(boolean alwaysCanonicalize) {

        generator.alwaysCanonicalize = alwaysCanonicalize;
        return this;
    }

}
