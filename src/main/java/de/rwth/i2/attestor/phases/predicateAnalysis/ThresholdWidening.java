package de.rwth.i2.attestor.phases.predicateAnalysis;

import de.rwth.i2.attestor.dataFlowAnalysis.WideningOperator;
import de.rwth.i2.attestor.domain.Lattice;

import java.util.Set;

public class ThresholdWidening<T> implements WideningOperator<T> {
    private final T threshold;
    private Lattice<T> latticeOp;

    public ThresholdWidening(T threshold, Lattice<T> latticeOp) {
        this.threshold = threshold;
        this.latticeOp = latticeOp;
    }

    @Override
    public T widen(Set<T> elements) {
        if (elements.stream().anyMatch(e -> latticeOp.isLessOrEqual(e, threshold))) {
            return latticeOp.getLeastUpperBound(elements);
        }
        else {
            return latticeOp.greatestElement();
        }
    }
}
