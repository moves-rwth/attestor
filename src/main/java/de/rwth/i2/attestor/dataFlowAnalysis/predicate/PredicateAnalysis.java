package de.rwth.i2.attestor.dataFlowAnalysis.predicate;

import de.rwth.i2.attestor.dataFlowAnalysis.DataFlowAnalysis;
import de.rwth.i2.attestor.dataFlowAnalysis.Flow;
import de.rwth.i2.attestor.dataFlowAnalysis.UntangledFlow;
import de.rwth.i2.attestor.domain.AssignMapping;
import de.rwth.i2.attestor.domain.Lattice;
import de.rwth.i2.attestor.domain.RelativeIndex;
import de.rwth.i2.attestor.graph.heap.internal.TAHeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.TransformationStep;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


public class PredicateAnalysis<I> implements DataFlowAnalysis<AssignMapping<Integer, RelativeIndex<I>>> {
    private final UntangledFlow flow;
    private final int extremalLabel;
    private final AssignMapping.MappingSet<Integer, RelativeIndex<I>> domainOp;
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
        for (TAProgramState state : adapter) {
            keySet.addAll(state.heap.nonterminalEdges());
        }

        this.keySet = Arrays.stream(keySet.toArray()).boxed().collect(Collectors.toSet());
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
            result.put(key, RelativeIndex.getVariable());
        }

        return result;
    }

    @Override
    public TIntSet getExtremalLabels() {
        return new TIntHashSet(Collections.singleton(extremalLabel));
    }

    @Override
    public Function<AssignMapping<Integer, RelativeIndex<I>>, AssignMapping<Integer, RelativeIndex<I>>>
    getTransferFunction(int from, int to) {
        TAProgramState state = stateSpaceAdapter.getState(to == flow.copy ? flow.untangled : to);

        if (state.isMaterialized) {
            return generateMaterializationTransferFunction(state.heap);
        } else {
            return generateAbstractionTransferFunction(state.heap);
        }
    }

    public Function<AssignMapping<Integer, RelativeIndex<I>>, AssignMapping<Integer, RelativeIndex<I>>>
    generateMaterializationTransferFunction(TAHeapConfiguration heap) {

        final Queue<TransformationStep> history = heap.transformationHistory;

        return assign -> {
            AssignMapping<Integer, RelativeIndex<I>> result = new AssignMapping<>(assign);

            while (!history.isEmpty()) {
                TAHeapConfiguration h = heap;
                TransformationStep step = history.remove();

                TIntObjectMap<RelativeIndex<I>> fragment = indexAbstractionRule.abstractForward(
                        result.get(step.getNtEdge()), step.getLabel(), step.getRule());

                if (fragment == null) {
                    continue;
                }

                // map result from rule to actual heap
                TIntObjectHashMap<RelativeIndex<I>> matchedFragment = new TIntObjectHashMap<>();
                fragment.forEachEntry((key, value) -> {
                    matchedFragment.put(step.match(key), value);
                    return true;
                });


                // update assign AssignMapping
                matchedFragment.forEachEntry((key, value) -> {
                    result.assign(key, value);
                    return true;
                });
            }

            return result;
        };
    }

    public Function<AssignMapping<Integer, RelativeIndex<I>>, AssignMapping<Integer, RelativeIndex<I>>>
    generateAbstractionTransferFunction(TAHeapConfiguration heap) {

        final Queue<TransformationStep> history = heap.transformationHistory;

        return assign -> {
            AssignMapping<Integer, RelativeIndex<I>> result = new AssignMapping<>(assign);

            while (!history.isEmpty()) {
                TransformationStep step = history.remove();

                // map current value from actual heap to rule
                TIntObjectHashMap<RelativeIndex<I>> fragment = new TIntObjectHashMap<>();
                for (Integer key : result.keySet()) {
                    step.getRule().nonterminalEdges().forEach(nt -> {
                        if (step.match(nt) == key) {
                            fragment.put(nt, result.get(key));
                            return false;
                        }

                        return true;
                    });
                }

                RelativeIndex<I> newIndex = indexAbstractionRule.abstractBackward(
                        fragment, step.getLabel(), step.getRule());

                if (newIndex == null) {
                    continue;
                }

                // update assign AssignMapping
                result.assign(step.getNtEdge(), newIndex);
            }

            return result;
        };
    }
}
