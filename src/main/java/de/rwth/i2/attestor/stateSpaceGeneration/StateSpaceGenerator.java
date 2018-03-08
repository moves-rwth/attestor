package de.rwth.i2.attestor.stateSpaceGeneration;


import java.util.Collection;

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

    @FunctionalInterface
    public interface TotalStatesCounter {
        void addStates(int states);
    }

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
    StateCanonicalizationStrategy canonicalizationStrategy;
    /**
     * strategy to rectify a state
      */
    StateRectificationStrategy stateRectificationStrategy;
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
     * Strategy determining how states are explored before being added to the state space
     */
    StateExplorationStrategy stateExplorationStrategy;
    /**
     * Strategy determining post-processing after termination of state space generation
     */
    PostProcessingStrategy postProcessingStrategy;
    /**
     * Counter for the total number of states generated so far.
     */
    TotalStatesCounter totalStatesCounter;
    /**
     * Functional interface to obtain instances of state spaces.
     */
    StateSpaceSupplier stateSpaceSupplier;
    /**
     * Functional interface to check whether a state has to be marked as final
     */
    FinalStateStrategy finalStateStrategy;

    boolean alwaysCanonicalize = false;

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
                .setCanonizationStrategy(stateSpaceGenerator.getCanonizationStrategy())
                .setStateRectificationStrategy(stateSpaceGenerator.getStateRectificationStrategy())
                .setMaterializationStrategy(stateSpaceGenerator.getMaterializationStrategy().getHeapStrategy())
                .setStateLabelingStrategy(stateSpaceGenerator.getStateLabelingStrategy())
                .setStateRefinementStrategy(stateSpaceGenerator.getStateRefinementStrategy())
                .setStateExplorationStrategy(stateSpaceGenerator.getStateExplorationStrategy())
                .setStateSpaceSupplier(stateSpaceGenerator.getStateSpaceSupplier())
                .setStateCounter(stateSpaceGenerator.getTotalStatesCounter())
                .setFinalStateStrategy(stateSpaceGenerator.getFinalStateStrategy())
                .setAlwaysCanonicalize(stateSpaceGenerator.isAlwaysCanonicalize())
                .setPostProcessingStrategy(stateSpaceGenerator.getPostProcessingStrategy());
    }

    public boolean isAlwaysCanonicalize() {
        return alwaysCanonicalize;
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
    public StateCanonicalizationStrategy getCanonizationStrategy() {

        return canonicalizationStrategy;
    }

    /**
     * @return The strategy used to rectify states.
     */
    public StateRectificationStrategy getStateRectificationStrategy() {

        return stateRectificationStrategy;
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

    public StateExplorationStrategy getStateExplorationStrategy() {

        return stateExplorationStrategy;
    }

    public PostProcessingStrategy getPostProcessingStrategy() {

        return postProcessingStrategy;
    }

    public StateSpace getStateSpace() {

        return stateSpace;
    }

    public TotalStatesCounter getTotalStatesCounter() {

        return totalStatesCounter;
    }

    public FinalStateStrategy getFinalStateStrategy() {
        return finalStateStrategy;
    }

    /**
     * Attempts to generate a StateSpace according to the
     * underlying analysis.
     *
     * @return The generated StateSpace.
     */
    public StateSpace generate() throws StateSpaceGenerationAbortedException {

        while (stateExplorationStrategy.hasUnexploredStates()) {

            ProgramState state = stateExplorationStrategy.getNextUnexploredState();
            state.setContainingStateSpace( this.stateSpace );

            if(!checkAbortCriteria(state)) {
                totalStatesCounter.addStates(stateSpace.size());
                return stateSpace;
            }

            SemanticsCommand stateSemanticsCommand = semanticsOf(state);

            boolean isMaterialized = materializationPhase(stateSemanticsCommand, state) ;
            if(isMaterialized) {
                Collection<ProgramState> successorStates = stateSemanticsCommand.computeSuccessors(state);
                if(finalStateStrategy.isFinalState(state, successorStates, stateSemanticsCommand)) {
                    stateSpace.setFinal(state);
                    stateSpace.addArtificialInfPathsTransition(state); // Add self-loop to each final state
                } else {
                    for(ProgramState nextState : successorStates) {
                        handleSuccessorState(state, nextState);
                    }
                }
            }
        }

        postProcessingStrategy.process(stateSpace);
        totalStatesCounter.addStates(stateSpace.size());
        return stateSpace;
    }

    private boolean checkAbortCriteria(ProgramState state) throws StateSpaceGenerationAbortedException {

        try {
            abortStrategy.checkAbort(stateSpace);
        } catch (StateSpaceGenerationAbortedException e) {

            stateSpace.setAborted(state);
            abortRemainingStates();
            if (!state.isFromTopLevelStateSpace()) {
                throw e;
            }
            return false;
        }
        return true;
    }

    private void abortRemainingStates() {

        while (stateExplorationStrategy.hasUnexploredStates()) {
            ProgramState unexploredState = stateExplorationStrategy.getNextUnexploredState();
            stateSpace.setAborted(unexploredState);
        }
        assert !stateExplorationStrategy.hasUnexploredStates();
    }


    private SemanticsCommand semanticsOf(ProgramState state) {

        return program.getStatement(state.getProgramCounter());
    }

    /**
     * In the materialization phase violation points of the given state are removed until the current statement
     * can be executed. The materialized states are immediately added to the state space as successors of the
     * given state.
     *
     * @param semanticsCommand The statement that should be executed next
     *                         and thus determines the necessary materialization.
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
            stateExplorationStrategy.addUnexploredState(m, true);
            stateSpace.addMaterializationTransition(state, m);
        }
        return materialized.isEmpty();
    }

    private void labelWithAtomicPropositions(ProgramState state) {

        if(state.isFromTopLevelStateSpace()) {
            stateLabelingStrategy.computeAtomicPropositions(state);
        }
    }

    private void handleSuccessorState(ProgramState state, ProgramState nextState) {

        SemanticsCommand semanticsCommand = semanticsOf(nextState);
        nextState = stateRefinementStrategy.refine(semanticsCommand, nextState);

        if(needsCanonicalization(semanticsCommand, nextState)) {
            ProgramState abstractedState = canonicalizationStrategy.canonicalize(nextState);
            for(ProgramState rectifiedState : stateRectificationStrategy.rectify(abstractedState)) {
                addOrMergeState(state, rectifiedState);
            }
        } else if(state.isContinueState()) {
            // if the previous state is a procedure invocation continued during fixpoint iteration, 
        	//we check whether the next state already exists; even if no canonicalization is performed.
            for(ProgramState rectifiedState : stateRectificationStrategy.rectify(nextState)) {
                addOrMergeState(state, rectifiedState);
            }
        } else {
            for(ProgramState rectifiedState : stateRectificationStrategy.rectify(nextState)) {
                addState(state, rectifiedState);
            }
        }
    }

    private boolean needsCanonicalization(SemanticsCommand semanticsCommand, ProgramState state) {
        return alwaysCanonicalize || semanticsCommand.needsCanonicalization()
                || program.countPredecessors(state.getProgramCounter()) > 1;
    }

    /**
     * If a isomorphic state is already present, a transition to this isomorphic state is added and the
     * state is discareded.
     * Otherwise the state is added to the stateSpace and marked as unexplored. A transition is then added to this state.
     * @param predecessorState the state from which a transition is drawn
     * @param state the state to which a transition is drawn (or to a isomorphic state)
     */
    private void addOrMergeState(ProgramState predecessorState, ProgramState state) {

        labelWithAtomicPropositions(state);
        if (stateSpace.addStateIfAbsent(state)) {
            stateExplorationStrategy.addUnexploredState(state, false);
        }
        stateSpace.addControlFlowTransition(predecessorState, state);
    }

    /**
     * The state is added to the stateSpace and marked as unexplored. Also, a transition
     * form the predecessorState to the state is a added.
     * In particular, there is no check whether a isomorphic state already exists
     * @param predecessorState the state from which a transition is drawn
     * @param state the state to which a transition is drawn
     */
    private void addState(ProgramState predecessorState, ProgramState state) {

        labelWithAtomicPropositions(state);
        stateSpace.addState(state);
        stateExplorationStrategy.addUnexploredState(state, false);
        stateSpace.addControlFlowTransition(predecessorState, state);
    }



}
