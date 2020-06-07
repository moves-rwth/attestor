package de.rwth.i2.attestor.dataFlowAnalysis.predicate;

import de.rwth.i2.attestor.dataFlowAnalysis.DataFlowAnalysis;
import de.rwth.i2.attestor.dataFlowAnalysis.Flow;
import de.rwth.i2.attestor.dataFlowAnalysis.UntangledFlow;
import de.rwth.i2.attestor.domain.AssignMapping;
import de.rwth.i2.attestor.domain.Lattice;
import de.rwth.i2.attestor.domain.RelativeIndex;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.HeapTransformation;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Queue;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;


public class PredicateAnalysis<I> implements DataFlowAnalysis<AssignMapping<Integer, RelativeIndex<I>>> {
    private final UntangledFlow flow;
    private final int extremalLabel;
    private final AssignMapping.MappingSet<Integer, RelativeIndex<I>> domainOp;
    private final RelativeIndex.RelativeIndexSet<I> indexOp;
    private final IndexAbstractionRule<RelativeIndex<I>> indexAbstractionRule;
    private final StateSpaceAdapter stateSpaceAdapter;
    private final Set<Integer> keySet;

    public PredicateAnalysis(
            Integer extremalLabel,
            StateSpaceAdapter adapter,
            RelativeIndex.RelativeIndexSet<I> indexOp,
            IndexAbstractionRule<RelativeIndex<I>> indexAbstractionRule) {

        this.extremalLabel = extremalLabel;
        this.indexAbstractionRule = indexAbstractionRule;

        this.stateSpaceAdapter = adapter;
        this.flow = new UntangledFlow(adapter.getFlow(), extremalLabel);

        TIntSet keySet = new TIntHashSet();
        for (ProgramState state : adapter.getStates()) {
            keySet.addAll(state.getHeap().nonterminalEdges());
        }

        this.keySet = Arrays.stream(keySet.toArray()).boxed().collect(Collectors.toSet());
        this.indexOp = indexOp;
        this.domainOp = new AssignMapping.MappingSet<>(this.keySet, indexOp);
    }

    @Override
    public Flow getFlow() {
        return flow;
    }

    @Override
    public Lattice<AssignMapping<Integer, RelativeIndex<I>>> getLattice() {
        return domainOp;
    }

    @Override
    public AssignMapping<Integer, RelativeIndex<I>> getExtremalValue() {
        AssignMapping<Integer, RelativeIndex<I>> result = new AssignMapping<>(keySet);

        for (Integer key : keySet) {
            if (stateSpaceAdapter.getState(extremalLabel).getHeap().nonterminalEdges().contains(key)) {
                result.put(key, RelativeIndex.getVariable());
            } else {
                result.put(key, indexOp.leastElement());
            }
        }

        return result;
    }

    @Override
    public Set<Integer> getExtremalLabels() {
        return Collections.singleton(extremalLabel);
    }

    @Override
    public Function<AssignMapping<Integer, RelativeIndex<I>>, AssignMapping<Integer, RelativeIndex<I>>>
    getTransferFunction(int from, int to) {
        int untangledTo = (to == flow.copy ? flow.untangled : to);
        Queue<HeapTransformation> buffer = stateSpaceAdapter.getTransformationBuffer(from, untangledTo);
        HeapConfiguration heapFrom = stateSpaceAdapter.getState(from).getHeap();
        HeapConfiguration heapTo = stateSpaceAdapter.getState(untangledTo).getHeap();

        if (stateSpaceAdapter.isMaterialized(untangledTo)) {
            return generateMaterializationTransferFunction(heapFrom, heapTo, buffer);
        } else {
            return generateAbstractionTransferFunction(heapFrom, heapTo, buffer);
        }
    }

    public Function<AssignMapping<Integer, RelativeIndex<I>>, AssignMapping<Integer, RelativeIndex<I>>>
    generateMaterializationTransferFunction(HeapConfiguration heapFrom, HeapConfiguration heapTo, Queue<HeapTransformation> transformationBuffer) {

        return assign -> {
            AssignMapping<Integer, RelativeIndex<I>> result = new AssignMapping<>(assign);

            while (!transformationBuffer.isEmpty()) {
                HeapTransformation step = transformationBuffer.remove();

                Map<Integer, RelativeIndex<I>> fragment = indexAbstractionRule.abstractForward(
                        result.get(step.getNtEdge()), step.getLabel(), step.getRule());

                // map result from rule to actual heap
                Map<Integer, RelativeIndex<I>> matchedFragment = new HashMap<>();

                for (Map.Entry<Integer, RelativeIndex<I>> entry : fragment.entrySet()) {
                    matchedFragment.put(step.ruleToHeap(entry.getKey()), entry.getValue());
                }

                // update assign AssignMapping
                for (Map.Entry<Integer, RelativeIndex<I>> entry : matchedFragment.entrySet()) {
                    result.assign(entry.getKey(), entry.getValue());
                }
            }

            for (Integer key : result.keySet()) {
                if (!heapTo.nonterminalEdges().contains(key)) {
                    result.put(key, indexOp.leastElement());
                }
            }

            return result;
        };
    }

    public Function<AssignMapping<Integer, RelativeIndex<I>>, AssignMapping<Integer, RelativeIndex<I>>>
    generateAbstractionTransferFunction(HeapConfiguration heapFrom, HeapConfiguration heapTo, Queue<HeapTransformation> transformationBuffer) {

        return assign -> {
            AssignMapping<Integer, RelativeIndex<I>> result = new AssignMapping<>(assign);
            TIntSet nonterminals = new TIntHashSet(heapFrom.nonterminalEdges());

            while (!transformationBuffer.isEmpty()) {
                HeapTransformation step = transformationBuffer.remove();
                // map current value from actual heap to rule
                Map<Integer, RelativeIndex<I>> fragment = new HashMap<>();
                nonterminals.forEach(nt -> {
                    int htr = step.heapToRule(nt);

                    if (htr != -1) {
                        fragment.put(step.heapToRule(nt), result.get(nt));
                    }

                    return true;
                });

                RelativeIndex<I> newIndex = indexAbstractionRule.abstractBackward(
                        fragment, step.getLabel(), step.getRule());

                if (newIndex == null) {
                    continue;
                }

                // update assign AssignMapping
                result.assign(step.getNtEdge(), newIndex);
                nonterminals.add(step.getNtEdge());
            }

            for (Integer key : result.keySet()) {
                if (!heapTo.nonterminalEdges().contains(key)) {
                    result.put(key, indexOp.leastElement());
                }
            }

            return result;
        };
    }
}
