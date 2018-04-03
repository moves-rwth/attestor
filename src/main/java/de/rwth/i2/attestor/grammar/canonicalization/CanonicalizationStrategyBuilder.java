package de.rwth.i2.attestor.grammar.canonicalization;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.canonicalization.defaultGrammar.DefaultCanonicalizationHelper;
import de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar.EmbeddingIndexChecker;
import de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar.IndexedCanonicalizationHelper;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexMaterializationStrategy;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.morphism.MorphismOptions;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminal;
import de.rwth.i2.attestor.programState.indexedState.index.DefaultIndexMaterialization;
import de.rwth.i2.attestor.programState.indexedState.index.IndexCanonizationStrategy;
import de.rwth.i2.attestor.programState.indexedState.index.IndexCanonizationStrategyImpl;
import de.rwth.i2.attestor.types.Types;
import gnu.trove.iterator.TIntIterator;

import java.util.LinkedHashSet;
import java.util.Set;

// TODO
public class CanonicalizationStrategyBuilder {

    private boolean indexedMode = false;
    private Grammar grammar = null;
    private MorphismOptions options;

    public CanonicalizationStrategy build() {

        if(grammar == null) {
            throw new IllegalStateException("No grammar provided to canonicalization strategy.");
        }

        if(options == null) {
            throw new IllegalStateException("No options provided to canonicalization strategy..");
        }

        EmbeddingCheckerProvider checkerProvider = new EmbeddingCheckerProvider(options);

        CanonicalizationHelper canonicalizationHelper;
        if(indexedMode) {
            canonicalizationHelper = getIndexedCanonicalizationHelper(checkerProvider);
        } else {
            canonicalizationHelper = new DefaultCanonicalizationHelper(checkerProvider);
        }
        return new GeneralCanonicalizationStrategy(grammar, canonicalizationHelper);
    }

    private CanonicalizationHelper getIndexedCanonicalizationHelper(EmbeddingCheckerProvider checkerProvider) {


        IndexCanonizationStrategy indexStrategy = new IndexCanonizationStrategyImpl(determineNullPointerGuards());
        IndexMaterializationStrategy materializationStrategy = new IndexMaterializationStrategy();
        DefaultIndexMaterialization indexGrammar = new DefaultIndexMaterialization();
        IndexMatcher indexMatcher = new IndexMatcher(indexGrammar);
        EmbeddingIndexChecker indexChecker = new EmbeddingIndexChecker(indexMatcher, materializationStrategy);

        return new IndexedCanonicalizationHelper(indexStrategy, checkerProvider, indexChecker);
    }

    private Set<String> determineNullPointerGuards() {

        Set<String> nullPointerGuards = new LinkedHashSet<>();

        for (Nonterminal lhs : grammar.getAllLeftHandSides()) {
            if (lhs instanceof IndexedNonterminal) {
                IndexedNonterminal iLhs = (IndexedNonterminal) lhs;
                if (iLhs.getIndex().getLastIndexSymbol().isBottom()) {
                    for (HeapConfiguration rhs : grammar.getRightHandSidesFor(lhs)) {

                        TIntIterator iterator = rhs.nodes().iterator();
                        while (iterator.hasNext()) {
                            int node = iterator.next();
                            for (SelectorLabel sel : rhs.selectorLabelsOf(node)) {

                                int target = rhs.selectorTargetOf(node, sel);
                                if (rhs.nodeTypeOf(target) == Types.NULL) {
                                    nullPointerGuards.add(sel.getLabel());
                                }
                            }
                        }
                    }
                }
            }
        }

        return nullPointerGuards;
    }

    public CanonicalizationStrategyBuilder setOptions(MorphismOptions options) {

        this.options = options;
        return this;
    }

    public CanonicalizationStrategyBuilder setIndexedMode(boolean enabled) {

        this.indexedMode = enabled;
        return this;
    }

    public CanonicalizationStrategyBuilder setGrammar(Grammar grammar) {

        this.grammar = grammar;
        return this;
    }
}
