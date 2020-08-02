package de.rwth.i2.attestor.phases.transformers;


import de.rwth.i2.attestor.predicateAnalysis.AbstractionRule;

public interface AbstractionRuleTransformer<T> {
    AbstractionRule<T> getAbstractionRule();
}
