package de.rwth.i2.attestor.counterexampleGeneration;

import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Skip;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.InvokeCleanup;
import de.rwth.i2.attestor.stateSpaceGeneration.CanonicalizationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

final class CounterexampleStateSpace implements StateSpace {

    private ProgramState initialState;
    private Set<ProgramState> requiredFinalStates;
    private CanonicalizationStrategy canonicalizationStrategy;
    private InvokeCleanup invokeCleanup;

    private int scopeDepth;
    private Set<ProgramState> finalStates = new HashSet<>();

    CounterexampleStateSpace(CanonicalizationStrategy canonicalizationStrategy,
                                    Set<ProgramState> requiredFinalStates,
                                    InvokeCleanup invokeCleanup) {

        this.canonicalizationStrategy = canonicalizationStrategy;
        this.requiredFinalStates = requiredFinalStates;
        this.invokeCleanup = invokeCleanup;

        assert !requiredFinalStates.isEmpty();
        this.scopeDepth = requiredFinalStates.iterator().next().getScopeDepth();
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

    @Override
    public void setFinal(ProgramState state) {

        ProgramState abstractState = getAbstractStateInOriginalStateSpace(state);
       if(requiredFinalStates.contains(abstractState)) {
           requiredFinalStates.remove(abstractState);
           finalStates.add(state);
       }
    }

    private ProgramState getAbstractStateInOriginalStateSpace(ProgramState state)  {

        ProgramState abstractState = canonicalizationStrategy.canonicalize(new Skip(-1), state);
        abstractState.setProgramCounter(-1);

        if(invokeCleanup != null) {
            try {
                invokeCleanup.getCleanedResultState(abstractState, null);
            } catch (NotSufficientlyMaterializedException e) {
                throw new IllegalStateException("Not sufficiently materialized state found.");
            }
        }
        return abstractState;
    }

    @Override
    public ProgramState getStateInStateSpace(ProgramState state) {

        return null;
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
