package de.rwth.i2.attestor.predicateAnalysis;

import de.rwth.i2.attestor.domain.AddMonoid;
import de.rwth.i2.attestor.domain.AssignMapping;
import de.rwth.i2.attestor.domain.Lattice;

import java.util.Collections;
import java.util.Set;

public class TotalSumChecker<I> implements TerminationChecker<I> {
    private final Lattice<I> latticeOp;
    private final AddMonoid<I> monoidOp;

    public TotalSumChecker(Lattice<I> latticeOp, AddMonoid<I> monoidOp) {
        this.latticeOp = latticeOp;
        this.monoidOp = monoidOp;
    }

    @Override
    public Set<Integer> check(AssignMapping<I> solutionCritical, AssignMapping<I> solutionUntangled) {
        if (!solutionCritical.keySet().equals(solutionUntangled.keySet())) {
            throw new IllegalArgumentException("Key sets of assign mapping must be compatible.");
        }

        I extremal = solutionCritical.values().stream().reduce(monoidOp.identity(), monoidOp::add);
        I transferred = solutionUntangled.values().stream().reduce(monoidOp.identity(), monoidOp::add);

        if (!transferred.equals(extremal) && latticeOp.isLessOrEqual(transferred, extremal)) {
            return Collections.unmodifiableSet(solutionCritical.keySet());
        }

        return Collections.emptySet();
    }
}
