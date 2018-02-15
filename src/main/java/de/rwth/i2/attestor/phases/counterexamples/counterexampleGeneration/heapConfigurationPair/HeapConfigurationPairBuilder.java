package de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration.heapConfigurationPair;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.NonterminalEdgeBuilder;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;

public final class HeapConfigurationPairBuilder implements HeapConfigurationBuilder {

    private final HeapConfigurationPair hc;
    private final HeapConfigurationBuilder actualBuilder;
    private final HeapConfigurationBuilder partnerBuilder;
    private final TIntIntMap ntEdgeRelation;

    HeapConfigurationPairBuilder(HeapConfigurationPair hc) {

        this.hc = hc;
        this.actualBuilder = hc.actual.builder();
        this.partnerBuilder = hc.pairedHeapConfiguration.builder();
        this.ntEdgeRelation = hc.ntEdgeRelation;
    }

    @Override
    public HeapConfiguration build() {

        actualBuilder.build();
        partnerBuilder.build();
        hc.builder = null;
        return hc;
    }

    @Override
    public HeapConfigurationBuilder addNodes(Type type, int count, TIntArrayList buffer) {

        this.actualBuilder.addNodes(type, count, buffer);
        return this;
    }

    @Override
    public HeapConfigurationBuilder removeIsolatedNode(int node) {

        this.actualBuilder.removeIsolatedNode(node);
        return this;
    }

    @Override
    public HeapConfigurationBuilder removeNode(int node) {

        this.actualBuilder.removeNode(node);
        return this;
    }

    @Override
    public HeapConfigurationBuilder addSelector(int from, SelectorLabel sel, int to) {

        this.actualBuilder.addSelector(from, sel, to);
        return this;
    }

    @Override
    public HeapConfigurationBuilder removeSelector(int node, SelectorLabel sel) {

        this.actualBuilder.removeSelector(node, sel);
        return this;
    }

    @Override
    public HeapConfigurationBuilder replaceSelector(int node, SelectorLabel oldSel, SelectorLabel newSel) {

        this.actualBuilder.replaceSelector(node, oldSel, newSel);
        return this;
    }

    @Override
    public HeapConfigurationBuilder setExternal(int node) {

        this.actualBuilder.setExternal(node);
        return this;
    }

    @Override
    public HeapConfigurationBuilder unsetExternal(int node) {

        this.actualBuilder.unsetExternal(node);
        return this;
    }

    @Override
    public HeapConfigurationBuilder addVariableEdge(String name, int target) {

        this.actualBuilder.addVariableEdge(name, target);
        return this;
    }

    @Override
    public HeapConfigurationBuilder removeVariableEdge(int varEdge) {

        this.actualBuilder.removeVariableEdge(varEdge);
        return this;
    }

    @Override
    public HeapConfigurationBuilder addNonterminalEdge(Nonterminal label, TIntArrayList attachedNodes) {

        this.actualBuilder.addNonterminalEdge(label, attachedNodes);
        return this;
    }

    @Override
    public int addNonterminalEdgeAndReturnId(Nonterminal label, TIntArrayList attachedNodes) {

        return this.actualBuilder.addNonterminalEdgeAndReturnId(label, attachedNodes);
    }

    @Override
    public NonterminalEdgeBuilder addNonterminalEdge(Nonterminal nt) {

        return this.actualBuilder.addNonterminalEdge(nt);
    }

    @Override
    public HeapConfigurationBuilder removeNonterminalEdge(int ntEdge) {

        this.actualBuilder.removeNonterminalEdge(ntEdge);
        return this;
    }

    @Override
    public HeapConfigurationBuilder replaceNonterminal(int ntEdge, Nonterminal newNt) {

        this.actualBuilder.replaceNonterminal(ntEdge, newNt);
        return this;
    }

    @Override
    public HeapConfigurationBuilder replaceNonterminalEdge(int ntEdge, HeapConfiguration replacement) {

        this.actualBuilder.replaceNonterminalEdge(ntEdge, replacement);
        if(ntEdgeRelation.containsKey(ntEdge)) {
            int partnerEdge = ntEdgeRelation.get(ntEdge);
            this.partnerBuilder.replaceNonterminalEdge(partnerEdge, replacement);
            this.hc.updateNtRelation();
        }

        return this;
    }

    @Override
    public HeapConfigurationBuilder replaceMatching(Matching matching, Nonterminal nonterminal) {

        this.actualBuilder.replaceMatching(matching, nonterminal);
        return this;
    }

    @Override
    public HeapConfigurationBuilder replaceNodeType(int node, Type newType) {

        this.actualBuilder.replaceNodeType(node, newType);
        return this;
    }

    @Override
    public HeapConfigurationBuilder replaceMatchingWithCollapsedExternals(Matching embedding,
                                                                          Nonterminal nonterminal,
                                                                          TIntArrayList externalIndicesMap) {

        this.actualBuilder.replaceMatchingWithCollapsedExternals(embedding, nonterminal, externalIndicesMap);
        return this;
    }

    @Override
    public HeapConfigurationBuilder mergeExternals(TIntArrayList extIndicesMap) {

        this.actualBuilder.mergeExternals(extIndicesMap);
        return this;
    }
}
