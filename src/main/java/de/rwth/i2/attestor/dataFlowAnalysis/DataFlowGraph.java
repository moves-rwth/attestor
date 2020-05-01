package de.rwth.i2.attestor.dataFlowAnalysis;

import gnu.trove.set.TIntSet;

public interface DataFlowGraph<T> {
    T getNode(int label);

    TIntSet getInitialLabels();

    TIntSet getFinalLabels();

    TIntSet getSuccessors(int label);

    TIntSet getPredecessors(int label);
}
