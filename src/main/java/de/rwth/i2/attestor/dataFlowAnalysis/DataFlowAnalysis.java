package de.rwth.i2.attestor.dataFlowAnalysis;

import de.rwth.i2.attestor.domain.Lattice;
import gnu.trove.set.TIntSet;

import java.util.function.Function;

public interface DataFlowAnalysis<D> {
    Flow getFlow();

    Lattice<D> getLattice();

    D getExtremalValue();

    TIntSet getExtremalLabels();

    Function<D, D> getTransferFunction(int from, int to);
}
