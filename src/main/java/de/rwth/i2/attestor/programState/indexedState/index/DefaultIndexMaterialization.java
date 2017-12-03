package de.rwth.i2.attestor.programState.indexedState.index;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminal;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.List;

/**
 * A IndexMaterializationStrategy that uses a fixed right-regular string grammar
 * given by the following rules:
 * <ul>
 * <li>X &#8594; sX</li>
 * <li>X &#8594; Z</li>
 * <li>Y &#8594; sY</li>
 * <li>Y &#8594; C</li>
 * </ul>
 *
 * @author Hannah, Christoph
 */
public class DefaultIndexMaterialization implements IndexMaterializationStrategy {

    public static final AbstractIndexSymbol SYMBOL_X = AbstractIndexSymbol.get("X");
    public static final AbstractIndexSymbol SYMBOL_Y = AbstractIndexSymbol.get("Y");
    public static final IndexSymbol SYMBOL_s = ConcreteIndexSymbol.getIndexSymbol("s", false);
    public static final IndexSymbol SYMBOL_Z = ConcreteIndexSymbol.getIndexSymbol("Z", true);
    public static final IndexSymbol SYMBOL_C = ConcreteIndexSymbol.getIndexSymbol("C", true);

    /**
     * Checks whether the originalIndexSymbol may be materialized to get the desiredIndexSymbol
     * according to the rules of the grammar.
     *
     * @param originalIndexSymbol The original (abstract) IndexSymbol that should be replaced.
     * @param desiredIndexSymbol  The desired IndexSymbol that should be obtained through
     *                            materialization.
     */
    private static void checkRules(IndexSymbol originalIndexSymbol, IndexSymbol desiredIndexSymbol) {

        String original = originalIndexSymbol.toString();
        String desired = desiredIndexSymbol.toString();

        assert (
                (original.equals("X") && (desired.equals("s") || desired.equals("Z")))
                        || (original.equals("Y") && (desired.equals("s") || desired.equals("C")))
        );
    }

    /**
     * Performs a sanity check to ensure that index materialization to replace
     * the given IndexSymbol is actually applicable to the given HeapConfiguration.
     *
     * @param heapConfiguration The HeapConfiguration that should be checked.
     * @param indexSymbol       The indexSymbol that should be materialized.
     */
    private static void checkConsistency(HeapConfiguration heapConfiguration, IndexSymbol indexSymbol) {

        assert (indexSymbol instanceof AbstractIndexSymbol);

        TIntArrayList ntEdges = heapConfiguration.nonterminalEdges();
        TIntIterator iter = ntEdges.iterator();
        while (iter.hasNext()) {
            int edge = iter.next();
            IndexedNonterminal l = (IndexedNonterminal) heapConfiguration.labelOf(edge);

            assert (!l.getIndex().hasConcreteIndex() || !l.getIndex().endsWith(indexSymbol));
        }
    }

    @Override
    public void materializeIndices(HeapConfiguration heapConfiguration,
                                   IndexSymbol originalIndexSymbol,
                                   IndexSymbol desiredIndexSymbol) {

        checkRules(originalIndexSymbol, desiredIndexSymbol);
        checkConsistency(heapConfiguration, originalIndexSymbol);

        TIntArrayList ntEdges = heapConfiguration.nonterminalEdges();
        TIntIterator iter = ntEdges.iterator();
        while (iter.hasNext()) {
            int edge = iter.next();
            IndexedNonterminal indexedNonterminal = (IndexedNonterminal) heapConfiguration.labelOf(edge);
            if (indexedNonterminal.getIndex().endsWith(originalIndexSymbol)) {

                IndexedNonterminal updatedNt = getNonterminalWithUpdatedIndex(indexedNonterminal,
                        desiredIndexSymbol, originalIndexSymbol);

                heapConfiguration.builder()
                        .replaceNonterminal(
                                edge,
                                updatedNt
                        )
                        .build();
            }
        }
    }

    /**
     * Materializes an IndexedNonterminal by adding the desired index symbol.
     *
     * @param nonterminal         The nonterminal whose index should be materialized.
     * @param desiredIndexSymbol  The IndexSymbol that should be added through materialization.
     * @param originalIndexSymbol The IndexSymbol that is materialized.
     * @return A new IndexedNonterminal with an updated index.
     */
    private IndexedNonterminal getNonterminalWithUpdatedIndex(IndexedNonterminal nonterminal,
                                                              IndexSymbol desiredIndexSymbol,
                                                              IndexSymbol originalIndexSymbol) {

        IndexedNonterminal result = nonterminal.getWithShortenedIndex().
                getWithProlongedIndex(desiredIndexSymbol);
        if (!desiredIndexSymbol.isBottom()) {
            result = result.getWithProlongedIndex(originalIndexSymbol);
        }
        return result;
    }

    @Override
    public IndexedNonterminal materializeIndex(IndexedNonterminal nt, IndexSymbol s) {

        assert (!nt.getIndex().hasConcreteIndex());
        nt = nt.getWithShortenedIndex().getWithProlongedIndex(s);
        if (!s.isBottom()) {
            nt = nt.getWithProlongedIndex(AbstractIndexSymbol.get("X"));
        }
        return nt;
    }

    @Override
    public List<IndexSymbol> getRuleCreatingSymbolFor(IndexSymbol originalIndexSymbol,
                                                      IndexSymbol desiredIndexSymbol) {

        List<IndexSymbol> result = new ArrayList<>();
        if (originalIndexSymbol.equals(SYMBOL_X)) {
            if (desiredIndexSymbol.equals(SYMBOL_s)) {
                result.add(SYMBOL_s);
                result.add(SYMBOL_X);
            } else if (desiredIndexSymbol.equals(SYMBOL_Z)) {
                result.add(SYMBOL_Z);
            }
        } else if (originalIndexSymbol.equals(SYMBOL_Y)) {
            if (desiredIndexSymbol.equals(SYMBOL_s)) {
                result.add(SYMBOL_s);
                result.add(SYMBOL_Y);
            } else if (desiredIndexSymbol.equals(SYMBOL_C)) {
                result.add(SYMBOL_C);
            }
        }
        return result;
    }

    @Override
    public boolean canCreateSymbolFor(IndexSymbol originalIndexSymbol, IndexSymbol desiredIndexSymbol) {


        return (
                (originalIndexSymbol.equals(SYMBOL_X)
                        && (desiredIndexSymbol.equals(SYMBOL_s)
                        || desiredIndexSymbol.equals(SYMBOL_Z))
                )
                        ||
                        (originalIndexSymbol.equals(SYMBOL_Y)
                                && (desiredIndexSymbol.equals(SYMBOL_s)
                                || desiredIndexSymbol.equals(SYMBOL_C)
                        )
                        )
        );
    }


}
