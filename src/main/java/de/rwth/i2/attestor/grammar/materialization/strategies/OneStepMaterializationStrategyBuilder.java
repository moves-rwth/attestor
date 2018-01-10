package de.rwth.i2.attestor.grammar.materialization.strategies;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.materialization.communication.DefaultGrammarResponseApplier;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.*;
import de.rwth.i2.attestor.grammar.materialization.util.*;
import de.rwth.i2.attestor.programState.indexedState.index.DefaultIndexMaterialization;

public class OneStepMaterializationStrategyBuilder {
	 private boolean indexedMode = false;
	    private Grammar grammar = null;

	    public OneStepMaterializationStrategy build() {

	        if(grammar == null) {
	            throw new IllegalStateException("No grammar.");
	        }

	        if(indexedMode) {
	            return createIndexedStrategy();
	        } else {
	            return createStrategy();
	        }
	    }

	    private OneStepMaterializationStrategy createIndexedStrategy() {

	        GrammarAdapter grammarAdapter = new GrammarAdapter(grammar);

	        IndexMatcher indexMatcher = new IndexMatcher(new DefaultIndexMaterialization());
	        IndexedRuleAdapter indexRuleAdapter = new IndexedRuleAdapter(indexMatcher);
	        
	        ApplicableRulesFinder ruleFinder = new ApplicableRulesFinder(grammarAdapter, indexRuleAdapter);

	        GrammarResponseApplier ruleApplier =
	                new IndexedGrammarResponseApplier(new IndexMaterializationStrategy(),
	                        new GraphMaterializer());

	        return new OneStepMaterializationStrategy(ruleFinder, ruleApplier);
	    }

	    private OneStepMaterializationStrategy createStrategy() {

	        GrammarAdapter grammarAdapter = new GrammarAdapter(grammar);	        
	        ApplicableRulesFinder ruleFinder = new ApplicableRulesFinder(grammarAdapter, null);
	        
	        GrammarResponseApplier ruleApplier =
	                new DefaultGrammarResponseApplier(new GraphMaterializer());

	        return new OneStepMaterializationStrategy(ruleFinder, ruleApplier);
	    }

	    public OneStepMaterializationStrategyBuilder setIndexedMode(boolean isIndexed) {

	        this.indexedMode = isIndexed;
	        return this;
	    }

	    public OneStepMaterializationStrategyBuilder setGrammar(Grammar grammar) {

	        this.grammar = grammar;
	        return this;
	    }
	}

