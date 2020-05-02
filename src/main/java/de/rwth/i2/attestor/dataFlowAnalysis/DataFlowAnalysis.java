package de.rwth.i2.attestor.dataFlowAnalysis;

import de.rwth.i2.attestor.domain.Lattice;
import gnu.trove.set.TIntSet;


public interface DataFlowAnalysis<T, D> {

    Lattice<D> getDomain();

    DataFlowGraph<T> getFlowGraph();

    TIntSet getExtremalLabels();

    D getExtremalValue(int label);

    D applyTransferFunction(int label);
}
