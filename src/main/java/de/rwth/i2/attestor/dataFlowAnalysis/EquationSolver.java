package de.rwth.i2.attestor.dataFlowAnalysis;

import java.util.Map;

public interface EquationSolver<D> {
    Map<Integer, D> solve(DataFlowAnalysis<D> framework);
}
