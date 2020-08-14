package de.rwth.i2.attestor.predicateAnalysis;

import de.rwth.i2.attestor.dataFlowAnalysis.FlowImpl;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.internal.HeapTransformation;
import de.rwth.i2.attestor.phases.symbolicExecution.stateSpaceGenerationImpl.TAStateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.SemanticsCommand;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;

import java.util.*;

public class StateSpaceAdapter {
    private final TAStateSpace stateSpace;
    private final Program program;
    private final Set<Class<? extends SemanticsCommand>> criticalCommands;

    private final Set<Integer> materialized = new HashSet<>();
    private final Set<Integer> criticalLabels = new HashSet<>();
    private final FlowImpl flow = new FlowImpl();
    private final Map<Integer, Set<Integer>> reachableFragment = new HashMap<>();

    public StateSpaceAdapter(StateSpace stateSpace, Program program,
                             Set<Class<? extends SemanticsCommand>> criticalCommands) {

        if (!(stateSpace instanceof TAStateSpace)) {
            throw new IllegalArgumentException("StateSpaceAdapter only supports TAProgramStates");
        }

        this.stateSpace = (TAStateSpace) stateSpace;
        this.program = program;
        this.criticalCommands = Collections.unmodifiableSet(criticalCommands);

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

    public Set<Integer> getCriticalLabels() {
        return Collections.unmodifiableSet(criticalLabels);
    }

    public Deque<HeapTransformation> getTransformationQueue(int from, int to) {
        return stateSpace.getTransformationQueue(stateSpace.getState(from), stateSpace.getState(to));
    }

    public Matching getMerger(int from, int to) {
        return stateSpace.getMerger(stateSpace.getState(from), stateSpace.getState(to));
    }

    public Set<Integer> reachableStates(int id) {
        reachableFragment.computeIfAbsent(id, k -> {
            Set<Integer> reachable = new HashSet<>();
            reachableRec(id, reachable);
            return reachable;
        });

        return Collections.unmodifiableSet(reachableFragment.get(id));
    }

    private void checkCritical(int id) {
        ProgramState state = stateSpace.getState(id);
        boolean criticalStatement = criticalCommands.stream()
                .anyMatch(clazz -> clazz.isInstance(program.getStatement(state.getProgramCounter())));
        boolean hasNonterminal = state.getHeap().countNonterminalEdges() > 0;
        boolean onCycle = reachableStates(id).contains(id);

        if (criticalStatement && hasNonterminal && onCycle) {
            criticalLabels.add(id);
        }
    }

    private void reachableRec(int id, Set<Integer> accumulator) {
        stateSpace.getMaterializationSuccessorsIdsOf(id).forEach(successor -> {
            if (!accumulator.contains(successor)) {
                accumulator.add(successor);
                reachableRec(successor, accumulator);
            }

            return true;
        });

        stateSpace.getControlFlowSuccessorsIdsOf(id).forEach(successor -> {
            if (!accumulator.contains(successor)) {
                accumulator.add(successor);
                reachableRec(successor, accumulator);
            }

            return true;
        });
    }
}
