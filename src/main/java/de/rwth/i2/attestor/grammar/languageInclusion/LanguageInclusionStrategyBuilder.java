package de.rwth.i2.attestor.grammar.languageInclusion;

import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.concretization.SingleStepConcretizationStrategy;

public class LanguageInclusionStrategyBuilder {

    private boolean indexedMode = false;
    private int minAbstractionDistance = 0;
    private CanonicalizationStrategy canonicalizationStrategy = null;
    private SingleStepConcretizationStrategy singleStepConcretizationStrategy = null;

    public LanguageInclusionStrategy build() {

        if(canonicalizationStrategy == null) {
            throw new IllegalStateException("No canonicalization strategy.");
        }

        return new LanguageInclusionImpl(minAbstractionDistance, indexedMode,
                canonicalizationStrategy, singleStepConcretizationStrategy);
    }

    public LanguageInclusionStrategyBuilder setIndexedMode(boolean indexedMode) {

        this.indexedMode = indexedMode;
        return this;
    }

    public LanguageInclusionStrategyBuilder setMinAbstractionDistance(int minAbstractionDistance) {

        if(minAbstractionDistance < 0 || minAbstractionDistance > 1) {
            throw new IllegalArgumentException("minAbstraction distance must be either 0 or 1. Got: "
                    + minAbstractionDistance);
        }
        this.minAbstractionDistance = minAbstractionDistance;
        return this;
    }

    public LanguageInclusionStrategyBuilder setCanonicalizationStrategy(CanonicalizationStrategy strategy) {

        this.canonicalizationStrategy = strategy;
        return this;
    }

    public LanguageInclusionStrategyBuilder setSingleStepConcretizationStrategy(SingleStepConcretizationStrategy strategy) {

        this.singleStepConcretizationStrategy = strategy;
        return this;
    }


}
