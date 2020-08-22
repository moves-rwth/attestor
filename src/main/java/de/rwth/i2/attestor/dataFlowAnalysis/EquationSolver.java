package de.rwth.i2.attestor.dataFlowAnalysis;

import java.util.Map;

public interface EquationSolver<D> {
    Map<Integer, D> solve();

    Map<Integer, D> narrow(Map<Integer, D> initial);
}
