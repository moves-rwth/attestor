package de.rwth.i2.attestor.dataFlowAnalysis;

import gnu.trove.set.TIntSet;

public interface Flow {
    TIntSet getLabels();

    TIntSet getInitial();

    TIntSet getFinal();

    TIntSet getSuccessors(int label);

    TIntSet getPredecessors(int label);
}
