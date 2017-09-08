package de.rwth.i2.attestor.stateSpaceGeneration;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.IntPredicate;

public class InternalStateSpace implements StateSpace {

    private final static Logger logger = LogManager.getLogger("InternalStateSpace");

    private Map<ProgramState, ProgramState> potentialMergeStates;
    private List<ProgramState> otherStates; // states that are never checked for isomorphism
    private Set<ProgramState> initialStates;

    private TIntSet finalStateIds;

    private TIntObjectMap<TIntArrayList> materializationSuccessors;
    private TIntObjectMap<TIntArrayList> controlFlowSuccessors;
    private int nextStateId = 0;
    private int maximalStateSize = 0;

    public InternalStateSpace(int capacity) {

        potentialMergeStates = new HashMap<>(capacity);
        otherStates = new ArrayList<>(capacity);
        initialStates = new HashSet<>(100);
        finalStateIds = new TIntHashSet(100);
        materializationSuccessors = new TIntObjectHashMap<>(capacity);
        controlFlowSuccessors = new TIntObjectHashMap<>(capacity);
    }

    public Set<ProgramState> getInitialStates() {

        return Collections.unmodifiableSet(initialStates);
    }

    @Override
    public Set<ProgramState> getFinalStates() {

        return filterStates(
                id -> finalStateIds.contains(id),
                finalStateIds.size()
        );
    }

    private Set<ProgramState> filterStates(IntPredicate filter, int size) {

        Set<ProgramState> result = new HashSet<>(size);

        if(size == 0) {
            return result;
        }

        for(ProgramState s : potentialMergeStates.keySet()) {
            if(filter.test(s.getStateSpaceId())) {
                result.add(s);
            }
            if(result.size() == size) {
                return result;
            }
        }
        for(ProgramState s : otherStates) {
            if(filter.test(s.getStateSpaceId())) {
                result.add(s);
            }
            if(result.size() == size) {
                return result;
            }
        }

        logger.warn("Not all successor state IDs could be found");
        return result;
    }

    @Override
    public Set<ProgramState> getControlFlowSuccessorsOf(ProgramState state) {

        int stateSpaceId = state.getStateSpaceId();
        TIntArrayList successors = controlFlowSuccessors.get(stateSpaceId);


        if(successors.isEmpty()) {
            return Collections.emptySet();
        }

        return filterStates(
            successors::contains,
            successors.size()
        );
    }

    @Override
    public Set<ProgramState> getMaterializationSuccessorsOf(ProgramState state) {

        int stateSpaceId = state.getStateSpaceId();
        TIntArrayList successors = materializationSuccessors.get(stateSpaceId);

        if(successors.isEmpty()) {
            return Collections.emptySet();
        }

        return filterStates(
                successors::contains,
                successors.size()
        );
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
        if(old == null) {
            updateAddedState(state);
            return true;
        } else {
            state.setStateSpaceId( old.getStateSpaceId() );
        }
        return false;
    }

    private void updateAddedState(ProgramState state) {
        state.setStateSpaceId(nextStateId);
        materializationSuccessors.put(nextStateId, new TIntArrayList());
        controlFlowSuccessors.put(nextStateId, new TIntArrayList());
        maximalStateSize = Math.max(maximalStateSize, state.getSize());
        ++nextStateId;
    }

    @Override
    public void addInitialState(ProgramState state) {
        addStateIfAbsent(state);
        initialStates.add(state);
    }

    @Override
    public void setFinal(ProgramState state) {
        finalStateIds.add(state.getStateSpaceId());
    }

    @Override
    public void addMaterializationTransition(ProgramState from, ProgramState to) {

        addTransition(from, to, materializationSuccessors);
    }

    @Override
    public void addControlFlowTransition(ProgramState from, ProgramState to) {

        addTransition(from, to, controlFlowSuccessors);
    }

    private void addTransition(ProgramState from, ProgramState to, TIntObjectMap<TIntArrayList> successors) {

        int fId = from.getStateSpaceId();
        int tId = to.getStateSpaceId();

        TIntArrayList succ = successors.get(fId);
        if(!succ.contains(tId)) {
           succ.add(tId);
        }
    }

    @Override
    public int getMaximalStateSize() {

        return maximalStateSize;
    }

    @Override
    public Set<ProgramState> getStates() {

        // This creates a combined view on both data structures storing
        // states without creating a new set first.
        // Note that the returned set cannot be modified without copying it
        // into a modifiable collection first.
        return new Set<ProgramState>() {

            private Iterator<ProgramState> mergerIter = potentialMergeStates.keySet().iterator();
            private Iterator<ProgramState> otherIter = otherStates.iterator();

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

                return new Iterator<ProgramState>() {
                    @Override
                    public boolean hasNext() {

                        return mergerIter.hasNext() || otherIter.hasNext();
                    }

                    @Override
                    public ProgramState next() {

                        if(mergerIter.hasNext()) {
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

                return null;
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
