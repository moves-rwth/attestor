package de.rwth.i2.attestor.phases.predicateAnalysis;

import de.rwth.i2.attestor.dataFlowAnalysis.DataFlowAnalysis;
import de.rwth.i2.attestor.dataFlowAnalysis.Flow;
import de.rwth.i2.attestor.dataFlowAnalysis.UntangledFlow;
import de.rwth.i2.attestor.dataFlowAnalysis.WideningOperator;
import de.rwth.i2.attestor.domain.AssignMapping;
import de.rwth.i2.attestor.domain.Lattice;
import de.rwth.i2.attestor.domain.RelativeIndex;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.internal.HeapTransformation;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PredicateAnalysis<I> implements DataFlowAnalysis<AssignMapping<Integer, RelativeIndex<I>>> {
    private final int extremalLabel;
    private final UntangledFlow flow;
    private final AssignMapping.MappingSet<Integer, RelativeIndex<I>> domainOp;
    private final RelativeIndex.RelativeIndexSet<I> indexOp;
    private final IndexAbstractionRule<RelativeIndex<I>> indexAbstractionRule;
    private final StateSpaceAdapter stateSpaceAdapter;
    private final Set<Integer> keySet;
    private final RelativeIndex<I> wideningThreshold;
    private final ThresholdWidening<RelativeIndex<I>> wideningOperator;

    public PredicateAnalysis(
            Integer extremalLabel,
            StateSpaceAdapter stateSpaceAdapter,
            RelativeIndex.RelativeIndexSet<I> indexOp,
            IndexAbstractionRule<RelativeIndex<I>> indexAbstractionRule,
            RelativeIndex<I> wideningThreshold) {

        this.extremalLabel = extremalLabel;
        this.stateSpaceAdapter = stateSpaceAdapter;
        this.indexOp = indexOp;
        this.indexAbstractionRule = indexAbstractionRule;
        this.wideningThreshold = wideningThreshold;

        TIntSet TIntkeySet = new TIntHashSet();
        for (ProgramState state : stateSpaceAdapter.getStates()) {
            TIntkeySet.addAll(state.getHeap().nonterminalEdges());
        }
        keySet = Arrays.stream(TIntkeySet.toArray()).boxed().collect(Collectors.toSet());
        domainOp = new AssignMapping.MappingSet<>(keySet, indexOp);
        flow = new UntangledFlow(stateSpaceAdapter.getFlow(), extremalLabel);
        wideningOperator = new ThresholdWidening<>(wideningThreshold, indexOp);
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
        AssignMapping<Integer, RelativeIndex<I>> result = new AssignMapping<>();

        for (Integer key : keySet) {
            if (stateSpaceAdapter.getState(extremalLabel).getHeap().nonterminalEdges().contains(key)) {
                result.assign(key, RelativeIndex.getVariable());
            } else {
                result.assign(key, indexOp.leastElement());
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
        Matching merger = stateSpaceAdapter.getMerger(from, untangledTo);
        Queue<HeapTransformation> buffer = stateSpaceAdapter.getTransformationBuffer(from, untangledTo);

        if (stateSpaceAdapter.isMaterialized(untangledTo)) {
            return generateMaterializationTransferFunction(buffer, merger);
        } else {
            return generateAbstractionTransferFunction(buffer, merger);
        }
    }

    @Override
    public WideningOperator<AssignMapping<Integer, RelativeIndex<I>>> getWideningOperator() {
        return elements -> {
            AssignMapping<Integer, RelativeIndex<I>> result = new AssignMapping<>();

            for (Integer key : keySet) {
                result.assign(key,
                        wideningOperator.widen(elements.stream().map(a -> a.get(key)).collect(Collectors.toSet())));
            }

            return result;
        };
    }

    public Function<AssignMapping<Integer, RelativeIndex<I>>, AssignMapping<Integer, RelativeIndex<I>>>
    generateMaterializationTransferFunction(Queue<HeapTransformation> transformationBuffer, Matching merger) {

        return assign -> {
            final AssignMapping<Integer, RelativeIndex<I>> result = new AssignMapping<>(assign);

            while (!transformationBuffer.isEmpty()) {
                HeapTransformation step = transformationBuffer.remove();

                // apply rule
                Map<Integer, RelativeIndex<I>> fragment = indexAbstractionRule.abstractForward(
                        result.get(step.getNtEdge()), step.getLabel(), step.getRule());

                // map result from rule to actual heap
                Map<Integer, RelativeIndex<I>> matchedFragment = new HashMap<>();
                for (Map.Entry<Integer, RelativeIndex<I>> entry : fragment.entrySet()) {
                    matchedFragment.put(step.ruleToHeap(entry.getKey()), entry.getValue());
                }

                // update assign AssignMapping
                result.assign(step.getNtEdge(), null);
                for (Map.Entry<Integer, RelativeIndex<I>> entry : matchedFragment.entrySet()) {
                    result.assign(entry.getKey(), entry.getValue());
                }
            }

            finalizeResult(merger, result);

            return result;
        };
    }

    public Function<AssignMapping<Integer, RelativeIndex<I>>, AssignMapping<Integer, RelativeIndex<I>>>
    generateAbstractionTransferFunction(Queue<HeapTransformation> transformationBuffer, Matching merger) {

        return assign -> {
            final AssignMapping<Integer, RelativeIndex<I>> result = new AssignMapping<>(assign);

            while (!transformationBuffer.isEmpty()) {
                HeapTransformation step = transformationBuffer.remove();

                // map current value from actual heap to rule
                Map<Integer, RelativeIndex<I>> fragment = new HashMap<>();
                step.getRule().nonterminalEdges().forEach(nt -> {
                    fragment.put(nt, result.get(step.ruleToHeap(nt)));
                    return true;
                });


                // apply rule
                RelativeIndex<I> newIndex = indexAbstractionRule.abstractBackward(
                        fragment, step.getLabel(), step.getRule());

                // update assign AssignMapping
                for (Integer nt : fragment.keySet()) {
                    result.assign(step.ruleToHeap(nt), null);
                }

                result.assign(step.getNtEdge(), newIndex);
            }

            finalizeResult(merger, result);

            return result;
        };
    }

    private void finalizeResult(Matching merger, AssignMapping<Integer, RelativeIndex<I>> result) {
        if (merger != null) {
            merger.pattern().nonterminalEdges().forEach(nt -> {
                RelativeIndex<I> value = result.get(nt);
                result.assign(nt, null);
                result.assign(merger.match(nt), value);
                return true;
            });
        }

        for (Integer key : keySet) {
            if (!result.contains(key)) {
                result.assign(key, indexOp.leastElement());
            }
        }
    }
}
