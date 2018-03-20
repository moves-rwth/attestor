package de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration.heapConfigurationPair;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.digraph.NodeLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import de.rwth.i2.attestor.graph.morphism.Graph;
import de.rwth.i2.attestor.graph.morphism.MorphismOptions;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.List;

/**
 * A HeapConfigurationPair actually consists of two heap configurations that are materialized together.
 * All methodExecution except for hyperedge replacement are only applied to the actual HeapConfiguration and not to its pairedHeapConfiguration
 *
 * @author Christoph
 */
public final class HeapConfigurationPair implements HeapConfiguration, Graph {

    final HeapConfiguration actual; // the actual HeapConfiguration visible through all methodExecution
    final HeapConfiguration pairedHeapConfiguration;
    final TIntIntMap ntEdgeRelation;
    HeapConfigurationPairBuilder builder;

    public HeapConfigurationPair(HeapConfiguration actual, HeapConfiguration partner) {

        this.actual = actual;
        this.pairedHeapConfiguration = partner;
        this.ntEdgeRelation = new TIntIntHashMap();
        updateNtRelation();
    }

    private HeapConfigurationPair(HeapConfigurationPair hc) {

        this.actual = hc.actual.clone();
        this.pairedHeapConfiguration = hc.pairedHeapConfiguration.clone();
        this.ntEdgeRelation = new TIntIntHashMap(hc.ntEdgeRelation);
    }

    void updateNtRelation() {

        TIntArrayList actualEdges = actual.nonterminalEdges();
        TIntArrayList partnerEdges = pairedHeapConfiguration.nonterminalEdges();
        ntEdgeRelation.clear();

        for (int i = 0; i < actualEdges.size(); i++) {
            ntEdgeRelation.put(actualEdges.get(i), partnerEdges.get(i));
        }
    }

    public HeapConfiguration getPairedHeapConfiguration() {

        return pairedHeapConfiguration;
    }


    @Override
    public HeapConfiguration clone() {

        return new HeapConfigurationPair(this);
    }

    @Override
    public HeapConfiguration getEmpty() {
        return new InternalHeapConfiguration();
    }

    @Override
    public HeapConfigurationBuilder builder() {

        if (builder == null) {
            builder = new HeapConfigurationPairBuilder(this);
        }
        return builder;
    }

    @Override
    public int countNodes() {

        return actual.countNodes();
    }

    @Override
    public TIntArrayList nodes() {

        return actual.nodes();
    }

    @Override
    public Type nodeTypeOf(int node) {

        return actual.nodeTypeOf(node);
    }

    @Override
    public TIntArrayList attachedVariablesOf(int node) {

        return actual.attachedVariablesOf(node);
    }

    @Override
    public TIntArrayList attachedNonterminalEdgesOf(int node) {

        return actual.attachedNonterminalEdgesOf(node);
    }

    @Override
    public TIntArrayList successorNodesOf(int node) {

        return actual.successorNodesOf(node);
    }

    @Override
    public TIntArrayList predecessorNodesOf(int node) {

        return actual.predecessorNodesOf(node);
    }

    @Override
    public List<SelectorLabel> selectorLabelsOf(int node) {

        return actual.selectorLabelsOf(node);
    }

    @Override
    public int selectorTargetOf(int node, SelectorLabel sel) {

        return actual.selectorTargetOf(node, sel);
    }

    @Override
    public int countExternalNodes() {

        return actual.countExternalNodes();
    }

    @Override
    public TIntArrayList externalNodes() {

        return actual.externalNodes();
    }

    @Override
    public int externalNodeAt(int pos) {

        return actual.externalNodeAt(pos);
    }

    @Override
    public boolean isExternalNode(int node) {

        return actual.isExternalNode(node);
    }

    @Override
    public int externalIndexOf(int node) {

        return actual.externalIndexOf(node);
    }

    @Override
    public int countNonterminalEdges() {

        return actual.countNonterminalEdges();
    }

    @Override
    public TIntArrayList nonterminalEdges() {

        return actual.nonterminalEdges();
    }

    @Override
    public int rankOf(int ntEdge) {

        return actual.rankOf(ntEdge);
    }

    @Override
    public Nonterminal labelOf(int ntEdge) {

        return actual.labelOf(ntEdge);
    }

    @Override
    public TIntArrayList attachedNodesOf(int ntEdge) {

        return actual.attachedNodesOf(ntEdge);
    }

    @Override
    public int countVariableEdges() {

        return actual.countVariableEdges();
    }

    @Override
    public TIntArrayList variableEdges() {

        return actual.variableEdges();
    }

    @Override
    public int variableWith(String name) {

        return actual.variableWith(name);
    }

    @Override
    public String nameOf(int varEdge) {

        return actual.nameOf(varEdge);
    }

    @Override
    public int targetOf(int varEdge) {

        return actual.targetOf(varEdge);
    }

    @Override
    public AbstractMatchingChecker getEmbeddingsOf(HeapConfiguration pattern, MorphismOptions options) {

        return actual.getEmbeddingsOf(pattern, options);
    }

    @Override
    public int variableTargetOf(String variableName) {

        return actual.variableTargetOf(variableName);
    }

    @Override
    public TIntIntMap attachedNonterminalEdgesWithNonReductionTentacle(int node) {

        return actual.attachedNonterminalEdgesWithNonReductionTentacle(node);
    }

    @Override
    public String toString() {

        return actual.toString();
    }

    @Override
    public boolean equals(Object otherObject) {

        return otherObject == this || otherObject instanceof HeapConfiguration && actual.equals(otherObject);

    }

    @Override
    public int hashCode() {

        return actual.hashCode();
    }

    @Override
    public int size() {

        return ((Graph) actual).size();
    }

    @Override
    public boolean hasEdge(int from, int to) {

        return ((Graph) actual).hasEdge(from, to);
    }

    @Override
    public TIntArrayList getSuccessorsOf(int node) {

        return ((Graph) actual).getSuccessorsOf(node);
    }

    @Override
    public TIntArrayList getPredecessorsOf(int node) {

        return ((Graph) actual).getPredecessorsOf(node);
    }

    @Override
    public NodeLabel getNodeLabel(int node) {

        return ((Graph) actual).getNodeLabel(node);
    }

    @Override
    public List<Object> getEdgeLabel(int from, int to) {

        return ((Graph) actual).getEdgeLabel(from, to);
    }

    @Override
    public boolean isExternal(int node) {

        return ((Graph) actual).isExternal(node);
    }

    @Override
    public int getExternalIndex(int node) {

        return ((Graph) actual).getExternalIndex(node);
    }

    @Override
    public boolean isEdgeBetweenMarkedNodes(int from, int to) {

        return ((Graph) actual).isEdgeBetweenMarkedNodes(from, to);
    }
}
