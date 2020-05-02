package de.rwth.i2.attestor.dataFlowAnalysis;

import gnu.trove.set.TIntSet;

public interface DataFlowGraph<T> {
    T getNode(int label);

    TIntSet getLabels();

    TIntSet getInitial();

    TIntSet getFinal();

    TIntSet getSuccessors(int label);

    TIntSet getPredecessors(int label);
}
