package de.rwth.i2.attestor.phases.predicateAnalysis;

import de.rwth.i2.attestor.domain.*;

public class RelativeIntegerPredicateAnalysis extends PredicateAnalysis<AugmentedInteger> {
    private static final RelativeIndex.RelativeIndexSet<AugmentedInteger> indexOp = new RelativeInteger.RelativeIntegerSet();

    public RelativeIntegerPredicateAnalysis(
            Integer extremalLabel,
            StateSpaceAdapter adapter,
            IndexAbstractionRule<RelativeIndex<AugmentedInteger>> indexAbstractionRule) {
        super(extremalLabel, adapter, indexOp, indexAbstractionRule, indexOp.greatestElement());
    }
}
