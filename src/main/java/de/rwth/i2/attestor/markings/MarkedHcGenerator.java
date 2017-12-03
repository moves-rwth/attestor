package de.rwth.i2.attestor.markings;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.semantics.util.Constants;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.TIntIntMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * Given a grammar and an initial HeapConfiguration, this class computes a set of partially unfolded HeapConfigurations
 * in which every node and ,if specified, some of its selectors are marked by special variables.
 * The computed sets covers all possible unfolded HeapConfigurations of the initial
 * one with respect to the given grammar.
 *
 * @author Christoph
 */
public class MarkedHcGenerator extends SceneObject {

    private final Grammar grammar;
    private final Marking marking;
    private final String universalVariableName;
    private final List<SelectorLabel> requiredSelectors;

    private final Set<HeapConfiguration> markedHeapConfigurations = new HashSet<>();
    private final Stack<HeapConfiguration> unexploredHeapConfigurations = new Stack<>();

    private int currentNode;
    private HeapConfiguration currentHc;
    private TIntIntMap nonReductionTentacles;

    /**
     * Start generating all marked HeapConfigurations.
     *
     * @param sceneObject Parent scene object
     * @param initialHc   The HeapConfigurations whose unfolded HeapConfigurations shall be marked.
     * @param grammar     The grammar specifying materialization and canonicalization.
     * @param marking     A specification of the variable that should traverse every node in the unfolded HeapConfigurations
     *                    and the selectors that should also be marked.
     */
    public MarkedHcGenerator(SceneObject sceneObject, HeapConfiguration initialHc, Grammar grammar, Marking marking) {

        super(sceneObject);
        this.grammar = grammar;
        this.marking = marking;
        this.universalVariableName = marking.getUniversalVariableName();
        this.requiredSelectors = marking.getRequiredSelectors();

        placeInitialMarkers(initialHc);
        computeFixpointOfMarkedHcs();
    }

    /**
     * @return The set of marked HeapConfigurations.
     */
    public Set<HeapConfiguration> getMarkedHcs() {

        return markedHeapConfigurations;
    }

    private void placeInitialMarkers(HeapConfiguration initialHc) {

        TIntSet nodesWithConstants = getNodesWithConstants(initialHc);

        TIntIterator nodeIter = initialHc.nodes().iterator();
        while (nodeIter.hasNext()) {
            int node = nodeIter.next();
            if (!nodesWithConstants.contains(node)) {
                HeapConfiguration hc = withUniversalMarking(initialHc, node);
                unexploredHeapConfigurations.push(hc);
            }
        }
    }

    private TIntSet getNodesWithConstants(HeapConfiguration hc) {

        TIntSet result = new TIntHashSet();

        TIntIterator iter = hc.variableEdges().iterator();
        while (iter.hasNext()) {
            int var = iter.next();
            String label = hc.nameOf(var);
            if (Constants.isConstant(label)) {
                result.add(hc.targetOf(var));
            }
        }
        return result;
    }

    private HeapConfiguration withUniversalMarking(HeapConfiguration hc, int node) {

        return hc
                .clone()
                .builder()
                .addVariableEdge(universalVariableName, node)
                .build();
    }

    private void computeFixpointOfMarkedHcs() {

        while (!unexploredHeapConfigurations.isEmpty()) {
            nextCurrent();
            if (isCurrentNodeFullyConcrete() && completeCurrentMarking()) {
                HeapConfiguration canonicalHc = canonicalizeCurrent(currentHc);
                if (markedHeapConfigurations.add(canonicalHc)) {

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

    private boolean completeCurrentMarking() {

        List<SelectorLabel> availableSelectors = currentHc.selectorLabelsOf(currentNode);

        if (marking.isMarkAllSuccessors()) {
            HeapConfigurationBuilder builder = currentHc.builder();
            for (SelectorLabel sel : availableSelectors) {
                builder.addVariableEdge(
                        marking.getSelectorVariableName(sel.getLabel()),
                        currentHc.selectorTargetOf(currentNode, sel)
                );
            }
            builder.build();
            return true;
        } else if (availableSelectors.containsAll(requiredSelectors)) {
            HeapConfigurationBuilder builder = currentHc.builder();
            for (SelectorLabel sel : requiredSelectors) {
                builder.addVariableEdge(
                        marking.getSelectorVariableName(sel.getLabel()),
                        currentHc.selectorTargetOf(currentNode, sel)
                );
            }
            builder.build();
            return true;
        }
        return marking.isMarkAllSuccessors() || requiredSelectors.isEmpty();
    }

    private HeapConfiguration canonicalizeCurrent(HeapConfiguration hc) {

        int minAbstractionDistance = (marking.isMarkAllSuccessors() || !marking.getRequiredSelectors().isEmpty()) ? 1 : 0;
        boolean aggressiveNullAbstraction = scene().options().getAggressiveNullAbstraction();

        for (Nonterminal lhs : grammar.getAllLeftHandSides()) {
            for (HeapConfiguration rhs : grammar.getRightHandSidesFor(lhs)) {
                AbstractMatchingChecker checker = hc.getEmbeddingsOf(rhs, minAbstractionDistance, aggressiveNullAbstraction);
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

    private void moveCurrentNodeToEachSuccessor() {

        HeapConfiguration cleanHc = withoutMarkings(currentHc);
        for (SelectorLabel sel : cleanHc.selectorLabelsOf(currentNode)) {
            int successorNode = cleanHc.selectorTargetOf(currentNode, sel);
            unexploredHeapConfigurations.push(withUniversalMarking(cleanHc, successorNode));
        }
    }

    private HeapConfiguration withoutMarkings(HeapConfiguration hc) {

        HeapConfigurationBuilder builder = hc.clone().builder();
        int var = hc.variableWith(universalVariableName);
        builder.removeVariableEdge(var);
        for (SelectorLabel sel : hc.selectorLabelsOf(currentNode)) {
            var = hc.variableWith(marking.getSelectorVariableName(sel.getLabel()));
            if (var != HeapConfiguration.INVALID_ELEMENT) {
                builder.removeVariableEdge(var);
            }
        }
        return builder.build();
    }

    private void materializeCurrentNode() {

        TIntIntIterator iter = nonReductionTentacles.iterator();
        if (iter.hasNext()) {
            iter.advance();
            int edge = iter.key();
            int tentacle = iter.value();
            Nonterminal label = currentHc.labelOf(edge);
            materialize(edge, label, tentacle);
        }
    }

    private void materialize(int edge, Nonterminal edgeLabel, int tentacle) {

        assert (edgeLabel.getRank() > tentacle);

        for (HeapConfiguration rhs : grammar.getRightHandSidesFor(edgeLabel)) {

            int ext = rhs.externalNodeAt(tentacle);
            if (!rhs.selectorLabelsOf(ext).isEmpty()) {

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
