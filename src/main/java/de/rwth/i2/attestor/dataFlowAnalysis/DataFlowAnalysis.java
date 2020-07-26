package de.rwth.i2.attestor.dataFlowAnalysis;

import de.rwth.i2.attestor.domain.Lattice;

import java.util.Set;
import java.util.function.Function;

public interface DataFlowAnalysis<D> {
    Flow getFlow();

    Lattice<D> getLattice();

    D getExtremalValue();

    Set<Integer> getExtremalLabels();

    Function<D, D> getTransferFunction(int from, int to);

    WideningOperator<D> getWideningOperator();
}
