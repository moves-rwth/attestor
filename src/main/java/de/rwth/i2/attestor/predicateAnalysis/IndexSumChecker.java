package de.rwth.i2.attestor.predicateAnalysis;

import de.rwth.i2.attestor.domain.AddMonoid;
import de.rwth.i2.attestor.domain.AssignMapping;
import de.rwth.i2.attestor.domain.Lattice;

public class IndexSumChecker<I> implements TerminationChecker<I> {
    private final Lattice<I> latticeOp;
    private AddMonoid<I> monoidOp;

    public IndexSumChecker(Lattice<I> latticeOp, AddMonoid<I> monoidOp) {
        this.latticeOp = latticeOp;
        this.monoidOp = monoidOp;
    }

    @Override
    public boolean check(AssignMapping<I> solutionCritical, AssignMapping<I> solutionUntangled) {
        I extremal = solutionCritical.values().stream().reduce(monoidOp.identity(), (i1, i2) -> monoidOp.add(i1, i2));
        I transferred = solutionCritical.values().stream().reduce(monoidOp.identity(), (i1, i2) -> monoidOp.add(i1, i2));

        return !transferred.equals(extremal) && latticeOp.isLessOrEqual(transferred, extremal);
    }
}
