package de.rwth.i2.attestor.predicateAnalysis;

import de.rwth.i2.attestor.domain.AssignMapping;

import java.util.Set;

public interface TerminationChecker<I> {
    Set<Integer> check(AssignMapping<I> solutionCritical, AssignMapping<I> solutionUntangled);
}
