package de.rwth.i2.attestor.refinement.balanced;

import de.rwth.i2.attestor.grammar.AbstractionOptions;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationHelper;
import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.canonicalization.EmbeddingCheckerProvider;
import de.rwth.i2.attestor.grammar.canonicalization.GeneralCanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar.EmbeddingIndexChecker;
import de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar.IndexedCanonicalizationHelper;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexMaterializationStrategy;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminal;
import de.rwth.i2.attestor.programState.indexedState.index.DefaultIndexMaterialization;
import de.rwth.i2.attestor.programState.indexedState.index.IndexCanonizationStrategy;
import de.rwth.i2.attestor.programState.indexedState.index.IndexCanonizationStrategyImpl;
import de.rwth.i2.attestor.refinement.StatelessHeapAutomaton;
import de.rwth.i2.attestor.types.Types;
import gnu.trove.iterator.TIntIterator;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class BalancednessAutomaton extends SceneObject implements StatelessHeapAutomaton {

    private final Grammar grammar;
    private CanonicalizationStrategy canonicalizationStrategy;
    private BalancednessHelper helper;

    public BalancednessAutomaton(SceneObject sceneObject, Grammar grammar) {

        super(sceneObject);
        this.grammar = grammar;
        SelectorLabel left = sceneObject.scene().getSelectorLabel("left");
        SelectorLabel right = sceneObject.scene().getSelectorLabel("right");
        helper = new BalancednessHelper(this, left, right);
        setupCanonicalization();
    }

    @Override
    public Set<String> transition(HeapConfiguration heapConfiguration) {

        heapConfiguration = getCopyWithoutVariables(heapConfiguration);
        helper.updateSelectorAnnotations(heapConfiguration);

        heapConfiguration = canonicalizationStrategy.canonicalize(heapConfiguration);

        if (countSelectorEdges(heapConfiguration) > 1) {
            return Collections.emptySet();
        }


        TIntIterator iter = heapConfiguration.nonterminalEdges().iterator();
        int pFound = 0;
        int btFound = 0;
        while (iter.hasNext()) {
            int edge = iter.next();
            String label = heapConfiguration.labelOf(edge).getLabel();
            switch (label) {
                case "P":
                    ++pFound;
                    break;
                case "BT":
                    ++btFound;
                    break;
            }
        }

        if (btFound == 1 && pFound <= 1) {
            return Collections.singleton("{ btree }");
        } else {
            return Collections.emptySet();
        }
    }

    private void setupCanonicalization() {

        EmbeddingCheckerProvider checkerProvider = new EmbeddingCheckerProvider(
                new AbstractionOptions().setAdmissibleConstants(
                        scene().options().isAdmissibleConstantsEnabled()
                )
        );

        CanonicalizationHelper canonicalizationHelper = getIndexedCanonicalizationHelper(checkerProvider);
        canonicalizationStrategy = new GeneralCanonicalizationStrategy(grammar, canonicalizationHelper);
    }

    private CanonicalizationHelper getIndexedCanonicalizationHelper(EmbeddingCheckerProvider checkerProvider) {

        CanonicalizationHelper canonicalizationHelper;
        IndexCanonizationStrategy indexStrategy = new IndexCanonizationStrategyImpl(determineNullPointerGuards());
        IndexMaterializationStrategy materializer = new IndexMaterializationStrategy();
        DefaultIndexMaterialization indexGrammar = new DefaultIndexMaterialization();
        IndexMatcher indexMatcher = new IndexMatcher(indexGrammar);
        EmbeddingIndexChecker indexChecker =
                new EmbeddingIndexChecker(indexMatcher,
                        materializer);

        canonicalizationHelper = new IndexedCanonicalizationHelper(indexStrategy, checkerProvider, indexChecker);
        return canonicalizationHelper;
    }

    private Set<String> determineNullPointerGuards() {

        Set<String> nullPointerGuards = new LinkedHashSet<>();

        for (Nonterminal lhs : grammar.getAllLeftHandSides()) {
            if (lhs instanceof IndexedNonterminal) {
                IndexedNonterminal iLhs = (IndexedNonterminal) lhs;
                if (iLhs.getIndex().getLastIndexSymbol().isBottom()) {
                    for (HeapConfiguration rhs : grammar.getRightHandSidesFor(lhs)) {

                        TIntIterator iter = rhs.nodes().iterator();
                        while (iter.hasNext()) {
                            int node = iter.next();
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

    private HeapConfiguration getCopyWithoutVariables(HeapConfiguration heapConfiguration) {

        heapConfiguration = heapConfiguration.clone();
        TIntIterator iter = heapConfiguration.variableEdges().iterator();
        HeapConfigurationBuilder builder = heapConfiguration.builder();
        while (iter.hasNext()) {
            int varEdge = iter.next();
            builder.removeVariableEdge(varEdge);
        }
        return builder.build();
    }

    private int countSelectorEdges(HeapConfiguration heapConfiguration) {

        int count = 0;
        TIntIterator iter = heapConfiguration.nodes().iterator();
        while (iter.hasNext()) {
            int node = iter.next();
            count += heapConfiguration.selectorLabelsOf(node).size();
        }
        return count;
    }

}
