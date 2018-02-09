package de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.*;
import java.util.function.Predicate;

/**
 * A dedicated state space for counterexample generation that only stores the initial state and
 * resulting final states.
 *
 * @author Christoph
 */
public class CounterexampleStateSpace implements StateSpace {

    private int lastUsedId = 0;
    private int maximalStateSize = 0;
    private ProgramState initialState = null;
    private final Set<ProgramState> finalStates = new LinkedHashSet<>();
    private final Predicate<ProgramState> isFinalStateRequiredPredicate;

    /**
     *
     * @param isFinalStateRequiredPredicate A predicate that determines whether a given final state is required
     *                                      as part of the counterexample generation or not.
     */
    public CounterexampleStateSpace(Predicate<ProgramState> isFinalStateRequiredPredicate) {

        if(isFinalStateRequiredPredicate == null) {
            throw new IllegalArgumentException("Final state predicate is null.");
        }
        this.isFinalStateRequiredPredicate = isFinalStateRequiredPredicate;
    }

    @Override
    public Set<ProgramState> getStates() {

        Set<ProgramState> result = new LinkedHashSet<>();
        if(initialState != null) {
            result.add(initialState);
        }
        result.addAll(finalStates);
        return result;
    }

    @Override
    public Set<ProgramState> getInitialStates() {
        return Collections.singleton(initialState);
    }

    @Override
    public TIntSet getInitialStateIds() {

        TIntSet result = new TIntHashSet(1);
        result.add(initialState.getStateSpaceId());
        return result;
    }

    private TIntSet getIds(Collection<ProgramState> states) {

        TIntSet result = new TIntHashSet(states.size());
        for(ProgramState state : states) {
            result.add(state.getStateSpaceId());
        }
        return result;
    }

    @Override
    public Set<ProgramState> getFinalStates() {
        return new LinkedHashSet<>(finalStates);
    }

    @Override
    public TIntSet getFinalStateIds() {

        return getIds(finalStates);
    }

    @Override
    public int size() {

        if(initialState != null) {
            return 1 + finalStates.size();
        }
        return  + finalStates.size();
    }

    @Override
    public Set<ProgramState> getControlFlowSuccessorsOf(ProgramState state) {

        if(initialState.equals(state)) {
            return finalStates;
        }
        return Collections.emptySet();
    }

    @Override
    public Set<ProgramState> getMaterializationSuccessorsOf(ProgramState state) {
        return Collections.emptySet();
    }

    @Override
    public Set<ProgramState> getArtificialInfPathsSuccessorsOf(ProgramState state) {

        if(finalStates.contains(state)) {
            return Collections.singleton(state);
        }
        return Collections.emptySet();
    }

    @Override
    public TIntArrayList getControlFlowSuccessorsIdsOf(int stateSpaceId) {

        TIntArrayList result = new TIntArrayList();
        if(initialState.getStateSpaceId() == stateSpaceId) {
            for(ProgramState state : finalStates) {
                result.add(state.getStateSpaceId());
            }
        }
        return result;
    }

    @Override
    public TIntArrayList getMaterializationSuccessorsIdsOf(int stateSpaceId) {
        return new TIntArrayList();
    }

    @Override
    public TIntArrayList getArtificialInfPathsSuccessorsIdsOf(int stateSpaceId) {

        TIntArrayList result = new TIntArrayList();

        if(stateSpaceId == initialState.getStateSpaceId()) {
           return result;
        }

        ProgramState state = getState(stateSpaceId);
        result.add(state.getStateSpaceId());
        return result;
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

        if(initialState != null) {
            throw new IllegalArgumentException("At most one initial state is permitted.");
        }
        initialState = updateState(state);
    }

    private ProgramState updateState(ProgramState state) {
        state.setStateSpaceId(lastUsedId);
        int nodes = state.getHeap().countNodes();
        maximalStateSize = Math.max(nodes, maximalStateSize);
        ++lastUsedId;
        return state;
    }

    @Override
    public void setFinal(ProgramState state) {

        if(isFinalStateRequiredPredicate.test(state)) {
            finalStates.add(updateState(state));
        }
    }

    @Override
    public void setAborted(ProgramState state) {
    }

    @Override
    public boolean containsAbortedStates() {
        return false;
    }

    @Override
    public void updateFinalStates(Set<ProgramState> newFinalStates, Map<Integer, Integer> idMapping) {

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

        for(ProgramState state : finalStates) {
            if(state.getStateSpaceId() == id) {
                return state;
            }
        }

        throw new IllegalArgumentException("Id not found.");

    }

    @Override
    public int getMaximalStateSize() {
        return maximalStateSize;
    }

    @Override
    public boolean satisfiesAP(int stateId, String expectedAP) {

        return getState(stateId).satisfiesAP(expectedAP);
    }

}
