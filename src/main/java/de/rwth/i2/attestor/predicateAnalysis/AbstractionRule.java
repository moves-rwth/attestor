package de.rwth.i2.attestor.predicateAnalysis;

import de.rwth.i2.attestor.grammar.Grammar;

public abstract class AbstractionRule {
    protected final Grammar grammar;

    protected AbstractionRule(Grammar grammar) {
        this.grammar = grammar;
    }
}
