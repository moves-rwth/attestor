package de.rwth.i2.attestor.dataFlowAnalysis.predicate;

import de.rwth.i2.attestor.dataFlowAnalysis.FlowImpl;
import de.rwth.i2.attestor.graph.heap.internal.HeapTransformation;
import de.rwth.i2.attestor.phases.symbolicExecution.stateSpaceGenerationImpl.TAStateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import gnu.trove.TCollections;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.Queue;
import java.util.Set;

public class StateSpaceAdapter {
    private final TAStateSpace stateSpace;
    private final Program program;
    private final Set<Class<? extends SemanticsCommand>> criticalCommands;

    private final TIntSet materialized = new TIntHashSet();
    private final TIntSet criticalLabels = new TIntHashSet();
    private final FlowImpl flow = new FlowImpl();

    public StateSpaceAdapter(StateSpace stateSpace, Program program,
                             Set<Class<? extends SemanticsCommand>> criticalCommands) {

        if (!(stateSpace instanceof TAStateSpace)) {
            throw new IllegalArgumentException("StateSpaceAdapter only supports TAProgramStates");
        }

        this.stateSpace = (TAStateSpace) stateSpace;
        this.program = program;
        this.criticalCommands = criticalCommands;

        stateSpace.getStates().forEach(state -> {
            int current = state.getStateSpaceId();

            if (stateSpace.getInitialStateIds().contains(current)) {
                checkCritical(current);
            }

            stateSpace.getControlFlowSuccessorsIdsOf(current).forEach(successor -> {
                flow.add(current, successor);
                checkCritical(successor);

                return true;
            });

            stateSpace.getMaterializationSuccessorsIdsOf(current).forEach(successor -> {
                flow.add(current, successor);
                checkCritical(successor);
                materialized.add(successor);

                return true;
            });
        });
    }

    public FlowImpl getFlow() {
        return flow;
    }

    public Set<ProgramState> getStates() {
        return stateSpace.getStates();
    }

    public ProgramState getState(int label) {
        return stateSpace.getState(label);
    }

    public boolean isMaterialized(int label) {
        return materialized.contains(stateSpace.getState(label).getStateSpaceId());
    }

    public TIntSet getCriticalLabels() {
        return TCollections.unmodifiableSet(criticalLabels);
    }

    public Queue<HeapTransformation> getTransformationBuffer(int from, int to) {
        return stateSpace.getTransformationBuffer(stateSpace.getState(from), stateSpace.getState(to));
    }

    private void checkCritical(int id) {
        ProgramState state = stateSpace.getState(id);
        boolean critical = criticalCommands.stream()
                .anyMatch(clazz -> clazz.isInstance(program.getStatement(state.getProgramCounter())));

        if (critical) {
            criticalLabels.add(state.getStateSpaceId());
        }
    }
}
