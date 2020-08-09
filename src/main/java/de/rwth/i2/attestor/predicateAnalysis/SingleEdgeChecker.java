package de.rwth.i2.attestor.predicateAnalysis;

import de.rwth.i2.attestor.domain.AssignMapping;
import de.rwth.i2.attestor.domain.Lattice;

public class SingleEdgeChecker<I> implements TerminationChecker<I> {
    private final Lattice<I> latticeOp;

    public SingleEdgeChecker(Lattice<I> latticeOp) {
        this.latticeOp = latticeOp;
    }

    @Override
    public boolean check(AssignMapping<I> solutionCritical, AssignMapping<I> solutionUntangled) {
        if (!solutionCritical.keySet().equals(solutionUntangled.keySet())) {
            throw new IllegalArgumentException("Key sets of assign mapping must be compatible.");
        }

        for (Integer key : solutionCritical.keySet()) {
            I extremal = solutionCritical.get(key);
            I transferred = solutionUntangled.get(key);

            if (!transferred.equals(extremal) && latticeOp.isLessOrEqual(transferred, extremal)) {
                return true;
            }
        }

        return false;
    }
}
