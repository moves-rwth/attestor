package de.rwth.i2.attestor.predicateAnalysis;

import de.rwth.i2.attestor.dataFlowAnalysis.DataFlowAnalysis;
import de.rwth.i2.attestor.dataFlowAnalysis.Flow;
import de.rwth.i2.attestor.dataFlowAnalysis.UntangledFlow;
import de.rwth.i2.attestor.dataFlowAnalysis.WideningOperator;
import de.rwth.i2.attestor.domain.*;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.internal.HeapTransformation;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PredicateAnalysis<I extends RelativeIndex<?>> implements DataFlowAnalysis<AssignMapping<I>> {
    private final int extremalLabel;
    private final UntangledFlow flow;
    private final StateSpaceAdapter stateSpaceAdapter;

    private final RelativeIndexOp<?, I> indexOp;
    private final ThresholdWidening<I> wideningOperator;
    private final AbstractionRule<I> abstractionRule;

    private final Set<Integer> keySet;
    private final MappingOp<I, AssignMapping<I>> domainOp;
    private final Supplier<AssignMapping<I>> assignSupplier;

    public PredicateAnalysis(
            Integer extremalLabel,
            StateSpaceAdapter stateSpaceAdapter,
            RelativeIndexOp<?, I> indexOp,
            AbstractionRule<I> abstractionRule,
            I wideningThreshold) {

        TIntSet TIntkeySet = new TIntHashSet();
        for (ProgramState state : stateSpaceAdapter.getStates()) {
            TIntkeySet.addAll(state.getHeap().nonterminalEdges());
        }

        this.extremalLabel = extremalLabel;
        this.stateSpaceAdapter = stateSpaceAdapter;
        this.indexOp = indexOp;
        this.abstractionRule = abstractionRule;


        keySet = Arrays.stream(TIntkeySet.toArray()).boxed().collect(Collectors.toSet());
        assignSupplier = () -> {
            AssignMapping<I> assign = new AssignMapping<>();
            for (Integer key : keySet) {
                assign.put(key, indexOp.leastElement());
            }

            return assign;
        };
        domainOp = new MappingOp<>(assignSupplier, indexOp);
        flow = new UntangledFlow(stateSpaceAdapter.getFlow(), extremalLabel);
        wideningOperator = new ThresholdWidening<>(wideningThreshold, indexOp);
    }

    public Integer getUntangled() {
        return flow.untangled;
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
        AssignMapping<I> result = assignSupplier.get();
        stateSpaceAdapter.getState(extremalLabel).getHeap().nonterminalEdges().forEach(i -> {
            result.assign(i, indexOp.getVariable());
            return true;
        });

        return result;
    }

    @Override
    public Set<Integer> getExtremalLabels() {
        return Collections.singleton(extremalLabel);
    }

    @Override
    public WideningOperator<AssignMapping<I>> getWideningOperator() {
        return elements -> {
            AssignMapping<I> result = assignSupplier.get();

            for (Integer key : keySet) {
                result.assign(key, wideningOperator.widen(elements.stream().map(a -> a.get(key)).collect(Collectors.toSet())));
            }

            return result;
        };
    }

    @Override
    public Function<AssignMapping<I>, AssignMapping<I>>
    getTransferFunction(int from, int to) {
        int untangledTo = (to == flow.untangled ? flow.original : to);
        Matching merger = stateSpaceAdapter.getMerger(from, untangledTo);
        Queue<HeapTransformation> buffer = stateSpaceAdapter.getTransformationBuffer(from, untangledTo);

        Function<AssignMapping<I>, AssignMapping<I>> transferFunction;
        if (stateSpaceAdapter.isMaterialized(untangledTo)) {
            transferFunction = generateMaterializationTransferFunction(buffer);
        } else {
            transferFunction = generateAbstractionTransferFunction(buffer);
        }

        return assign -> {
            AssignMapping<I> result = transferFunction.apply(assign);

            // merge
            if (merger != null) {
                merger.pattern().nonterminalEdges().forEach(nt -> {
                    I value = result.get(nt);
                    result.unassign(nt);
                    result.assign(merger.match(nt), value);
                    return true;
                });
            }

            // clean up
            for (Integer key : keySet) {
                if (!result.containsKey(key)) {
                    result.assign(key, indexOp.leastElement());
                }
            }

            for (Integer key : result.keySet()) {
                if (!keySet.contains(key)) {
                    result.unassign(key);
                }
            }

            return result;
        };
    }

    public Function<AssignMapping<I>, AssignMapping<I>>
    generateMaterializationTransferFunction(Queue<HeapTransformation> transformationBuffer) {
        return assign -> {
            final AssignMapping<I> result = assignSupplier.get();
            result.assignAll(assign);

            while (!transformationBuffer.isEmpty()) {
                HeapTransformation step = transformationBuffer.remove();

                // apply rule
                Map<Integer, I> fragment = abstractionRule.abstractForward(
                        result.get(step.getNtEdge()), step.getLabel(), step.getRule());

                // map result from rule to actual heap
                Map<Integer, I> matchedFragment = new HashMap<>();
                for (Map.Entry<Integer, I> entry : fragment.entrySet()) {
                    matchedFragment.put(step.ruleToHeap(entry.getKey()), entry.getValue());
                }

                // update assign AssignMapping
                result.unassign(step.getNtEdge());
                for (Map.Entry<Integer, I> entry : matchedFragment.entrySet()) {
                    result.assign(entry.getKey(), entry.getValue());
                }
            }

            return result;
        };
    }

    public Function<AssignMapping<I>, AssignMapping<I>>
    generateAbstractionTransferFunction(Queue<HeapTransformation> transformationBuffer) {
        return assign -> {
            final AssignMapping<I> result = assignSupplier.get();
            result.assignAll(assign);

            while (!transformationBuffer.isEmpty()) {
                HeapTransformation step = transformationBuffer.remove();

                // map current value from actual heap to rule
                Map<Integer, I> fragment = new HashMap<>();
                step.getRule().nonterminalEdges().forEach(nt -> {
                    fragment.put(nt, result.get(step.ruleToHeap(nt)));
                    return true;
                });

                // apply rule
                I newIndex = abstractionRule.abstractBackward(fragment, step.getLabel(), step.getRule());

                // update assign AssignMapping
                for (Integer nt : fragment.keySet()) {
                    result.unassign(step.ruleToHeap(nt));
                }

                result.assign(step.getNtEdge(), newIndex);
            }

            return result;
        };
    }
}
