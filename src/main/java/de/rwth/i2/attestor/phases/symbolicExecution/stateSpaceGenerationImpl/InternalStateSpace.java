package de.rwth.i2.attestor.phases.symbolicExecution.stateSpaceGenerationImpl;

import de.rwth.i2.attestor.programState.AtomicPropositions;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import gnu.trove.TIntCollection;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.*;

public class InternalStateSpace implements StateSpace {

    private final Map<ProgramState, ProgramState> potentialMergeStates;
    private final List<ProgramState> otherStates; // states that are never checked for isomorphism
    private final TIntSet initialStateIds;
    private final TIntSet finalStateIds;
    private final TIntObjectMap<TIntArrayList> materializationSuccessors;
    private final TIntObjectMap<TIntArrayList> controlFlowSuccessors;
    // TODO: Self-loops are managed here! Use map to int instead of list?!?
    private final TIntObjectMap<TIntArrayList> artificialInfPathsSuccessors;
    private final TIntObjectMap<Set<String>> atomicPropMap;
    private TIntObjectMap<ProgramState> stateIdLookupTable = null;
    private int nextStateId = 0;
    private int maximalStateSize = 0;
    private boolean containsAtLeastOneAbortedState = false;

    public InternalStateSpace(int capacity) {

        capacity = 2 * capacity;
        potentialMergeStates = new LinkedHashMap<>(capacity, 0.8f);
        otherStates = new ArrayList<>(capacity);
        initialStateIds = new TIntHashSet(100);
        finalStateIds = new TIntHashSet(100);
        materializationSuccessors = new TIntObjectHashMap<>(capacity, 0.8f);
        controlFlowSuccessors = new TIntObjectHashMap<>(capacity, 0.8f);
        artificialInfPathsSuccessors = new TIntObjectHashMap<>(100);
        atomicPropMap = new TIntObjectHashMap<>(capacity, 0.8f);
    }

    private static void replaceIds(TIntObjectMap<TIntArrayList> map, Map<Integer, Integer> idMapping) {

        TIntObjectIterator<TIntArrayList> iterator = map.iterator();
        while (iterator.hasNext()) {
            iterator.advance();
            iterator.value().transformValues(id -> idMapping.getOrDefault(id, id));
        }
    }

    public Set<ProgramState> getInitialStates() {

        return getStatesOf(initialStateIds);
    }

    private Set<ProgramState> getStatesOf(TIntCollection collection) {

        initLookupTable();
        Set<ProgramState> result = new LinkedHashSet<>(collection.size());
        TIntIterator iter = collection.iterator();
        while (iter.hasNext()) {
            int id = iter.next();
            result.add(stateIdLookupTable.get(id));
        }

        return result;
    }

    private void initLookupTable() {

        if (stateIdLookupTable == null || stateIdLookupTable.size() < getStates().size() ) {
            stateIdLookupTable = new TIntObjectHashMap<>(getStates().size());
            for (ProgramState state : getStates()) {
                stateIdLookupTable.put(state.getStateSpaceId(), state);
            }
        }
    }



    public TIntSet getInitialStateIds() {

        return initialStateIds;
    }

    @Override
    public Set<ProgramState> getFinalStates() {

        return getStatesOf(finalStateIds);
    }

    public TIntSet getFinalStateIds() {

        return finalStateIds;
    }

    @Override
    public int size() {

        return potentialMergeStates.size() + otherStates.size();
    }

    @Override
    public Set<ProgramState> getControlFlowSuccessorsOf(ProgramState state) {

        int stateSpaceId = state.getStateSpaceId();

        TIntArrayList successors = controlFlowSuccessors.get(stateSpaceId);

        if (successors.isEmpty()) {
            return Collections.emptySet();
        }

        return getStatesOf(successors);
    }

    @Override
    public Set<ProgramState> getMaterializationSuccessorsOf(ProgramState state) {

        int stateSpaceId = state.getStateSpaceId();
        TIntArrayList successors = materializationSuccessors.get(stateSpaceId);

        if (successors.isEmpty()) {
            return Collections.emptySet();
        }

        return getStatesOf(successors);
    }

    @Override
    public Set<ProgramState> getArtificialInfPathsSuccessorsOf(ProgramState state) {

        int stateSpaceId = state.getStateSpaceId();
        TIntArrayList successors = artificialInfPathsSuccessors.get(stateSpaceId);


        if (successors.isEmpty()) {
            return Collections.emptySet();
        }

        return getStatesOf(successors);
    }

    @Override
    public TIntArrayList getControlFlowSuccessorsIdsOf(int stateSpaceId) {

        return controlFlowSuccessors.get(stateSpaceId);
    }

    @Override
    public TIntArrayList getMaterializationSuccessorsIdsOf(int stateSpaceId) {

        return materializationSuccessors.get(stateSpaceId);
    }

    @Override
    public TIntArrayList getArtificialInfPathsSuccessorsIdsOf(int stateSpaceId) {

        return artificialInfPathsSuccessors.get(stateSpaceId);
    }

    @Override
    public boolean addState(ProgramState state) {

        otherStates.add(state);
        updateAddedState(state);
        return true;
    }

    @Override
    public boolean addStateIfAbsent(ProgramState state) {

        ProgramState old = potentialMergeStates.putIfAbsent(state, state);
        if (old == null) {
            updateAddedState(state);
            return true;
        } else {
            state.setStateSpaceId(old.getStateSpaceId());
        }
        return false;
    }

    private void updateAddedState(ProgramState state) {

        state.setStateSpaceId(nextStateId);
        materializationSuccessors.put(nextStateId, new TIntArrayList());
        controlFlowSuccessors.put(nextStateId, new TIntArrayList());
        artificialInfPathsSuccessors.put(nextStateId, new TIntArrayList());
        // TODO: In the long run remove APs from program state!
        atomicPropMap.put(nextStateId, state.getAPs());
        maximalStateSize = Math.max(maximalStateSize, state.size());
        ++nextStateId;
    }

    @Override
    public void addInitialState(ProgramState state) {

        addStateIfAbsent(state);
        initialStateIds.add(state.getStateSpaceId());
    }

    @Override
    public void setFinal(ProgramState state) {

        finalStateIds.add(state.getStateSpaceId());
        state.addAP("{ terminated }");
    }

    @Override
    public void setAborted(ProgramState state) {

        state.addAP(AtomicPropositions.ABORTED);
        this.containsAtLeastOneAbortedState = true;
    }

    @Override
    public boolean containsAbortedStates() {

        return containsAtLeastOneAbortedState;
    }

    @Override
    public void updateFinalStates(Set<ProgramState> newFinalStates, Map<Integer, Integer> idMapping) {

        initLookupTable();

        TIntIterator idIterator = finalStateIds.iterator();
        while (idIterator.hasNext()) {
            int id = idIterator.next();
            ProgramState state = stateIdLookupTable.get(id);
            potentialMergeStates.remove(state);
            otherStates.remove(state);
            stateIdLookupTable.remove(id);
            artificialInfPathsSuccessors.remove(id);
        }

        finalStateIds.clear();
        for (ProgramState s : newFinalStates) {
            finalStateIds.add(s.getStateSpaceId());
            potentialMergeStates.put(s, s);
            stateIdLookupTable.put(s.getStateSpaceId(), s);

            TIntArrayList tIntArrayList = new TIntArrayList();
            tIntArrayList.add(s.getStateSpaceId());
            artificialInfPathsSuccessors.put(s.getStateSpaceId(), tIntArrayList);
        }

        // redirect
        replaceIds(controlFlowSuccessors, idMapping);
    }

    @Override
    public void addMaterializationTransition(ProgramState from, ProgramState to) {

        addTransition(from, to, materializationSuccessors);
    }

    @Override
    public void addControlFlowTransition(ProgramState from, ProgramState to) {

        addTransition(from, to, controlFlowSuccessors);
    }

    public void addArtificialInfPathsTransition(ProgramState cur) {

        addTransition(cur, cur, artificialInfPathsSuccessors);

    }

    @Override
    public ProgramState getState(int id) {

        initLookupTable();
        return stateIdLookupTable.get(id);
    }

    private void addTransition(ProgramState from, ProgramState to, TIntObjectMap<TIntArrayList> successors) {

        int fId = from.getStateSpaceId();
        int tId = to.getStateSpaceId();

        TIntArrayList succ = successors.get(fId);
        if (!succ.contains(tId)) {
            succ.add(tId);
        }
    }

    @Override
    public int getMaximalStateSize() {

        return maximalStateSize;
    }

    @Override
    public boolean satisfiesAP(int stateId, String expectedAP) {

        Set<String> satisfiedAPs = atomicPropMap.get(stateId);
        return satisfiedAPs.contains(expectedAP);
    }

    @Override
    public Set<ProgramState> getStates() {

        // This creates a combined view on both data structures storing
        // states without creating a new set first.
        // Note that the returned set cannot be modified without copying it
        // into a modifiable collection first.
        return new Set<ProgramState>() {


            @Override
            public int size() {

                return potentialMergeStates.size() + otherStates.size();
            }

            @Override
            public boolean isEmpty() {

                return potentialMergeStates.isEmpty() && otherStates.isEmpty();
            }

            @Override
            public boolean contains(Object o) {

                return potentialMergeStates.containsKey(o)
                        || otherStates.contains(o);
            }

            @Override
            public Iterator<ProgramState> iterator() {

                Iterator<ProgramState> mergerIter = potentialMergeStates.keySet().iterator();
                Iterator<ProgramState> otherIter = otherStates.iterator();

                return new Iterator<ProgramState>() {
                    @Override
                    public boolean hasNext() {

                        return mergerIter.hasNext() || otherIter.hasNext();
                    }

                    @Override
                    public ProgramState next() {

                        if (mergerIter.hasNext()) {
                            return mergerIter.next();
                        }
                        return otherIter.next();
                    }
                };
            }

            @Deprecated
            @Override
            public Object[] toArray() {

                return new Object[0];
            }

            @Deprecated
            @Override
            public <T> T[] toArray(T[] ts) {

                return ts;
            }

            @Deprecated
            @Override
            public boolean add(ProgramState programState) {

                return false;
            }

            @Deprecated
            @Override
            public boolean remove(Object o) {

                return false;
            }

            @Deprecated
            @Override
            public boolean containsAll(Collection<?> collection) {

                return false;
            }

            @Deprecated
            @Override
            public boolean addAll(Collection<? extends ProgramState> collection) {

                return false;
            }

            @Deprecated
            @Override
            public boolean retainAll(Collection<?> collection) {

                return false;
            }

            @Deprecated
            @Override
            public boolean removeAll(Collection<?> collection) {

                return false;
            }

            @Deprecated
            @Override
            public void clear() {

            }
        };
    }

}
