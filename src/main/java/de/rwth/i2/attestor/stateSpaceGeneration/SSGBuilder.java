package de.rwth.i2.attestor.stateSpaceGeneration;

import de.rwth.i2.attestor.main.scene.SceneObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides methods to safely initialize a StateSpaceGenerator.
 *
 * @author Christoph
 */
public class SSGBuilder extends SceneObject {

    /**
     * The initial state passed to the state space generation
     */
    private final List<ProgramState> initialStates;

    /**
     * Internal instance of the StateSpaceGenerator under
     * construction by this builder
     */
    private final StateSpaceGenerator generator;

    /**
     * Creates a new builder representing an everywhere
     * uninitialized StateSpaceGenerator.
     */
    SSGBuilder(SceneObject sceneObject) {

        super(sceneObject);
        initialStates = new ArrayList<>();
        generator = new StateSpaceGenerator(sceneObject);
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

        if (generator.explorationStrategy == null) {
            throw new IllegalStateException("StateSpaceGenerator: No exploration strategy.");
        }

        if (generator.semanticsObserverSupplier == null) {
            throw new IllegalStateException("StateSpaceGenerator: No supplier for semantics options.");
        }

        if (generator.stateSpaceSupplier == null) {
            throw new IllegalStateException("StateSpaceGenerator: No supplier for state spaces.");
        }

        if (generator.postProcessingStrategy == null) {
            throw new IllegalStateException("StateSpaceGenerator: No post-processing strategy.");
        }

        generator.stateSpace = generator.stateSpaceSupplier.get();

        for (ProgramState state : initialStates) {
            state.setProgramCounter(0);
            generator.stateLabelingStrategy.computeAtomicPropositions(state);
            generator.stateSpace.addInitialState(state);
            generator.addUnexploredState(state);
        }

        generator.symbolicExecutionObserver = generator.semanticsObserverSupplier.get(generator);
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
     *                      the state space generation.
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

    /**
     * @param stateRefinementStrategy The strategy to refine states before continuing the symbolic execution.
     * @return The builder.
     */
    public SSGBuilder setStateRefinementStrategy(StateRefinementStrategy stateRefinementStrategy) {

        generator.stateRefinementStrategy = stateRefinementStrategy;
        return this;
    }

    /**
     * @param stateCounter The global counter for the total number of states generated so far.
     * @return The builder.
     */
    public SSGBuilder setStateCounter(StateSpaceGenerator.TotalStatesCounter stateCounter) {

        generator.totalStatesCounter = stateCounter;
        return this;
    }

    /**
     * @param enabled True if and only if it is permitted to eliminate dead variables after a single
     *                step of the symbolic execution.
     * @return The builder.
     */
    public SSGBuilder setDeadVariableElimination(boolean enabled) {

        generator.deadVariableEliminationEnabled = enabled;
        return this;
    }

    /**
     * @param enabled True if and only if the state space should be explored in a breadth-first instead of
     *                a depth-first fashion.
     * @return The builder.
     */
    public SSGBuilder setBreadthFirstSearchEnabled(boolean enabled) {

        generator.breadthFirstSearchEnabled = enabled;
        return this;
    }

    /**
     * @param strategy A strategy that determines whether successors of a given state should be explored further.
     * @return The builder.
     */
    public SSGBuilder setExplorationStrategy(ExplorationStrategy strategy) {

        generator.explorationStrategy = strategy;
        return this;
    }

    /**
     * @param stateSpaceSupplier The function determining which instances of state spaces are generated
     * @return The builder.
     */
    public SSGBuilder setStateSpaceSupplier(StateSpaceSupplier stateSpaceSupplier) {

        generator.stateSpaceSupplier = stateSpaceSupplier;
        return this;
    }

    /**
     * @param semanticsObserverSupplier The function determining which semantics options are passed to individual
     *                                  statements during symbolic execution.
     * @return The builder.
     */
    public SSGBuilder setSemanticsOptionsSupplier(SemanticsObserverSupplier semanticsObserverSupplier) {

        generator.semanticsObserverSupplier = semanticsObserverSupplier;
        return this;
    }

    /**
     * @param postProcessingStrategy A strategy to optimize the state space after state space generation terminated.
     *                               This strategy is applied for each generated state space, including procedure
     *                               calls.
     * @return The builder.
     */
    public SSGBuilder setPostProcessingStrategy(PostProcessingStrategy postProcessingStrategy) {

        generator.postProcessingStrategy = postProcessingStrategy;
        return this;
    }

}
