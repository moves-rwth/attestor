package de.rwth.i2.attestor.dataFlowAnalysis.predicate;

import de.rwth.i2.attestor.dataFlowAnalysis.DataFlowAnalysis;
import de.rwth.i2.attestor.dataFlowAnalysis.Flow;
import de.rwth.i2.attestor.dataFlowAnalysis.UntangledFlow;
import de.rwth.i2.attestor.domain.AssignMapping;
import de.rwth.i2.attestor.domain.Lattice;
import de.rwth.i2.attestor.domain.RelativeIndexSet;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.Collections;
import java.util.function.Function;

public class PredicateAnalysis<I, L extends Lattice<I> & RelativeIndexSet<I>>
        implements DataFlowAnalysis<AssignMapping<Integer, I>> {

    private final UntangledFlow flow;
    private final TIntObjectMap<TAProgramState> labelToState;
    private final int extremalLabel;
    private final L indexLattice;
    private final Lattice<AssignMapping<Integer, I>> domainLattice;

    public PredicateAnalysis(L indexLattice, StateSpaceAdapter adapter, int extremalLabel) {
        this.flow = new UntangledFlow(adapter.flow, extremalLabel);
        this.labelToState = adapter.labelToStateMap;
        this.extremalLabel = extremalLabel;
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
        return null;
    }
}
