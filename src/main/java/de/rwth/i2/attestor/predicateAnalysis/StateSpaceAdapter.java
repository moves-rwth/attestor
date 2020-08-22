package de.rwth.i2.attestor.predicateAnalysis;

import de.rwth.i2.attestor.dataFlowAnalysis.FlowImpl;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.internal.HeapTransformation;
import de.rwth.i2.attestor.phases.symbolicExecution.stateSpaceGenerationImpl.TAStateSpace;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.GotoStmt;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.IfStmt;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StateSpaceAdapter {
    private final TAStateSpace stateSpace;
    private final Program program;
    private final Set<Integer> materialized = new HashSet<>();
    private final Set<Integer> criticalLabels = new HashSet<>();
    private final FlowImpl flow = new FlowImpl();
    private final Map<Integer, Set<Integer>> reachableFragment = new HashMap<>();

    public StateSpaceAdapter(StateSpace stateSpace, Program program) {

        if (!(stateSpace instanceof TAStateSpace)) {
            throw new IllegalArgumentException("StateSpaceAdapter only supports TAProgramStates");
        }

        this.stateSpace = (TAStateSpace) stateSpace;
        this.program = program;

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

        Map<Integer, Set<Integer>> common = flow
                .getLabels()
                .stream()
                .collect(Collectors.toMap(Function.identity(), l -> criticalLabels
                        .stream()
                        .filter(c -> reachableStates(c).contains(l))
                        .collect(Collectors.toSet())))
                .entrySet()
                .stream()
                .filter(e -> reachableStates(e.getKey()).containsAll(e.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        HashSet<Integer> oldCriticalLabels = new HashSet<>(criticalLabels);
        criticalLabels.clear();
        criticalLabels.addAll(oldCriticalLabels
                .stream()
                .map(l -> {
                    for (Map.Entry<Integer, Set<Integer>> e : common.entrySet()) {
                        if (e.getValue().contains(l)) {
                            return e.getKey();
                        }
                    }
                    return l;
                })
                .collect(Collectors.toSet()));

        criticalLabels.removeIf(s -> stateSpace.getState(s).getHeap().countNonterminalEdges() <= 0);
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
        return stateSpace.getTransformationQueue(from, to);
    }

    public Matching getMerger(int from, int to) {
        return stateSpace.getMerger(from, to);
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
        boolean criticalStatement =
                (program.getStatement(state.getProgramCounter()) instanceof GotoStmt
                        || program.getStatement(state.getProgramCounter()) instanceof IfStmt)
                        && !stateSpace.getFinalStateIds().contains(id);
        boolean onCycle = reachableStates(id).contains(id);

        if (criticalStatement && onCycle) {
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
