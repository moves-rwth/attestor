package de.rwth.i2.attestor.dataFlowAnalysis;


import gnu.trove.TCollections;
import gnu.trove.set.TIntSet;

import java.util.function.Function;

public abstract class DataFlowAnalysis<T, D> {
    private final DataFlowGraph<T> graph;

    public abstract D getExtremalValue(int label);

    public abstract Function<D, D> getTransferFunction(int label);

    public DataFlowAnalysis(DataFlowGraph<T> graph, TIntSet extremalLabels) {
        this.graph = graph;
    }

    public DataFlowGraph<T> getGraph() {
        return graph;
    }
}
