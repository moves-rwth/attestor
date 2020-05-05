package de.rwth.i2.attestor.dataFlowAnalysis.predicate;

import de.rwth.i2.attestor.dataFlowAnalysis.DataFlowAnalysis;
import de.rwth.i2.attestor.dataFlowAnalysis.EquationSolver;
import de.rwth.i2.attestor.dataFlowAnalysis.Flow;
import de.rwth.i2.attestor.dataFlowAnalysis.UntangledFlow;
import de.rwth.i2.attestor.domain.AssignMapping;
import de.rwth.i2.attestor.domain.AssignMappingImpl;
import de.rwth.i2.attestor.domain.Lattice;
import de.rwth.i2.attestor.domain.RelativeIndexSet;
import de.rwth.i2.attestor.graph.heap.internal.TAHeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.TransformationStep;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.Collections;
import java.util.Queue;
import java.util.function.Function;

public class PredicateAnalysis<I, L extends Lattice<I> & RelativeIndexSet<I>>
        implements DataFlowAnalysis<AssignMapping<Integer, I>> {

    private final UntangledFlow flow;
    private final TIntObjectMap<TAProgramState> labelToState;
    private final int extremalLabel;
    private final L indexLattice;
    private final Lattice<AssignMapping<Integer, I>> domainLattice;
    private final IndexAbstractionRule<I> indexAbstractionRule;
    private final EquationSolver<AssignMapping<Integer, I>> solver;

    public PredicateAnalysis(
            int extremalLabel,
            L indexLattice,
            StateSpaceAdapter adapter,
            IndexAbstractionRule<I> indexAbstractionRule,
            EquationSolver<AssignMapping<Integer, I>> solver) {

        this.extremalLabel = extremalLabel;
        this.indexAbstractionRule = indexAbstractionRule;
        this.solver = solver;
        this.flow = new UntangledFlow(adapter.flow, extremalLabel);
        this.labelToState = adapter.labelToStateMap;
        this.indexLattice = indexLattice;
        this.domainLattice = new AssignMapping.AssignMappingSet<>(indexLattice);
    }

    @Override
    public Flow getFlow() {
        return flow;
    }

    @Override
    public Lattice<AssignMapping<Integer, I>> getLattice() {
        return domainLattice;
    }

    @Override
    public AssignMapping<Integer, I> getExtremalValue() {
        return new AssignMapping<Integer, I>() {
            private final TIntObjectMap<I> map = new TIntObjectHashMap<>();

            @Override
            public I apply(Integer i) {
                if (!map.containsKey(i)) {
                    map.put(i, indexLattice.generateVariable());
                }

                return map.get(i);
            }
        };
    }

    @Override
    public TIntSet getExtremalLabels() {
        return new TIntHashSet(Collections.singleton(extremalLabel));
    }

    @Override
    public Function<AssignMapping<Integer, I>, AssignMapping<Integer, I>> getTransferFunction(int from, int to) {
        TAProgramState state = labelToState.get(to);

        if (state.isMaterialized) {
            return generateMaterializationTransferFunction(state.heap);
        } else {
            return generateAbstractionTransferFunction(state.heap);
        }
    }

    @Override
    public TIntObjectMap<AssignMapping<Integer, I>> solve() {
        return solver.solve(this);
    }

    private Function<AssignMapping<Integer, I>, AssignMapping<Integer, I>>
    generateMaterializationTransferFunction(TAHeapConfiguration heap) {
        final Queue<TransformationStep> history = heap.transformationHistory;

        return assign -> {
            AssignMappingImpl<Integer, I> result = new AssignMappingImpl<>(assign);
            while (!history.isEmpty()) {
                TransformationStep step = history.element();

                TIntObjectMap<I> fragment = indexAbstractionRule.abstractForward(
                        result.apply(step.getNtEdge()),
                        step.getRule());

                // map result from rule to actual heap
                TIntObjectHashMap<I> matchedFragment = new TIntObjectHashMap<>();
                fragment.forEachEntry((key, value) -> {
                    matchedFragment.put(step.match(key), value);
                    return true;
                });

                // update assign mapping
                matchedFragment.forEachEntry((key, value) -> {
                    result.set(key, value);
                    return true;
                });
            }

            return result;
        };
    }

    private Function<AssignMapping<Integer, I>, AssignMapping<Integer, I>>
    generateAbstractionTransferFunction(TAHeapConfiguration heap) {
        final Queue<TransformationStep> history = heap.transformationHistory;

        return assign -> {
            AssignMappingImpl<Integer, I> result = new AssignMappingImpl<>(assign);

            while (!history.isEmpty()) {
                TransformationStep step = history.element();

                // map current value from actual heap to rule
                TIntObjectHashMap<I> fragment = new TIntObjectHashMap<I>();
                for (Integer key : result) {
                    step.getRule().nonterminalEdges().forEach(nt -> {
                        if (step.match(nt) == key) {
                            fragment.put(nt, result.apply(key));
                            return false;
                        }
                        return true;
                    });
                }

                I newIndex = indexAbstractionRule.abstractBackward(fragment, step.getRule());

                // update assign mapping
                result.set(step.getNtEdge(), newIndex);
            }

            return result;
        };
    }
}
