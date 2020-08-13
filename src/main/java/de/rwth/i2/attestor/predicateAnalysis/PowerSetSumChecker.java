package de.rwth.i2.attestor.predicateAnalysis;

import de.rwth.i2.attestor.domain.AddMonoid;
import de.rwth.i2.attestor.domain.AssignMapping;
import de.rwth.i2.attestor.domain.Lattice;
import de.rwth.i2.attestor.util.Sets;

import java.util.*;

public class PowerSetSumChecker<I> implements TerminationChecker<I> {
    private final Lattice<I> latticeOp;
    private final AddMonoid<I> monoidOp;

    public PowerSetSumChecker(Lattice<I> latticeOp, AddMonoid<I> monoidOp) {
        this.latticeOp = latticeOp;
        this.monoidOp = monoidOp;
    }

    @Override
    public Set<Integer> check(AssignMapping<I> solutionCritical, AssignMapping<I> solutionUntangled) {
        if (!solutionCritical.keySet().equals(solutionUntangled.keySet())) {
            throw new IllegalArgumentException("Key sets of assign mapping must be compatible.");
        }

        List<Set<Integer>> powerSet = new ArrayList<>(Sets.powerSet(solutionCritical.keySet()));
        powerSet.sort(Comparator.comparingInt(Set::size));

        for (Set<Integer> set : powerSet) {
            I extremal = solutionCritical
                    .entrySet()
                    .stream()
                    .filter(entry -> set.contains(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .reduce(monoidOp.identity(), monoidOp::add);

            I transferred = solutionUntangled.entrySet()
                    .stream()
                    .filter(entry -> set.contains(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .reduce(monoidOp.identity(), monoidOp::add);

            if (!transferred.equals(extremal) && latticeOp.isLessOrEqual(transferred, extremal)) {
                return set;
            }
        }

        return Collections.emptySet();
    }
}
