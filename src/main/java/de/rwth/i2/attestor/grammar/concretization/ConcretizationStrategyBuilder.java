package de.rwth.i2.attestor.grammar.concretization;

import de.rwth.i2.attestor.grammar.Grammar;

public class ConcretizationStrategyBuilder {

    private Grammar grammar = null;
    private SingleStepConcretizationStrategy singleStepStrategy = null;

    public SingleStepConcretizationStrategy buildSingleStepStrategy() {

        if(grammar == null) {
            throw new IllegalStateException("No grammar.");
        }

        singleStepStrategy = new DefaultSingleStepConcretizationStrategy(grammar);
        return singleStepStrategy;
    }

    public FullConcretizationStrategy buildFullConcretizationStrategy() {

        if(singleStepStrategy == null) {
            buildSingleStepStrategy();
        }

        return new FullConcretizationStrategyImpl(singleStepStrategy);
    }

    public ConcretizationStrategyBuilder setGrammar(Grammar grammar) {

        this.grammar = grammar;
        return this;
    }
}
