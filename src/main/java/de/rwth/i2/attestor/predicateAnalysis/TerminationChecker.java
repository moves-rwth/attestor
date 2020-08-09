package de.rwth.i2.attestor.predicateAnalysis;

import de.rwth.i2.attestor.domain.AssignMapping;

public interface TerminationChecker<I> {
    boolean check(AssignMapping<I> solutionCritical, AssignMapping<I> solutionUntangled);
}
