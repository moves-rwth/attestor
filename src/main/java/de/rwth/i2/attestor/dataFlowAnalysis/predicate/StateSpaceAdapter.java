package de.rwth.i2.attestor.dataFlowAnalysis.predicate;

import de.rwth.i2.attestor.dataFlowAnalysis.FlowImpl;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Set;

public class StateSpaceAdapter implements Iterable<TAProgramState> {
    private final StateSpace stateSpace;
    private final Program program;
    private final Set<Class<? extends SemanticsCommand>> criticalCommands;
    private final TIntObjectMap<TAProgramState> labelToStateMap = new TIntObjectHashMap<>();

    private final TIntSet criticalLabels = new TIntHashSet();
    private final FlowImpl flow = new FlowImpl();

    public StateSpaceAdapter(StateSpace stateSpace, Program program,
                             Set<Class<? extends SemanticsCommand>> criticalCommands) {

        this.stateSpace = stateSpace;
        this.program = program;
        this.criticalCommands = criticalCommands;

        stateSpace.getStates().forEach(state -> {
            int current = state.getStateSpaceId();

            if (stateSpace.getInitialStateIds().contains(current)) {
                addState(current, false);
            }

            stateSpace.getControlFlowSuccessorsIdsOf(current).forEach(successor -> {
                flow.add(current, successor);
                addState(successor, false);

                return true;
            });

            stateSpace.getMaterializationSuccessorsIdsOf(current).forEach(successor -> {
                flow.add(current, successor);
                addState(successor, true);

                return true;
            });
        });
    }

    @Nonnull
    @Override
    public Iterator<TAProgramState> iterator() {
        return labelToStateMap.valueCollection().iterator();
    }

    public FlowImpl getFlow() {
        return flow;
    }

    public TAProgramState getState(int label) {
        return labelToStateMap.get(label);
    }

    public TIntSet getCriticalLabels() {
        return TCollections.unmodifiableSet(criticalLabels);
    }


    private void addState(int id, boolean isMaterialized) {
        ProgramState state = stateSpace.getState(id);
        boolean critical = criticalCommands.stream()
                .anyMatch(clazz -> clazz.isInstance(program.getStatement(state.getProgramCounter())));

        if (critical) {
            criticalLabels.add(state.getStateSpaceId());
        }

        labelToStateMap.put(state.getStateSpaceId(), new TAProgramState(isMaterialized, state.getHeap()));
    }
}
