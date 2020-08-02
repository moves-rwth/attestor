package de.rwth.i2.attestor.phases.parser;

import de.rwth.i2.attestor.domain.RelativeInteger;

import java.util.Map;

public interface AbstractionRuleExpression {
    RelativeInteger evaluate(RelativeInteger index, Map<Integer, RelativeInteger> assign);
}
