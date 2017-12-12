package de.rwth.i2.attestor.grammar.materialization.indexedGrammar;

import de.rwth.i2.attestor.grammar.materialization.communication.CannotMaterializeException;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminal;
import de.rwth.i2.attestor.programState.indexedState.index.AbstractIndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.IndexSymbol;
import gnu.trove.iterator.TIntIterator;

import java.util.List;

/**
 * Responsible for applying the materialization rules to the indices of the nonterminals
 * in the graph.
 * <p>
 * Example:
 * For input rule X &#8594; ssZ it replaces the index symbol X in all nonterminals where it occurs
 * by ssZ.
 *
 * @author Hannah
 */
public class IndexMaterializationStrategy {

    /**
     * Applies the given materialization ( e.g. X &#8594; ssX ) to all indexed nonterminals to a
     * copy of the graph.
     *
     * @param inputGraph                  the graph to materialize
     * @param symbolToMaterialize         The abstract index symbol which shall be materialized
     * @param inputMaterializationPostfix the sequence of index symbols for materialization
     * @return a materialized copy of the graph if the mateterialization is non empty.
     * The graph itself otherwise.
     * @throws CannotMaterializeException if a nonterminal unexpectedly has a concrete index
     */
    public HeapConfiguration getMaterializedCloneWith(HeapConfiguration inputGraph,
                                                      AbstractIndexSymbol symbolToMaterialize, List<IndexSymbol> inputMaterializationPostfix) throws CannotMaterializeException {

        if (!inputMaterializationPostfix.isEmpty()) {

            return computeCloneWithAppliedMaterializationOf(inputGraph,
                    symbolToMaterialize,
                    inputMaterializationPostfix);

        } else {
            return inputGraph;
        }
    }


    /**
     * Creates a clone of the input graph and applies the given materialization to
     * all indexed nonterminals in this clone
     *
     * @param inputGraph                  the original graph
     * @param symbolToMaterialize         The abstract index symbol which shall be materialized
     * @param inputMaterializationPostfix the sequence of index symbols
     *                                    with which to materialize
     * @return a clone of the original graph with applied materialization
     * @throws CannotMaterializeException if one of the indexed nonterminals in the
     *                                    graph has a concrete index and can therefore not be materialized
     */
    private HeapConfiguration computeCloneWithAppliedMaterializationOf(HeapConfiguration inputGraph,
                                                                       AbstractIndexSymbol symbolToMaterialize, List<IndexSymbol> inputMaterializationPostfix) throws CannotMaterializeException {


        final HeapConfiguration clone = inputGraph.clone();
        return applyMaterializationToIndexedNonterminalsOf(clone, symbolToMaterialize, inputMaterializationPostfix);
    }

    /**
     * replaces all indexed nonterminals in the graph with a copy containing
     * the materialized index
     *
     * @param inputGraph                  the graph in which the nonterminals will be replaced
     * @param symbolToMaterialize
     * @param inputMaterializationPostfix the sequence of index symbols with which to materialize
     * @return the graph with all indexed nonterminals properly replaced
     * @throws CannotMaterializeException if one of the indexed nonterminals in the graph
     *                                    as a concrete index and can therefore not be materialized
     */
    private HeapConfiguration applyMaterializationToIndexedNonterminalsOf(
            final HeapConfiguration inputGraph,
            AbstractIndexSymbol symbolToMaterialize, List<IndexSymbol> inputMaterializationPostfix) throws CannotMaterializeException {

        HeapConfigurationBuilder builder = inputGraph.builder();
        TIntIterator edgeIter = inputGraph.nonterminalEdges().iterator();
        while (edgeIter.hasNext()) {
            int indexOfNonterminal = edgeIter.next();
            Nonterminal nonterminal = inputGraph.labelOf(indexOfNonterminal);
            if (nonterminal instanceof IndexedNonterminal) {
                IndexedNonterminal nonterminalToMaterialize = (IndexedNonterminal) nonterminal;
                if (nonterminalToMaterialize.getIndex().getLastIndexSymbol().equals(symbolToMaterialize)) {

                    Nonterminal nonterminalWithMaterializedStack =
                            computeMaterializedCopyOf(nonterminalToMaterialize, inputMaterializationPostfix);
                    builder.replaceNonterminal(indexOfNonterminal, nonterminalWithMaterializedStack);
                }

            }
        }
        return builder.build();
    }

    /**
     * Applies the given materialization sequence to a copy of the given indexed nonterminal.
     * The original nonterminal is unmodified.
     *
     * @param nonterminalToMaterialize    the indexed nonterminal to which the materialization
     *                                    shall be applied
     * @param inputMaterializationPostfix the sequence of index symbols with which the
     *                                    index shall be materialized
     * @return a copy indexed nonterminal with the given materialization applied
     * @throws CannotMaterializeException if the nonterminal has a concrete index
     */
    private Nonterminal computeMaterializedCopyOf(IndexedNonterminal nonterminalToMaterialize,
                                                  List<IndexSymbol> inputMaterializationPostfix) throws CannotMaterializeException {


        if (nonterminalToMaterialize.getIndex().hasConcreteIndex()) {
            throw new CannotMaterializeException(nonterminalToMaterialize.toString() + "has a concrete index");
        }

        return nonterminalToMaterialize.getWithProlongedIndex(inputMaterializationPostfix);
    }


}
