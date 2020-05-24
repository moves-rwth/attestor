package de.rwth.i2.attestor.dataFlowAnalysis.predicate;

import de.rwth.i2.attestor.dataFlowAnalysis.FlowImpl;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Set;

public class StateSpaceAdapter {
    private final StateSpace stateSpace;
    private final Program program;
    private final Set<Class<? extends SemanticsCommand>> criticalCommands;
    private final TIntObjectMap<TAProgramState> labelToStateMap = new TIntObjectHashMap<>();
    private final FlowImpl flow = new FlowImpl();

    public FlowImpl getFlow() {
        return flow;
    }

    public TAProgramState getState(int label) {
        return labelToStateMap.get(label);
    }

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

            stateSpace.getMaterializationSuccessorsIdsOf(current).forEach(successor -> {
                addState(successor, true);
                flow.add(current, successor);

                return true;
            });

            stateSpace.getControlFlowSuccessorsIdsOf(current).forEach(successor -> {
                flow.add(current, successor);
                addState(successor, false);

                return true;
            });

        });
    }

    private void addState(int id, boolean isMaterialized) {
        ProgramState state = stateSpace.getState(id);
        boolean critical = criticalCommands
                .stream()
                .anyMatch(clazz -> clazz.isInstance(program.getStatement(state.getProgramCounter())));

        labelToStateMap.put(state.getStateSpaceId(), new TAProgramState(critical, isMaterialized, state.getHeap()));
    }
}
