package de.rwth.i2.attestor.predicateAnalysis;

import de.rwth.i2.attestor.dataFlowAnalysis.DataFlowAnalysis;
import de.rwth.i2.attestor.dataFlowAnalysis.Flow;
import de.rwth.i2.attestor.dataFlowAnalysis.UntangledFlow;
import de.rwth.i2.attestor.dataFlowAnalysis.WideningOperator;
import de.rwth.i2.attestor.domain.*;
import de.rwth.i2.attestor.domain.AssignMapping;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.internal.HeapTransformation;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PredicateAnalysis<I extends RelativeIndex<?>> implements DataFlowAnalysis<AssignMapping<I>> {
    private final int extremalLabel;
    private final UntangledFlow flow;
    private final StateSpaceAdapter stateSpaceAdapter;

    private final RelativeIndexOp<?, I> indexOp;
    private final ThresholdWidening<I> wideningOperator;
    private final IndexAbstractionRule<I> indexAbstractionRule;

    private final Set<Integer> keySet;
    private final Lattice<AssignMapping<I>> domainOp;

    public PredicateAnalysis(
            Integer extremalLabel,
            StateSpaceAdapter stateSpaceAdapter,
            RelativeIndexOp<?, I> indexOp,
            IndexAbstractionRule<I> indexAbstractionRule) {

        TIntSet TIntkeySet = new TIntHashSet();
        for (ProgramState state : stateSpaceAdapter.getStates()) {
            TIntkeySet.addAll(state.getHeap().nonterminalEdges());
        }

        this.extremalLabel = extremalLabel;
        this.stateSpaceAdapter = stateSpaceAdapter;
        this.indexOp = indexOp;
        this.indexAbstractionRule = indexAbstractionRule;

        keySet = Arrays.stream(TIntkeySet.toArray()).boxed().collect(Collectors.toSet());
        domainOp = new MappingOp<>(AssignMapping::new, keySet, indexOp);
        flow = new UntangledFlow(stateSpaceAdapter.getFlow(), extremalLabel);
        wideningOperator = new ThresholdWidening<>(indexOp.greatestElement(), indexOp);
    }

    @Override
    public Flow getFlow() {
        return flow;
    }

    @Override
    public Lattice<AssignMapping<I>> getLattice() {
        return domainOp;
    }

    @Override
    public AssignMapping<I> getExtremalValue() {
        AssignMapping<I> result = new AssignMapping<>();

        for (Integer key : keySet) {
            if (stateSpaceAdapter.getState(extremalLabel).getHeap().nonterminalEdges().contains(key)) {
                MappingOp.assign(result, key, indexOp.getVariable());
            } else {
                MappingOp.assign(result, key, indexOp.leastElement());
            }
        }

        return result;
    }

    @Override
    public Set<Integer> getExtremalLabels() {
        return Collections.singleton(extremalLabel);
    }

    @Override
    public Function<AssignMapping<I>, AssignMapping<I>>
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
    public WideningOperator<AssignMapping<I>> getWideningOperator() {
        return elements -> {
            AssignMapping<I> result = new AssignMapping<>();

            for (Integer key : keySet) {
                MappingOp.assign(result, key,
                        wideningOperator.widen(elements.stream().map(a -> a.get(key)).collect(Collectors.toSet())));
            }

            return result;
        };
    }

    public Function<AssignMapping<I>, AssignMapping<I>>
    generateMaterializationTransferFunction(Queue<HeapTransformation> transformationBuffer, Matching merger) {

        return assign -> {
            final AssignMapping<I> result = new AssignMapping<>(assign);

            while (!transformationBuffer.isEmpty()) {
                HeapTransformation step = transformationBuffer.remove();

                // apply rule
                Map<Integer, I> fragment = indexAbstractionRule.abstractForward(
                        result.get(step.getNtEdge()), step.getLabel(), step.getRule());

                // map result from rule to actual heap
                Map<Integer, I> matchedFragment = new HashMap<>();
                for (Map.Entry<Integer, I> entry : fragment.entrySet()) {
                    matchedFragment.put(step.ruleToHeap(entry.getKey()), entry.getValue());
                }

                // update assign AssignMapping
                MappingOp.assign(result, step.getNtEdge(), null);
                for (Map.Entry<Integer, I> entry : matchedFragment.entrySet()) {
                    MappingOp.assign(result, entry.getKey(), entry.getValue());
                }
            }

            finalizeResult(merger, result);

            return result;
        };
    }

    public Function<AssignMapping<I>, AssignMapping<I>>
    generateAbstractionTransferFunction(Queue<HeapTransformation> transformationBuffer, Matching merger) {

        return assign -> {
            final AssignMapping<I> result = new AssignMapping<>(assign);

            while (!transformationBuffer.isEmpty()) {
                HeapTransformation step = transformationBuffer.remove();

                // map current value from actual heap to rule
                Map<Integer, I> fragment = new HashMap<>();
                step.getRule().nonterminalEdges().forEach(nt -> {
                    fragment.put(nt, result.get(step.ruleToHeap(nt)));
                    return true;
                });

                // apply rule
                I newIndex = indexAbstractionRule.abstractBackward(
                        fragment, step.getLabel(), step.getRule());

                // update assign AssignMapping
                for (Integer nt : fragment.keySet()) {
                    MappingOp.assign(result, step.ruleToHeap(nt), null);
                }

                MappingOp.assign(result, step.getNtEdge(), newIndex);
            }

            finalizeResult(merger, result);

            return result;
        };
    }

    private void finalizeResult(Matching merger, AssignMapping<I> result) {
        if (merger != null) {
            merger.pattern().nonterminalEdges().forEach(nt -> {
                I value = result.get(nt);
                MappingOp.assign(result, nt, null);
                MappingOp.assign(result, merger.match(nt), value);
                return true;
            });
        }

        for (Integer key : keySet) {
            if (!MappingOp.contains(result, key)) {
                MappingOp.assign(result, key, indexOp.leastElement());
            }
        }
    }
}
