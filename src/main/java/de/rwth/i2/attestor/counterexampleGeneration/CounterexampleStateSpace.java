package de.rwth.i2.attestor.counterexampleGeneration;

import de.rwth.i2.attestor.semantics.AggressiveTerminalStatement;
import de.rwth.i2.attestor.semantics.TerminalStatement;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.InvokeCleanup;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A custom state space implementation for counterexample generation that stores only final states that are required
 * by some state within a trace. Note that multiple final states might be required due to multiple paths to reach
 * a fixed final state within bodies of procedure calls.
 *
 * @author Christoph
 */
final class CounterexampleStateSpace implements StateSpace {

    private ProgramState initialState;
    private final Program program;
    private final CanonicalizationStrategy canonicalizationStrategy;
    private final Set<ProgramState> requiredFinalStates;
    private final InvokeCleanup invokeCleanup;
    private final SymbolicExecutionObserver invokeObserver;

    private final Set<ProgramState> finalStates = new HashSet<>();

    CounterexampleStateSpace(Program program,
                             CanonicalizationStrategy canonicalizationStrategy,
                             Set<ProgramState> requiredFinalStates,
                             InvokeCleanup invokeCleanup,
                             SymbolicExecutionObserver invokeObserver) {

        this.program = program;
        this.canonicalizationStrategy = canonicalizationStrategy;
        this.requiredFinalStates = requiredFinalStates;
        this.invokeCleanup = invokeCleanup;
        this.invokeObserver = invokeObserver;

        assert !requiredFinalStates.isEmpty();
    }

    @Override
    public Set<ProgramState> getStates() {

        Set<ProgramState> result = new HashSet<>();
        result.add(initialState);
        result.addAll(finalStates);
        return result;
    }

    @Override
    public Set<ProgramState> getInitialStates() {

        return Collections.singleton(initialState);
    }

    @Override
    public TIntSet getInitialStateIds() {

        TIntSet result = new TIntHashSet();
        result.add(initialState.getStateSpaceId());
        return result;
    }

    @Override
    public Set<ProgramState> getFinalStates() {

        return new HashSet<>(finalStates);
    }

    @Override
    public TIntSet getFinalStateIds() {

        TIntSet result = new TIntHashSet();
        for(ProgramState s : finalStates) {
            result.add(s.getStateSpaceId());
        }
        return result;
    }

    @Override
    public int size() {

        return 1 + finalStates.size();
    }

    @Override
    public Set<ProgramState> getControlFlowSuccessorsOf(ProgramState state) {

        if(state.equals(initialState)) {
            return getFinalStates();
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public Set<ProgramState> getMaterializationSuccessorsOf(ProgramState state) {

        return Collections.emptySet();
    }

    @Override
    public Set<ProgramState> getArtificialInfPathsSuccessorsOf(ProgramState state) {

        if(!state.equals(initialState) && finalStates.contains(state)) {
            return Collections.singleton(state);
        }
        return Collections.emptySet();
    }

    @Override
    public TIntArrayList getControlFlowSuccessorsIdsOf(int stateSpaceId) {

        if(initialState.getStateSpaceId() == stateSpaceId) {
            return new TIntArrayList(getFinalStateIds());
        }
        return new TIntArrayList();
    }

    @Override
    public TIntArrayList getMaterializationSuccessorsIdsOf(int stateSpaceId) {

        return new TIntArrayList();
    }

    @Override
    public TIntArrayList getArtificialInfPathsSuccessorsIdsOf(int stateSpaceId) {

        for(ProgramState s : finalStates) {
            if(s.getStateSpaceId() == stateSpaceId) {
                TIntArrayList result = new TIntArrayList();
                result.add(stateSpaceId);
                return result;
            }
        }
        return new TIntArrayList();
    }

    @Override
    public boolean addState(ProgramState state) {
        return true;
    }

    @Override
    public boolean addStateIfAbsent(ProgramState state) {
        return true;
    }

    @Override
    public void addInitialState(ProgramState state) {
        this.initialState = state;
    }

    /** We first reconstruct a fully abstract state from the currently generated
     * final state. If this state is actually required and no (more concrete)
     * representative of this state has been stored yet, we add this state as
     * a final state.
     */
    @Override
    public void setFinal(ProgramState state) {

        ProgramState abstractState = getAbstractStateInOriginalStateSpace(state);
       if(requiredFinalStates.contains(abstractState)) {
           requiredFinalStates.remove(abstractState);
           finalStates.add(state);
       }
    }

    @Override
    public void updateFinalStates(Set<ProgramState> newFinalStates, Map<Integer, Integer> idMapping) {
        assert false;
    }

    private ProgramState getAbstractStateInOriginalStateSpace(ProgramState state)  {

        Semantics semantics = program.getStatement(state.getProgramCounter());

        // TODO
        if(semantics instanceof TerminalStatement) {
            semantics = new AggressiveTerminalStatement();
        }

        ProgramState abstractState = canonicalizationStrategy.canonicalize(semantics, state);
        abstractState.setProgramCounter(-1);

        if(invokeCleanup != null) {
            try {
                invokeCleanup.getCleanedResultState(abstractState, invokeObserver);
            } catch (NotSufficientlyMaterializedException e) {
                throw new IllegalStateException("Not sufficiently materialized state found.");
            }
        }
        return abstractState;
    }

    @Override
    public void addMaterializationTransition(ProgramState from, ProgramState to) {
    }

    @Override
    public void addControlFlowTransition(ProgramState from, ProgramState to) {
    }

    @Override
    public void addArtificialInfPathsTransition(ProgramState cur) {
    }

    @Override
    public ProgramState getState(int id) {

        if(initialState.getStateSpaceId() == id) {
            return initialState;
        }

        for(ProgramState s : finalStates) {
            if(s.getStateSpaceId() == id) {
                return s;
            }
        }
        return null;
    }

    @Override
    public int getMaximalStateSize() {

        return 0;
    }

    @Override
    public boolean satisfiesAP(int stateId, String expectedAP) {

        return false;
    }
}
