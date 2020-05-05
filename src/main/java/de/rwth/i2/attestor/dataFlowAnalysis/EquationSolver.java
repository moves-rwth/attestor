package de.rwth.i2.attestor.dataFlowAnalysis;

import gnu.trove.map.TIntObjectMap;

public interface EquationSolver<D> {
    TIntObjectMap<D> solve(DataFlowAnalysis<D> framework);
}
