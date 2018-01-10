package de.rwth.i2.attestor.grammar.materialization.strategies;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.materialization.communication.DefaultGrammarResponseApplier;
import de.rwth.i2.attestor.grammar.materialization.defaultGrammar.DefaultMaterializationRuleManager;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexMaterializationStrategy;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedGrammarResponseApplier;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedMaterializationRuleManager;
import de.rwth.i2.attestor.grammar.materialization.util.*;
import de.rwth.i2.attestor.programState.indexedState.index.DefaultIndexMaterialization;

public class MaterializationStrategyBuilder {

    private boolean indexedMode = false;
    private Grammar grammar = null;

    public MaterializationStrategy build() {

        if(grammar == null) {
            throw new IllegalStateException("No grammar.");
        }

        if(indexedMode) {
            return createIndexedStrategy();
        } else {
            return createStrategy();
        }
    }

    private MaterializationStrategy createIndexedStrategy() {

        ViolationPointResolver vioResolver = new ViolationPointResolver(grammar);

        IndexMatcher indexMatcher = new IndexMatcher(new DefaultIndexMaterialization());
        MaterializationRuleManager grammarManager =
                new IndexedMaterializationRuleManager(vioResolver, indexMatcher);

        GrammarResponseApplier ruleApplier =
                new IndexedGrammarResponseApplier(new IndexMaterializationStrategy(),
                        new GraphMaterializer());

        return new GeneralMaterializationStrategy(grammarManager, ruleApplier);
    }

    private MaterializationStrategy createStrategy() {

        ViolationPointResolver vioResolver = new ViolationPointResolver(grammar);
        MaterializationRuleManager grammarManager =
                new DefaultMaterializationRuleManager(vioResolver);
        GrammarResponseApplier ruleApplier =
                new DefaultGrammarResponseApplier(new GraphMaterializer());

        return new GeneralMaterializationStrategy(grammarManager, ruleApplier);
    }

    public MaterializationStrategyBuilder setIndexedMode(boolean enabled) {

        this.indexedMode = enabled;
        return this;
    }

    public MaterializationStrategyBuilder setGrammar(Grammar grammar) {

        this.grammar = grammar;
        return this;
    }
}
