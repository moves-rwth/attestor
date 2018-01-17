package de.rwth.i2.attestor.phases.modelChecking.modelChecker;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.*;
import java.util.function.Function;

public class FailureTrace implements ModelCheckingTrace {

    private final LinkedList<Integer> stateIdTrace = new LinkedList<>();
    private final LinkedList<ProgramState> stateTrace = new LinkedList<>();
    private Iterator<ProgramState> iterator;

    FailureTrace(Assertion failureAssertion, StateSpace stateSpace) {

        Assertion current = failureAssertion;

        do {
            int stateId = current.getProgramState();
            stateIdTrace.addFirst(stateId);
            ProgramState state = stateSpace.getState(stateId);
            stateTrace.addFirst(state);
            current = current.getParent();
        } while (current != null);

        iterator = stateTrace.iterator();
    }

    @Override
    public List<Integer> getStateIdTrace() {

        return new LinkedList<>(stateIdTrace);
    }


    @Override
    public ProgramState getInitialState() {

        return stateTrace.getFirst();
    }

    @Override
    public ProgramState getFinalState() {

        return stateTrace.getLast();
    }

    public boolean isEmpty() {

        return stateIdTrace.isEmpty();
    }

    public String toString() {

        return stateIdTrace.toString();
    }


    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public ProgramState next() {
        return iterator.next();
    }

    @Override
    public StateSpace getStateSpace() {

        Set<ProgramState> states = new LinkedHashSet<>(stateTrace);
        StateSpace stateSpace = stateTrace.getFirst().getContainingStateSpace();

        return new StateSpace() {
            @Override
            public Set<ProgramState> getStates() {
                return states;
            }

            @Override
            public Set<ProgramState> getInitialStates() {
                return Collections.singleton(stateTrace.getFirst());
            }

            @Override
            public TIntSet getInitialStateIds() {

                TIntSet result = new TIntHashSet();
                result.add(stateIdTrace.getFirst());
                return result;
            }

            @Override
            public Set<ProgramState> getFinalStates() {
                return Collections.singleton(stateTrace.getLast());
            }

            @Override
            public TIntSet getFinalStateIds() {
                TIntSet result = new TIntHashSet();
                result.add(stateIdTrace.getLast());
                return result;
            }

            @Override
            public int size() {
                return stateTrace.size();
            }

            private Set<ProgramState> getSuccessors(ProgramState state,
                                                    Function<Integer, TIntArrayList> getSuccessorIdsOf) {

                Iterator<ProgramState> iterator = stateTrace.iterator();
                while (iterator.hasNext()) {
                    ProgramState current = iterator.next();
                    int id = current.getStateSpaceId();
                    if(id == state.getStateSpaceId()) {

                        if(iterator.hasNext()) {
                            ProgramState successor = iterator.next();
                            TIntArrayList allSuccessors = getSuccessorIdsOf.apply(id);
                            if(allSuccessors.contains(successor.getStateSpaceId())) {
                                return Collections.singleton(successor);
                            }
                        }
                        return Collections.emptySet();
                    }
                }
                return Collections.emptySet();
            }

            @Override
            public Set<ProgramState> getControlFlowSuccessorsOf(ProgramState state) {

                return getSuccessors(state, stateSpace::getControlFlowSuccessorsIdsOf);
            }

            @Override
            public Set<ProgramState> getMaterializationSuccessorsOf(ProgramState state) {

                return getSuccessors(state, stateSpace::getMaterializationSuccessorsIdsOf);
            }

            @Override
            public Set<ProgramState> getArtificialInfPathsSuccessorsOf(ProgramState state) {

                return getSuccessors(state, stateSpace::getArtificialInfPathsSuccessorsIdsOf);
            }

            private TIntArrayList getSuccessorIds(int stateSpaceId,
                                                  Function<Integer, TIntArrayList> getAllSuccessors)  {

                Iterator<Integer> iterator = stateIdTrace.iterator();
                while (iterator.hasNext()) {
                    int id = iterator.next();
                    if(id == stateSpaceId) {

                        if(iterator.hasNext()) {
                            int successor = iterator.next();
                            TIntArrayList allSuccessors = getAllSuccessors.apply(id);
                            if(allSuccessors.contains(stateSpaceId)) {
                                TIntArrayList result = new TIntArrayList();
                                result.add(successor);
                                return result;
                            }
                        }
                        return new TIntArrayList();
                    }
                }
                return new TIntArrayList();
            }

            @Override
            public TIntArrayList getControlFlowSuccessorsIdsOf(int stateSpaceId) {

                return getSuccessorIds(stateSpaceId, stateSpace::getControlFlowSuccessorsIdsOf);
            }

            @Override
            public TIntArrayList getMaterializationSuccessorsIdsOf(int stateSpaceId) {

                return getSuccessorIds(stateSpaceId, stateSpace::getMaterializationSuccessorsIdsOf);
            }

            @Override
            public TIntArrayList getArtificialInfPathsSuccessorsIdsOf(int stateSpaceId) {

                return getSuccessorIds(stateSpaceId, stateSpace::getArtificialInfPathsSuccessorsIdsOf);
            }

            @Override
            public boolean addState(ProgramState state) {
                throw new IllegalStateException("not supported.");
            }

            @Override
            public boolean addStateIfAbsent(ProgramState state) {
                throw new IllegalStateException("not supported.");
            }

            @Override
            public void addInitialState(ProgramState state) {
                throw new IllegalStateException("not supported.");
            }

            @Override
            public void setFinal(ProgramState state) {
                throw new IllegalStateException("not supported.");
            }

            @Override
            public void updateFinalStates(Set<ProgramState> newFinalStates, Map<Integer, Integer> idMapping) {
                throw new IllegalStateException("not supported.");
            }

            @Override
            public void addMaterializationTransition(ProgramState from, ProgramState to) {
                throw new IllegalStateException("not supported.");
            }

            @Override
            public void addControlFlowTransition(ProgramState from, ProgramState to) {
                throw new IllegalStateException("not supported.");
            }

            @Override
            public void addArtificialInfPathsTransition(ProgramState cur) {
                throw new IllegalStateException("not supported.");
            }

            @Override
            public ProgramState getState(int id) {
                for(ProgramState state : stateTrace) {
                    if(state.getStateSpaceId() == id) {
                        return state;
                    }
                }
                return null;
            }

            @Override
            public int getMaximalStateSize() {
                int max = 0;
                for(ProgramState state : states) {
                   max = Math.max(max, state.size());
                }
                return max;
            }

            @Override
            public boolean satisfiesAP(int stateId, String expectedAP) {
                return getState(stateId).satisfiesAP(expectedAP);
            }

            @Override
            public void transformTerminalStates() {
                throw new IllegalStateException("not supported.");
            }
        };
    }
}
