package de.rwth.i2.attestor.markings;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.TIntIntMap;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class MarkedHcGenerator {

    private Grammar grammar;
    private Marking marking;
    private String universalVariableName;
    List<SelectorLabel> requiredSelectors;

    private Set<HeapConfiguration> markedHeapConfigurations = new HashSet<>();
    private Stack<HeapConfiguration> unexploredHeapConfigurations = new Stack<>();

    private int currentNode;
    private HeapConfiguration currentHc;
    private TIntIntMap nonReductionTentacles;

    public MarkedHcGenerator(HeapConfiguration initialHc, Grammar grammar, Marking marking) {

        this.grammar = grammar;
        this.marking = marking;
        this.universalVariableName = marking.getUniversalVariableName();
        this.requiredSelectors = marking.getRequiredSelectors();

        placeInitialMarkers(initialHc);
        computeFixpointOfMarkedHcs();
    }

    public Set<HeapConfiguration> getMarkedHcs() {

        return markedHeapConfigurations;
    }

    private void placeInitialMarkers(HeapConfiguration initialHc) {

        TIntIterator nodeIter = initialHc.nodes().iterator();
        while(nodeIter.hasNext()) {
            int node = nodeIter.next();
            HeapConfiguration hc = withUniversalMarking(initialHc, node);
            unexploredHeapConfigurations.push(hc);
        }
    }

    private HeapConfiguration withUniversalMarking(HeapConfiguration hc, int node) {

        return hc
                .clone()
                .builder()
                .addVariableEdge(universalVariableName, node)
                .build();
    }

    private void computeFixpointOfMarkedHcs() {

        while( !unexploredHeapConfigurations.isEmpty() ) {
            nextCurrent();
            if(isCurrentNodeFullyConcrete()) {
                completeCurrentMarking();
                currentHc = canonicalizeCurrent(currentHc);
                if(markedHeapConfigurations.add(currentHc)) {
                    moveCurrentNodeToEachSuccessor();
                }
            } else {
                materializeCurrentNode();
            }
        }
    }

    private void nextCurrent() {

        currentHc = unexploredHeapConfigurations.pop();
        currentNode = currentHc.targetOf(currentHc.variableWith(universalVariableName));
    }

    private boolean isCurrentNodeFullyConcrete() {

        nonReductionTentacles = currentHc.attachedNonterminalEdgesWithNonReductionTentacle(currentNode);
        return nonReductionTentacles.isEmpty();
    }

    private void completeCurrentMarking() {

        List<SelectorLabel> availableSelectors = currentHc.selectorLabelsOf(currentNode);
        if(availableSelectors.containsAll(requiredSelectors)) {
            HeapConfigurationBuilder builder = currentHc.builder();
            for(SelectorLabel sel : requiredSelectors) {
                builder.addVariableEdge(
                        marking.getSelectorVariableName(sel.getLabel()),
                        currentHc.selectorTargetOf(currentNode, sel)
                );
            }
            builder.build();
        }
    }

    private HeapConfiguration canonicalizeCurrent(HeapConfiguration hc) {

        for(Nonterminal lhs : grammar.getAllLeftHandSides()) {
            for(HeapConfiguration rhs : grammar.getRightHandSidesFor(lhs)) {
                AbstractMatchingChecker checker = hc.getEmbeddingsOf(rhs, 0);
                if(checker.hasMatching()) {
                    Matching embedding = checker.getMatching();
                    return hc.clone().builder().replaceMatching( embedding, lhs).build();
                }
            }
        }
        return hc;
    }

    private void moveCurrentNodeToEachSuccessor() {

        HeapConfiguration cleanHc = withoutMarkings(currentHc);
        for(SelectorLabel sel : cleanHc.selectorLabelsOf(currentNode)) {
            int successorNode = cleanHc.selectorTargetOf(currentNode, sel);
            unexploredHeapConfigurations.push(withUniversalMarking(cleanHc, successorNode));
        }
    }

    private HeapConfiguration withoutMarkings(HeapConfiguration hc)  {

        HeapConfigurationBuilder builder = hc.clone().builder();
        int var = hc.variableWith(universalVariableName);
        builder.removeVariableEdge(var);
        for(SelectorLabel sel : hc.selectorLabelsOf(currentNode)) {
            var = hc.variableWith(marking.getSelectorVariableName(sel.getLabel()));
            if(var != HeapConfiguration.INVALID_ELEMENT) {
                builder.removeVariableEdge(var);
            }
        }
        return builder.build();
    }

    private void materializeCurrentNode() {

        TIntIntIterator iter = nonReductionTentacles.iterator();
        while(iter.hasNext()) {
            iter.advance();
            int edge = iter.key();
            int tentacle = iter.value();
            Nonterminal label = currentHc.labelOf(edge);
            materialize(edge, label, tentacle);
            break;
        }
    }

    private void materialize(int edge, Nonterminal edgeLabel, int tentacle) {

        assert(edgeLabel.getRank() > tentacle);

        for(HeapConfiguration rhs : grammar.getRightHandSidesFor(edgeLabel)) {

            int ext = rhs.externalNodeAt(tentacle);
            if(!rhs.selectorLabelsOf(ext).isEmpty()) {

                HeapConfiguration materializedHc = currentHc
                        .clone()
                        .builder()
                        .replaceNonterminalEdge(edge, rhs)
                        .build();

                unexploredHeapConfigurations.add(materializedHc);
            }
        }

    }
}
