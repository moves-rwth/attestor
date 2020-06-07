package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.heap.Matching;
import gnu.trove.list.array.TIntArrayList;

// TODO(mkh): refactor duplicates properly (?)
public class TAHeapConfigurationBuilder extends InternalHeapConfigurationBuilder {
    TAHeapConfiguration heapConf;

    TAHeapConfigurationBuilder(TAHeapConfiguration heapConf) {
        super(heapConf);
        this.heapConf = heapConf;
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

        Nonterminal label = heapConf.labelOf(ntEdge);
        removeNonterminalEdge(ntEdge);

        TIntArrayList newElements = computeNewElements(replacementHc, tentacles);

        addReplacementGraph(replacementHc, newElements);
        saveNonterminalReplacement(ntEdge, label, newElements, replacementHc);

        return this;

    }

    @Override
    protected void addReplacementGraph(InternalHeapConfiguration replacement, TIntArrayList newElements) {
        int replSize = replacement.graph.size();

        // In the second pass we add all selectors for nodes as well as nonterminal hyperedges and their tentacles.
        for (int i = 0; i < replSize; i++) {
            if (replacement.isNode(i)) {
                newElements.set(i, addNodeFromReplacement(replacement, newElements, i));

            } else if (replacement.isNonterminalEdge(i)) {
                newElements.set(i, addNtEdgeFromReplacement(replacement, newElements, i));

            } else if (replacement.isVariable(i)) {
                newElements.set(i, addVariableFromReplacement(replacement, newElements, i));
            }
        }
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

        int ntEdge = heapConf.getPublicId(addMatchingNonterminalEdge(internalMatching, pattern, nonterminal));
        saveNonterminalInsertion(ntEdge, nonterminal, internalMatching);

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
        saveNonterminalInsertion(ntEdge, nonterminal, internalMatching);

        return this;
    }


    private void saveNonterminalReplacement(int ntEdge, Nonterminal label, TIntArrayList newElements, HeapConfiguration replacement) {
        TIntArrayList publicIdMapping = new TIntArrayList(newElements);
        publicIdMapping.transformValues(i -> i == -1 ? -1 : heapConf.getPublicId(i));
        heapConf.addTransformationStep(new HeapTransformation.NonterminalReplacement(ntEdge, label, replacement, publicIdMapping));
    }

    private void saveNonterminalInsertion(int ntEdge, Nonterminal label, Matching matching) {
        heapConf.addTransformationStep(new HeapTransformation.NonterminalInsertion(ntEdge, label, matching.pattern(), matching));
    }
}
