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

    private Map<ProgramState, ProgramState> allStates;
    private Set<ProgramState> initialStates;

    private TIntSet finalStateIds;

    private TIntObjectMap<TIntArrayList> materializationSuccessors;
    private TIntObjectMap<TIntArrayList> controlFlowSuccessors;
    private int nextStateId = 0;
    private int maximalStateSize = 0;

    public InternalStateSpace(int capacity) {

        allStates = new HashMap<>(capacity);
        initialStates = new HashSet<>(100);
        finalStateIds = new TIntHashSet(100);
        materializationSuccessors = new TIntObjectHashMap<>(capacity);
        controlFlowSuccessors = new TIntObjectHashMap<>(capacity);
    }

    @Override
    public boolean contains(ProgramState state) {

        return allStates.containsKey(state);
    }

    @Override
    public Set<ProgramState> getStates() {

        return allStates.keySet();
    }

    @Override
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

        for(ProgramState s : allStates.keySet()) {
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
    public boolean addStateIfAbsent(ProgramState state) {

        ProgramState old = allStates.putIfAbsent(state, state);
        if(old == null) {
            state.setStateSpaceId(nextStateId);
            materializationSuccessors.put(nextStateId, new TIntArrayList());
            controlFlowSuccessors.put(nextStateId, new TIntArrayList());
            maximalStateSize = Math.max(maximalStateSize, state.getSize());
            ++nextStateId;
            return true;
        } else {
            state.setStateSpaceId( old.getStateSpaceId() );
        }
        return false;
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

        int fId = from.getStateSpaceId();
        int tId = to.getStateSpaceId();

        materializationSuccessors.get(fId).add(tId);
    }

    @Override
    public void addControlFlowTransition(ProgramState from, ProgramState to) {

        int fId = from.getStateSpaceId();
        int tId = to.getStateSpaceId();

        controlFlowSuccessors.get(fId).add(tId);
    }

    @Override
    public int getMaximalStateSize() {

        return maximalStateSize;
    }
}
