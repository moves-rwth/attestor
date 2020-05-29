package de.rwth.i2.attestor.dataFlowAnalysis.predicate;

import de.rwth.i2.attestor.dataFlowAnalysis.DataFlowAnalysis;
import de.rwth.i2.attestor.dataFlowAnalysis.Flow;
import de.rwth.i2.attestor.dataFlowAnalysis.UntangledFlow;
import de.rwth.i2.attestor.domain.AssignMapping;
import de.rwth.i2.attestor.domain.Lattice;
import de.rwth.i2.attestor.domain.Mapping;
import de.rwth.i2.attestor.domain.RelativeIndex;
import de.rwth.i2.attestor.graph.heap.internal.TAHeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.TransformationStep;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.Collections;
import java.util.Queue;
import java.util.function.Function;


public class PredicateAnalysis<I> implements DataFlowAnalysis<Mapping<Integer, RelativeIndex<I>>> {
    private final UntangledFlow flow;
    private final int extremalLabel;
    private final Mapping.MappingSet<Integer, RelativeIndex<I>> domainOp;
    private final IndexAbstractionRule<RelativeIndex<I>> indexAbstractionRule;
    private final StateSpaceAdapter stateSpaceAdapter;

    public PredicateAnalysis(
            Integer extremalLabel,
            StateSpaceAdapter adapter,
            RelativeIndex.RelativeIndexSet<I> indexOp,
            IndexAbstractionRule<RelativeIndex<I>> indexAbstractionRule) {

        this.extremalLabel = extremalLabel;
        this.indexAbstractionRule = indexAbstractionRule;

        this.stateSpaceAdapter = adapter;
        this.flow = new UntangledFlow(adapter.getFlow(), extremalLabel);
        this.domainOp = new Mapping.MappingSet<>(indexOp);
    }

    @Override
    public Flow getFlow() {
        return flow;
    }

    @Override
    public Lattice<Mapping<Integer, RelativeIndex<I>>> getLattice() {
        return domainOp;
    }

    @Override
    public Mapping<Integer, RelativeIndex<I>> getExtremalValue() {
        return new AssignMapping<Integer, RelativeIndex<I>>() {
            @Override
            public RelativeIndex<I> apply(Integer i) {
                if (super.apply(i) == null) {
                    assign(i, RelativeIndex.getVariable());
                }

                return super.apply(i);
            }
        };
    }

    @Override
    public TIntSet getExtremalLabels() {
        return new TIntHashSet(Collections.singleton(extremalLabel));
    }

    @Override
    public Function<Mapping<Integer, RelativeIndex<I>>, Mapping<Integer, RelativeIndex<I>>>
    getTransferFunction(int from, int to) {
        TAProgramState state = stateSpaceAdapter.getState(to == flow.copy ? flow.untangled : to);

        if (state.isMaterialized) {
            return generateMaterializationTransferFunction(state.heap);
        } else {
            return generateAbstractionTransferFunction(state.heap);
        }
    }

    private Function<Mapping<Integer, RelativeIndex<I>>, Mapping<Integer, RelativeIndex<I>>>
    generateMaterializationTransferFunction(TAHeapConfiguration heap) {

        final Queue<TransformationStep> history = heap.transformationHistory;

        return assign -> {
            AssignMapping<Integer, RelativeIndex<I>> result = new AssignMapping<>(assign);

            while (!history.isEmpty()) {
                TransformationStep step = history.remove();

                TIntObjectMap<RelativeIndex<I>> fragment = indexAbstractionRule.abstractForward(
                        result.apply(step.getNtEdge()), step.getLabel(), step.getRule());

                // map result from rule to actual heap
                TIntObjectHashMap<RelativeIndex<I>> matchedFragment = new TIntObjectHashMap<>();
                fragment.forEachEntry((key, value) -> {
                    matchedFragment.put(step.match(key), value);
                    return true;
                });

                // update assign mapping
                matchedFragment.forEachEntry((key, value) -> {
                    result.assign(key, value);
                    return true;
                });
            }

            return result;
        };
    }

    private Function<Mapping<Integer, RelativeIndex<I>>, Mapping<Integer, RelativeIndex<I>>>
    generateAbstractionTransferFunction(TAHeapConfiguration heap) {

        final Queue<TransformationStep> history = heap.transformationHistory;

        return assign -> {
            AssignMapping<Integer, RelativeIndex<I>> result = new AssignMapping<>(assign);

            while (!history.isEmpty()) {
                TransformationStep step = history.remove();

                // map current value from actual heap to rule
                TIntObjectHashMap<RelativeIndex<I>> fragment = new TIntObjectHashMap<>();
                for (Integer key : result) {
                    step.getRule().nonterminalEdges().forEach(nt -> {
                        if (step.match(nt) == key) {
                            fragment.put(nt, result.apply(key));
                            return false;
                        }

                        return true;
                    });
                }

                RelativeIndex<I> newIndex = indexAbstractionRule.abstractBackward(
                        fragment, step.getLabel(), step.getRule());

                // update assign mapping
                result.assign(step.getNtEdge(), newIndex);
            }

            return result;
        };
    }
}
