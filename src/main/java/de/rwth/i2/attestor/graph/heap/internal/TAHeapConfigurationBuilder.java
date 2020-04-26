package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.heap.Matching;
import gnu.trove.list.array.TIntArrayList;

// TODO(mkh): refactor duplicates properly
public class TAHeapConfigurationBuilder extends InternalHeapConfigurationBuilder {
    TAHeapConfigurationBuilder(TAHeapConfiguration heapConf) {
        super(heapConf);
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public HeapConfigurationBuilder replaceNonterminalEdge(int ntEdge, HeapConfiguration replacement) {
        if (replacement == null) {
            throw new NullPointerException();
        }

        if (!(replacement instanceof InternalHeapConfiguration)) {
            throw new IllegalArgumentException("Provided replacement is not an InternalHeapConfiguration.");
        }

        InternalHeapConfiguration replacementHc = (InternalHeapConfiguration) replacement;
        int ntPrivateId = heapConf.getPrivateId(ntEdge);

        if (!heapConf.isNonterminalEdge(ntPrivateId)) {
            throw new IllegalArgumentException("Provided ID does not correspond to a nonterminal edge.");
        }

        // store originally attached nodes, because these are merged with the external nodes of replacementHc.
        TIntArrayList tentacles = heapConf.graph.successorsOf(ntPrivateId);

        if (tentacles.size() != replacement.countExternalNodes()) {
            throw new IllegalArgumentException("The rank of the nonterminal edge to be replaced " +
                    "does not match the rank of the replacement.");
        }

        removeNonterminalEdge(ntEdge);

        TIntArrayList newElements = computeNewElements(replacementHc, tentacles);

        saveMaterializationLog(ntEdge, newElements, replacementHc);
        addReplacementGraph(replacementHc, newElements);

        return this;

    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public HeapConfigurationBuilder replaceMatching(Matching matching, Nonterminal nonterminal) {
        if (matching == null || nonterminal == null) {
            throw new NullPointerException();
        }

        InternalHeapConfiguration pattern = (InternalHeapConfiguration) matching.pattern();

        if (pattern.countExternalNodes() != nonterminal.getRank()) {
            throw new IllegalArgumentException("The number of external nodes in pattern must " +
                    "match the rank of the provided nonterminal.");
        }

        InternalMatching internalMatching = (InternalMatching) matching;

        // First remove all selector edges and tentacles that also occur in pattern
        removeSelectorAndTentacleEdges(internalMatching, pattern);

        removeNonExternalNodes(internalMatching, pattern);

        int ntEdge = addMatchingNonterminalEdge(internalMatching, pattern, nonterminal);
        saveCanonicalizationLog(ntEdge, internalMatching);

        return this;
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public HeapConfigurationBuilder replaceMatchingWithCollapsedExternals(
            Matching matching, Nonterminal nonterminal, TIntArrayList externalIndicesMap) {

        if (matching == null || nonterminal == null) {
            throw new NullPointerException();
        }

        InternalHeapConfiguration pattern = (InternalHeapConfiguration) matching.pattern();

        // Use the mapping of externals instead of the actual smaller number of external nodes
        if (externalIndicesMap.size() != nonterminal.getRank()) {
            throw new IllegalArgumentException("The number of external nodes in pattern must " +
                    "match the rank of the provided nonterminal.");
        }

        InternalMatching internalMatching = (InternalMatching) matching;

        // First remove all selector edges and tentacles that also occur in pattern
        removeSelectorAndTentacleEdges(internalMatching, pattern);

        removeNonExternalNodes(internalMatching, pattern);

        int ntEdge = addMatchingNonterminalEdgeWithCollapsedExternals(internalMatching, pattern, nonterminal, externalIndicesMap);
        saveCanonicalizationLog(ntEdge, internalMatching);

        return this;
    }


    private void saveMaterializationLog(int ntEdge, TIntArrayList newElements, HeapConfiguration replacement) {
        TIntArrayList publicIdMapping = new TIntArrayList(newElements);
        publicIdMapping.transformValues(i -> i == -1 ? -1 : heapConf.getPublicId(i));
        ((TAHeapConfiguration) heapConf).addTransformationStep(new MaterializationStep(ntEdge, replacement, publicIdMapping));
    }

    private void saveCanonicalizationLog(int ntEdge, Matching matching) {
        ((TAHeapConfiguration) heapConf).addTransformationStep(new CanonicalizationStep(ntEdge, matching.pattern(), matching));
    }
}
