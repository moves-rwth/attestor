package de.rwth.i2.attestor.phases.predicateAnalysis;

import de.rwth.i2.attestor.domain.*;

import java.util.Set;

public class RelativeIntegerPredicateAnalysis extends PredicateAnalysis<AugmentedInteger> {

    public RelativeIntegerPredicateAnalysis(
            Integer extremalLabel,
            StateSpaceAdapter adapter,
            IndexAbstractionRule<RelativeIndex<AugmentedInteger>> indexAbstractionRule) {
        super(extremalLabel, adapter, new RelativeInteger.RelativeIntegerSet(), indexAbstractionRule);
    }
}
