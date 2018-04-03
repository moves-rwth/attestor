package de.rwth.i2.attestor.refinement.languageInclusion;

import de.rwth.i2.attestor.grammar.AbstractionOptions;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import de.rwth.i2.attestor.graph.morphism.MorphismOptions;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.refinement.StatelessHeapAutomaton;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;

import java.util.Collections;
import java.util.Set;

public class LanguageInclusionAutomaton extends SceneObject implements StatelessHeapAutomaton {

    private final Grammar grammar;

    public LanguageInclusionAutomaton(SceneObject sceneObject, Grammar grammar) {

        super(sceneObject);
        this.grammar = grammar;
    }

    @Override
    public Set<String> transition(HeapConfiguration heapConfiguration) {

        heapConfiguration = getCopyWithoutVariables(heapConfiguration);

        heapConfiguration = canonicalizeCurrent(heapConfiguration);

        TIntArrayList ntEdges = heapConfiguration.nonterminalEdges();
        if (ntEdges.size() != 1 || hasSelectorEdges(heapConfiguration)) {
            return Collections.emptySet();
        }

        String label = heapConfiguration.labelOf(ntEdges.get(0)).getLabel();

        return Collections.singleton("{ L(" + label + ") }");

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

    private HeapConfiguration canonicalizeCurrent(HeapConfiguration hc) {

        MorphismOptions options = new AbstractionOptions().setAdmissibleConstants(
                scene().options().isAdmissibleConstantsEnabled()
        );

        for (Nonterminal lhs : grammar.getAllLeftHandSides()) {
            for (HeapConfiguration rhs : grammar.getRightHandSidesFor(lhs)) {
                AbstractMatchingChecker checker = hc.getEmbeddingsOf(rhs, options);
                if (checker.hasMatching()) {
                    Matching embedding = checker.getMatching();
                    HeapConfiguration abstractedHc = hc.clone()
                            .builder()
                            .replaceMatching(embedding, lhs)
                            .build();
                    return canonicalizeCurrent(abstractedHc);
                }
            }
        }
        return hc;
    }


    private boolean hasSelectorEdges(HeapConfiguration heapConfiguration) {

        TIntIterator iter = heapConfiguration.nodes().iterator();
        while (iter.hasNext()) {
            int node = iter.next();
            if (!heapConfiguration.successorNodesOf(node).isEmpty()) {
                return true;
            }
        }
        return false;
    }

}
