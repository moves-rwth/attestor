package de.rwth.i2.attestor.predicateAnalysis.relativeIntegerPA;

import de.rwth.i2.attestor.domain.AugmentedInteger;
import de.rwth.i2.attestor.domain.RelativeIndex;
import de.rwth.i2.attestor.predicateAnalysis.IndexAbstractionRule;
import de.rwth.i2.attestor.predicateAnalysis.PredicateAnalysis;
import de.rwth.i2.attestor.predicateAnalysis.StateSpaceAdapter;

public class RelativeIntegerPA extends PredicateAnalysis<AugmentedInteger> {

    public RelativeIntegerPA(
            Integer extremalLabel,
            StateSpaceAdapter adapter,
            IndexAbstractionRule<RelativeIndex<AugmentedInteger>> indexAbstractionRule) {

        super(extremalLabel, adapter, RelativeInteger.opSet, RelativeInteger.opSet.greatestElement(), indexAbstractionRule);
    }
}
