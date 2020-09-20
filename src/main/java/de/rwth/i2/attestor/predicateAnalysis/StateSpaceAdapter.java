package de.rwth.i2.attestor.predicateAnalysis;

import de.rwth.i2.attestor.dataFlowAnalysis.GraphFlow;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.internal.HeapTransformation;
import de.rwth.i2.attestor.phases.symbolicExecution.stateSpaceGenerationImpl.TAStateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;

import java.util.*;
import java.util.stream.Collectors;

public class StateSpaceAdapter {
    private final TAStateSpace stateSpace;
    private final Set<Integer> materialized = new HashSet<>();
    private final Set<Integer> criticalLabels = new HashSet<>();
    private final GraphFlow flow = new GraphFlow();

    public StateSpaceAdapter(StateSpace stateSpace) {

        if (!(stateSpace instanceof TAStateSpace)) {
            throw new IllegalArgumentException("StateSpaceAdapter only supports TAProgramStates");
        }

        this.stateSpace = (TAStateSpace) stateSpace;

        // populate flow
        this.stateSpace.getStates().forEach(state -> {
            int current = state.getStateSpaceId();

            this.stateSpace.getControlFlowSuccessorsIdsOf(current).forEach(successor -> {
                flow.add(current, successor);
                return true;
            });

            this.stateSpace.getMaterializationSuccessorsIdsOf(current).forEach(successor -> {
                flow.add(current, successor);
                materialized.add(successor);
                return true;
            });
        });

        // compute critical states
        Set<Set<Integer>> circuits = flow.getCircuits().stream().map(HashSet::new).collect(Collectors.toSet());

        for (Set<Integer> circuit : circuits) {
            Map<Long, Set<Integer>> ranking = new HashMap<>();
            for (Integer candidate : circuit) {
                long rank = circuits.stream().filter(c -> c.contains(candidate)).count();
                ranking.putIfAbsent(rank, new HashSet<>());
                ranking.get(rank).add(candidate);
            }

            Set<Integer> candidates = ranking.entrySet()
                    .stream()
                    .filter(e -> !e.getValue().isEmpty())
                    .max(Map.Entry.comparingByKey())
                    .map(Map.Entry::getValue)
                    .orElse(Collections.emptySet());

            if (Collections.disjoint(criticalLabels, candidates)) {
                candidates.stream().findAny().ifPresent(criticalLabels::add);
            }
        }

        criticalLabels.removeIf(s -> this.stateSpace.getState(s).getHeap().countNonterminalEdges() <= 0);
    }

    public GraphFlow getFlow() {
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
}
