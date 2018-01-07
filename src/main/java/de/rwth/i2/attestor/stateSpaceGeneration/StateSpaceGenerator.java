package de.rwth.i2.attestor.stateSpaceGeneration;


import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.semantics.TerminalStatement;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;

/**
 * A StateSpaceGenerator takes an analysis and generates a
 * state space from it. <br>
 * Initialization of a StateSpaceGenerator has to be performed
 * by calling the static method StateSpaceGenerator.builder().
 * This yields a builder object to configure the analysis used during
 * state space generation.
 * <br>
 * The generation of a StateSpace itself is started by invoking generate().
 *
 * @author christoph
 */
public class StateSpaceGenerator {

    private final static Logger logger = LogManager.getLogger("StateSpaceGenerator");
    /**
     * Stores the program configurations that still have
     * to be executed by the state space generation
     */
    final LinkedList<ProgramState> unexploredConfigurations = new LinkedList<>();
    /**
     * Stores the state space generated upon instantiation of
     * this generator.
     */
    StateSpace stateSpace;
    /**
     * The control flow of the program together with the
     * abstract semantics of each statement
     */
    Program program;
    /**
     * Strategy guiding the materialization of states.
     * This strategy is invoked whenever an abstract transfer
     * function cannot be executed.
     */
    StateMaterializationStrategy materializationStrategy;
    /**
     * Strategy guiding the canonicalization of states.
     * This strategy is invoked after execution of abstract transfer
     * functions in order to generalize.
     */
    StateCanonicalizationStrategyWrapper canonicalizationStrategy;
    /**
     * Strategy determining when to give up on further state space
     * exploration.
     */
    AbortStrategy abortStrategy;
    /**
     * Strategy determining the labels of states in the state space
     */
    StateLabelingStrategy stateLabelingStrategy;
    /**
     * Strategy determining how states are refined prior to canonicalization
     */
    StateRefinementStrategy stateRefinementStrategy;
    /**
     * Strategy determining whether successors of a given state should also be explored.
     */
    ExplorationStrategy explorationStrategy;
    /**
     * Strategy determining post-processing after termination of state space generation
     */
    PostProcessingStrategy postProcessingStrategy;
    /**
     * Counter for the total number of states generated so far.
     */
    TotalStatesCounter totalStatesCounter;
    /**
     * Flag that determines whether the state space is generated in a depth-first
     * or breadth-first fashion.
     */
    boolean breadthFirstSearchEnabled;
    /**
     * Functional interface to obtain instances of state spaces.
     */
    StateSpaceSupplier stateSpaceSupplier;

    protected StateSpaceGenerator() {
    }

    /**
     * @return An StateSpaceGeneratorBuilder which is the only means to create a new
     * StateSpaceGenerator object.
     */
    public static StateSpaceGeneratorBuilder builder() {

        return new StateSpaceGeneratorBuilder();
    }

    public static StateSpaceGeneratorBuilder builder(StateSpaceGenerator stateSpaceGenerator) {

        return new StateSpaceGeneratorBuilder()
                .setAbortStrategy(stateSpaceGenerator.getAbortStrategy())
                .setCanonizationStrategy(stateSpaceGenerator.getCanonizationStrategy().getHeapStrategy())
                .setMaterializationStrategy(stateSpaceGenerator.getMaterializationStrategy().getHeapStrategy())
                .setStateLabelingStrategy(stateSpaceGenerator.getStateLabelingStrategy())
                .setStateRefinementStrategy(stateSpaceGenerator.getStateRefinementStrategy())
                .setBreadthFirstSearchEnabled(stateSpaceGenerator.isBreadthFirstSearchEnabled())
                .setExplorationStrategy(stateSpaceGenerator.getExplorationStrategy())
                .setStateSpaceSupplier(stateSpaceGenerator.getStateSpaceSupplier())
                .setStateCounter(stateSpaceGenerator.getTotalStatesCounter())
                .setPostProcessingStrategy(stateSpaceGenerator.getPostProcessingStrategy());
    }

    /**
     * @return The strategy determining when state space generation is aborted.
     */
    public AbortStrategy getAbortStrategy() {

        return abortStrategy;
    }

    /**
     * @return The strategy determining how materialization is performed.
     */
    public StateMaterializationStrategy getMaterializationStrategy() {

        return materializationStrategy;
    }

    /**
     * @return The strategy determining how canonicalization is performed.
     */
    public StateCanonicalizationStrategyWrapper getCanonizationStrategy() {

        return canonicalizationStrategy;
    }

    /**
     * @return The strategy determining how states are labeled with atomic propositions.
     */
    public StateLabelingStrategy getStateLabelingStrategy() {

        return stateLabelingStrategy;
    }

    /**
     * @return The strategy determining how states are refined prior to canonicalization.
     */
    public StateRefinementStrategy getStateRefinementStrategy() {

        return stateRefinementStrategy;
    }

    public StateSpaceSupplier getStateSpaceSupplier() {

        return stateSpaceSupplier;
    }

    /**
     * @return The strategy determining whether successors of a given state should be explored or not.
     */
    public ExplorationStrategy getExplorationStrategy() {

        return explorationStrategy;
    }

    public PostProcessingStrategy getPostProcessingStrategy() {

        return postProcessingStrategy;
    }

    public boolean isBreadthFirstSearchEnabled() {

        return breadthFirstSearchEnabled;
    }

    public StateSpace getStateSpace() {

        return stateSpace;
    }

    public TotalStatesCounter getTotalStatesCounter() {

        return totalStatesCounter;
    }

    /**
     * Attempts to generate a StateSpace according to the
     * underlying analysis.
     *
     * @return The generated StateSpace.
     */
    public StateSpace generate() throws StateSpaceGenerationAbortedException {

        while (hasUnexploredStates()) {

            ProgramState state = nextUnexploredState();
            state.setContainingStateSpace( this.stateSpace );

            try {
                abortStrategy.checkAbort(stateSpace);
            } catch (StateSpaceGenerationAbortedException e) {
                if (!state.isFromTopLevelStateSpace()) {
                    throw e;
                }
                break;
            }

            SemanticsCommand stateSemanticsCommand = semanticsOf(state);
            boolean isSufficientlyMaterialized = materializationPhase(stateSemanticsCommand, state);

            if (isSufficientlyMaterialized) {
                Collection<ProgramState> successorStates = executionPhase(stateSemanticsCommand, state);
                if (successorStates.isEmpty() && isTerminalStatement(stateSemantics) ) {
                    stateSpace.setFinal(state);
                    // Add self-loop to each final state
                    stateSpace.addArtificialInfPathsTransition(state);
                } else {
                    successorStates.forEach(nextState -> {
                        SemanticsCommand semanticsCommand = semanticsOf(nextState);
                        nextState = stateRefinementStrategy.refine(semanticsCommand, nextState);
                        nextState = canonicalizationPhase(semanticsCommand, nextState);
                        if (state.isFromTopLevelStateSpace()) {
                            stateLabelingStrategy.computeAtomicPropositions(nextState);
                        }
                        addingPhase(semanticsCommand, state, nextState);
                    });
                }
            }
        }

        postProcessingStrategy.process(stateSpace);
        totalStatesCounter.addStates(stateSpace.size());
        return stateSpace;
    }

	private boolean isTerminalStatement(Semantics stateSemantics) {
		return stateSemantics.getClass() == TerminalStatement.class;
	}

    /**
     * @return true iff further states can and should be generated.
     */
    private boolean hasUnexploredStates() {

        return !unexploredConfigurations.isEmpty();
    }

    private ProgramState nextUnexploredState() {

        if (breadthFirstSearchEnabled) {
            return unexploredConfigurations.removeFirst();
        } else {
            return unexploredConfigurations.removeLast();
        }
    }

    protected void addUnexploredState(ProgramState state, boolean isMaterializedState) {

        if (explorationStrategy.check(state, isMaterializedState)) {
            unexploredConfigurations.addLast(state);
        }
    }

    private SemanticsCommand semanticsOf(ProgramState state) {

        return program.getStatement(state.getProgramCounter());
    }

    /**
     * In the materialization phase violation points of the given state are removed until the current statement
     * can be executed. The materialized states are immediately added to the state space as successors of the
     * given state.
     *
     * @param semanticsCommand The statement that should be executed next and thus determines the necessary materialization.
     * @param state     The program state that should be materialized.
     * @return True if and only if no materialization is needed.
     */
    private boolean materializationPhase(SemanticsCommand semanticsCommand, ProgramState state) {

        Collection<ProgramState> materialized = materializationStrategy.materialize(
                state,
                semanticsCommand.getPotentialViolationPoints()
        );

        for (ProgramState m : materialized) {
            // performance optimization that prevents isomorphism checks against states in the state space.
            stateSpace.addState(m);
            addUnexploredState(m, true);
            stateSpace.addMaterializationTransition(state, m);
        }
        return materialized.isEmpty();
    }

    /**
     * Computes canonical successors of the given program state.
     *
     * @param semanticsCommand The statement that should be executed.
     * @param state     The program state whose successor states shall be computed.
     */
    private Collection<ProgramState> executionPhase(SemanticsCommand semanticsCommand, ProgramState state)
            throws StateSpaceGenerationAbortedException {

        try {
            return semanticsCommand.computeSuccessors(state);
        } catch (NotSufficientlyMaterializedException e) {
            logger.error("A state could not be sufficiently materialized.");
            return Collections.emptySet();
        }
    }

    private ProgramState canonicalizationPhase(SemanticsCommand semanticsCommand, ProgramState state) {

        if (needsCanonicalization(semanticsCommand, state)) {
            state = canonicalizationStrategy.canonicalize(state);
        }
        return state;
    }

    /**
     * Adds a state as a successor of the given previous state to the state space
     * provided to no subsuming state already exists.
     *
     * @param semanticsCommand     The statement that has been executed on previousState to get to state.
     * @param previousState The predecessor of the given state.
     * @param state         The state that should be added.
     */
    private void addingPhase(SemanticsCommand semanticsCommand, ProgramState previousState, ProgramState state) {

        // performance optimization that prevents isomorphism checks against states in the state space.
        if (!needsCanonicalization(semanticsCommand, state)) {
            stateSpace.addState(state);
            addUnexploredState(state, false);
        } else if (stateSpace.addStateIfAbsent(state)) {
            addUnexploredState(state, false);
        }

        stateSpace.addControlFlowTransition(previousState, state);
    }

    private boolean needsCanonicalization(SemanticsCommand semanticsCommand, ProgramState state) {
        return semanticsCommand.needsCanonicalization() || program.countPredecessors(state.getProgramCounter()) > 1;
    }

    @FunctionalInterface
    public interface TotalStatesCounter {

        void addStates(int states);
    }

}
