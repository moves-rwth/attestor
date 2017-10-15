package de.rwth.i2.attestor.refinement.balanced;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationHelper;
import de.rwth.i2.attestor.grammar.canonicalization.EmbeddingCheckerProvider;
import de.rwth.i2.attestor.grammar.canonicalization.GeneralCanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar.EmbeddingIndexChecker;
import de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar.IndexedCanonicalizationHelper;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexMaterializationStrategy;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.refinement.StatelessHeapAutomaton;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.ReturnVoidStmt;
import de.rwth.i2.attestor.stateSpaceGeneration.CanonicalizationStrategy;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminal;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedState;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.DefaultIndexMaterialization;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.IndexCanonizationStrategy;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.IndexCanonizationStrategyImpl;
import de.rwth.i2.attestor.types.GeneralType;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BalancednessAutomaton implements StatelessHeapAutomaton {

    private Grammar grammar;
    private CanonicalizationStrategy canonicalizationStrategy;

    public BalancednessAutomaton(Grammar grammar) {

        this.grammar = grammar;
        setupCanonicalization();
    }

    @Override
    public Set<String> transition(HeapConfiguration heapConfiguration) {

        heapConfiguration = getCopyWithoutVariables(heapConfiguration);
        BalancednessHelper.updateSelectorAnnotations(heapConfiguration);

        IndexedState state = new IndexedState(heapConfiguration, 0);
        heapConfiguration = canonicalizationStrategy.canonicalize(new ReturnVoidStmt(), state).getHeap();

        TIntArrayList ntEdges = heapConfiguration.nonterminalEdges();
        if(ntEdges.size() > 2 || countSelectorEdges(heapConfiguration) > 1) {
            return Collections.emptySet();
        }

        //String label = heapConfiguration.labelOf( ntEdges.get(0) ).getLabel();

        return Collections.singleton("{ btree }");

    }

    private void setupCanonicalization() {

        EmbeddingCheckerProvider checkerProvider = new EmbeddingCheckerProvider(0,
                0, true);

        CanonicalizationHelper canonicalizationHelper = getIndexedCanonicalizationHelper(checkerProvider);
        canonicalizationStrategy = new GeneralCanonicalizationStrategy(grammar, canonicalizationHelper);
    }

    private CanonicalizationHelper getIndexedCanonicalizationHelper(EmbeddingCheckerProvider checkerProvider) {
        CanonicalizationHelper canonicalizationHelper;
        IndexCanonizationStrategy indexStrategy = new IndexCanonizationStrategyImpl(determineNullPointerGuards());
        IndexMaterializationStrategy materializer = new IndexMaterializationStrategy();
        DefaultIndexMaterialization indexGrammar = new DefaultIndexMaterialization();
        IndexMatcher indexMatcher = new IndexMatcher( indexGrammar);
        EmbeddingIndexChecker indexChecker =
                new EmbeddingIndexChecker( indexMatcher,
                        materializer );

        canonicalizationHelper = new IndexedCanonicalizationHelper( indexStrategy, checkerProvider, indexChecker);
        return canonicalizationHelper;
    }

    private Set<String> determineNullPointerGuards() {

        Set<String> nullPointerGuards = new HashSet<>();

        for(Nonterminal lhs : grammar.getAllLeftHandSides()) {
            if(lhs instanceof IndexedNonterminal) {
                IndexedNonterminal iLhs = (IndexedNonterminal) lhs;
                if(iLhs.getIndex().getLastIndexSymbol().isBottom()) {
                    for(HeapConfiguration rhs : grammar.getRightHandSidesFor(lhs)) {

                        TIntIterator iter = rhs.nodes().iterator();
                        while(iter.hasNext()) {
                            int node = iter.next();
                            for(SelectorLabel sel : rhs.selectorLabelsOf(node)) {

                                int target = rhs.selectorTargetOf(node, sel);
                                if(rhs.nodeTypeOf(target) == GeneralType.getType("NULL")) {
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

    private HeapConfiguration getCopyWithoutVariables(HeapConfiguration heapConfiguration) {

        heapConfiguration = heapConfiguration.clone();
        TIntIterator iter = heapConfiguration.variableEdges().iterator();
        HeapConfigurationBuilder builder = heapConfiguration.builder();
        while(iter.hasNext()) {
            int varEdge = iter.next();
            builder.removeVariableEdge(varEdge);
        }
        return builder.build();
    }

    private int countSelectorEdges(HeapConfiguration heapConfiguration) {

        int count = 0;
        TIntIterator iter = heapConfiguration.nodes().iterator();
        while(iter.hasNext()) {
            int node = iter.next();
            count += heapConfiguration.selectorLabelsOf(node).size();
        }
        return count;
    }

}
