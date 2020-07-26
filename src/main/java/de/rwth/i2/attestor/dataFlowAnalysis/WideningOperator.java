package de.rwth.i2.attestor.dataFlowAnalysis;

import java.util.Set;

public interface WideningOperator<T> {
    T widen(Set<T> elements);
}
