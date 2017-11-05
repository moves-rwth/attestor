package de.rwth.i2.attestor.counterexamples.heapConfWithPartner;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.List;

/**
 * A HeapConfigurationWithPartner actually consists of two heap configurations that are materialized together.
 * All methods except for hyperedge replacement are only applied to the actual HeapConfiguration and not to its partner
 *
 * @author Christoph
 */
public final class HeapConfigurationWithPartner implements HeapConfiguration {

    HeapConfiguration actual; // the actual HeapConfiguration visible through all methods
    HeapConfiguration partner;
    TIntIntMap ntEdgeRelation;
    HeapConfigurationWithPartnerBuilder builder;

    public HeapConfigurationWithPartner(HeapConfiguration actual, HeapConfiguration partner) {

        this.actual = actual;
        this.partner = partner;
        this.ntEdgeRelation = new TIntIntHashMap();
        updateNtRelation();
    }

    void updateNtRelation() {
        TIntArrayList actualEdges = actual.nonterminalEdges();
        TIntArrayList partnerEdges = partner.nonterminalEdges();
        ntEdgeRelation.clear();

        for(int i=0; i < actualEdges.size(); i++) {
            ntEdgeRelation.put(actualEdges.get(i), partnerEdges.get(i));
        }
    }

    protected HeapConfigurationWithPartner(HeapConfigurationWithPartner hc) {

        this.actual = hc.actual.clone();
        this.partner = hc.partner.clone();
        this.ntEdgeRelation = new TIntIntHashMap(hc.ntEdgeRelation);
    }

    public HeapConfiguration getPartner() {

        return partner;
    }


    @Override
    public HeapConfiguration clone() {

        return new HeapConfigurationWithPartner(this);
    }

    @Override
    public HeapConfigurationBuilder builder() {

        if(builder == null) {
            builder = new HeapConfigurationWithPartnerBuilder(this);
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
    public AbstractMatchingChecker getEmbeddingsOf(HeapConfiguration pattern, int minAbstractionDistance) {

        return actual.getEmbeddingsOf(pattern, minAbstractionDistance);
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

        return actual.equals(otherObject);
    }

    @Override
    public int hashCode() {

        return actual.hashCode();
    }
}
