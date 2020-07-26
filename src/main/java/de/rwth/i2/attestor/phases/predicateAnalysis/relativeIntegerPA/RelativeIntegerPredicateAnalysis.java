package de.rwth.i2.attestor.phases.predicateAnalysis.relativeIntegerPA;

import de.rwth.i2.attestor.domain.AugmentedInteger;
import de.rwth.i2.attestor.domain.RelativeIndex;
import de.rwth.i2.attestor.phases.predicateAnalysis.IndexAbstractionRule;
import de.rwth.i2.attestor.phases.predicateAnalysis.PredicateAnalysis;
import de.rwth.i2.attestor.phases.predicateAnalysis.StateSpaceAdapter;

public class RelativeIntegerPredicateAnalysis extends PredicateAnalysis<AugmentedInteger> {

    public RelativeIntegerPredicateAnalysis(
            Integer extremalLabel,
            StateSpaceAdapter adapter,
            IndexAbstractionRule<RelativeIndex<AugmentedInteger>> indexAbstractionRule) {
        super(extremalLabel, adapter, RelativeInteger.opSet, indexAbstractionRule, RelativeInteger.opSet.greatestElement());
    }
}
