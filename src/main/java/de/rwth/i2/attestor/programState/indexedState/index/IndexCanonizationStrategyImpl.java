package de.rwth.i2.attestor.programState.indexedState.index;


import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminal;
import de.rwth.i2.attestor.semantics.util.Constants;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;

import java.util.Set;

/**
 * A IndexCanonizationStrategy that uses a fixed right-regular string grammar given by the following rules:
 * <ul>
 * <li>X &#8594; sX</li>
 * <li>X &#8594; Z</li>
 * <li>Y &#8594; sY</li>
 * <li>Y &#8594; C</li>
 * </ul>
 * Furthermore, abstraction is specialized towards features of AVL trees if a HeapConfiguration containsSubsumingState
 * selectors "left" and "right".
 *
 * @author Hannah, Christoph
 */
public class IndexCanonizationStrategyImpl implements IndexCanonizationStrategy {

    private Set<String> nullPointerGuards;

    public IndexCanonizationStrategyImpl(Set<String> nullPointerGuards) {

        this.nullPointerGuards = nullPointerGuards;
    }

    /**
     * Abstracts the indices of all nonterminals in heapConfiguration simultaneously and as far as possible.
     * Actually alters the nonterminals in heapConfiguration (no clone is performed).
     * This method assumes that index abstraction is sound (e.g. it doesn't check whether anything is linked to null)
     */
    @Override
    public void canonizeIndex(HeapConfiguration heapConfiguration) {

        if (!isCanonicalizationAllowed(heapConfiguration)) {
            return;
        }

        boolean appliedAbstraction;
        do {
            appliedAbstraction = attemptAbstraction(heapConfiguration, "Z");
            appliedAbstraction |= attemptAbstraction(heapConfiguration, "X");
            appliedAbstraction |= attemptAbstraction(heapConfiguration, "C");
            appliedAbstraction |= attemptAbstraction(heapConfiguration, "Y");
        } while (appliedAbstraction);
    }

    /**
     * Attempt to abstract the given HeapConfiguration by abstracting the given index rightmost symbol.
     *
     * @param heapConfiguration The HeapConfiguration that should be abstracted.
     * @param indexLabel        The rightmost index symbol that should be part of the abstraction.
     * @return true if and only if abstraction actually has been applied.
     */
    private boolean attemptAbstraction(HeapConfiguration heapConfiguration, String indexLabel) {

        TIntArrayList ntEdges = heapConfiguration.nonterminalEdges();
        TIntArrayList applicableEdges = new TIntArrayList(ntEdges.size());

        TIntIterator iter = ntEdges.iterator();
        while (iter.hasNext()) {
            int edge = iter.next();
            if (!(heapConfiguration.labelOf(edge) instanceof IndexedNonterminal)) {
                continue;
            }
            IndexedNonterminal edgeLabel = (IndexedNonterminal) heapConfiguration.labelOf(edge);

            if (isAbstractionPossible(edgeLabel, indexLabel)) {
                if (isAbstractionApplicable(edgeLabel, indexLabel)) {
                    applicableEdges.add(edge);
                }
            } else {
                return false;
            }

        }

        return applyAbstraction(heapConfiguration, applicableEdges);
    }

    /**
     * Checks whether at least one abstraction step that abstracts the given indexLabel is possible at all.
     *
     * @param edgeLabel  An edge label that should be checked for possible abstractions.
     * @param indexLabel The rightmost symbol that should be abstracted.
     * @return true if and only if abstraction is possible at all.
     */
    private boolean isAbstractionPossible(IndexedNonterminal edgeLabel, String indexLabel) {

        String endEdge = edgeLabel.getIndex().getLastIndexSymbol().toString();

        return (
                (indexLabel.equals("Z") && !endEdge.equals("X"))
                        || (indexLabel.equals("C") && !endEdge.equals("Y"))
                        || (indexLabel.equals("X") && !endEdge.equals("Z") && (!endEdge.equals("X") || edgeLabel.getIndex().size() > 1))
                        || (indexLabel.equals("Y") && !endEdge.equals("C") && (!endEdge.equals("Y") || edgeLabel.getIndex().size() > 1))
        );
    }

    /**
     * Checks whether abstraction with the given rightmost symbol is applicable to the given Nonterminal.
     *
     * @param edgeLabel  The Nonterminal that should be abstracted.
     * @param indexLabel The label of the rightmost IndexSymbol that should be abstracted.
     * @return true if and only if a hyperedge labeled with edgeLabel should be abstracted.
     */
    private boolean isAbstractionApplicable(IndexedNonterminal edgeLabel, String indexLabel) {

        return edgeLabel.getIndex().getLastIndexSymbol().toString().equals(indexLabel)
                && (indexLabel.equals("Z") || indexLabel.equals("C") || edgeLabel.getIndex().size() > 1);
    }

    /**
     * Applies abstraction to the indices of the given edges.
     *
     * @param heapConfiguration The HeapConfiguration whose edges should be abstracted.
     * @param applicableEdges   A subset of edges of heapConfiguration that are actually abstracted.
     * @return true if and only if the abstraction has been successfully executed.
     */
    private boolean applyAbstraction(HeapConfiguration heapConfiguration, TIntArrayList applicableEdges) {

        if (applicableEdges.isEmpty()) {
            return false;
        }

        applicableEdges.forEach(
                edge -> heapConfiguration
                        .builder()
                        .replaceNonterminal(
                                edge,
                                updateNonterminal((IndexedNonterminal) heapConfiguration.labelOf(edge))
                        ) != null // need a boolean return value
        );

        return true;
    }

    /**
     * Creates an IndexedNonterminal with an updated index according to the abstraction rules
     * inferred from the underlying context-free string grammar.
     *
     * @param originalNonterminal The nonterminal symbol whose index should be abstracted.
     * @return A new IndexedNonterminal with an abstracted index.
     */
    private IndexedNonterminal updateNonterminal(IndexedNonterminal originalNonterminal) {

        String last = originalNonterminal.getIndex().getLastIndexSymbol().toString();

        if (last.equals("Z")) {
            return originalNonterminal
                    .getWithShortenedIndex() // Z
                    .getWithProlongedIndex(AbstractIndexSymbol.get("X")); // -> X
        }

        if (last.equals("X")) {
            return originalNonterminal
                    .getWithShortenedIndex() // X
                    .getWithShortenedIndex() // s
                    .getWithProlongedIndex(AbstractIndexSymbol.get("X")); // -> X
        }

        if (last.equals("C")) {
            return originalNonterminal
                    .getWithShortenedIndex() // C
                    .getWithProlongedIndex(AbstractIndexSymbol.get("Y")); // -> Y
        }

        if (last.equals("Y")) {
            return originalNonterminal
                    .getWithShortenedIndex() // Y
                    .getWithShortenedIndex() // s
                    .getWithProlongedIndex(AbstractIndexSymbol.get("Y")); // -> Y
        }

        throw new IllegalStateException("Unknown index symbol.");
    }


    /**
     * Specialized check that prevents abstractions if
     * selector edges to null exist.
     *
     * @param heapConfiguration The HeapConfiguration to which canonicalization should be applied.
     * @return true if and only if no selector edges labeled "left" or "right" to null exist.
     */
    private boolean isCanonicalizationAllowed(HeapConfiguration heapConfiguration) {

        if (nullPointerGuards.isEmpty()) {
            return true;
        }

        try {

            int varNull = heapConfiguration.variableWith(Constants.NULL);
            int nullNode = heapConfiguration.targetOf(varNull);

            TIntIterator iter = heapConfiguration.predecessorNodesOf(nullNode).iterator();
            while (iter.hasNext()) {
                int node = iter.next();

                for (SelectorLabel sel : heapConfiguration.selectorLabelsOf(node)) {

                    if (heapConfiguration.selectorTargetOf(node, sel) == nullNode) {
                        if (nullPointerGuards.contains(sel.getLabel())) {
                            return false;
                        }
                    }
                }
            }
            return true;
        } catch (NullPointerException | IllegalArgumentException e) {
            return true;
        }
    }
}
