package de.rwth.i2.attestor.programState.indexedState;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import gnu.trove.iterator.TIntIterator;

public class IndexedHeapConfigurationUtil {

    public static boolean hasInstantiatiatedIndices(HeapConfiguration hc) {

        TIntIterator ntIterator = hc.nonterminalEdges().iterator();
        while (ntIterator.hasNext()) {
            int ntId = ntIterator.next();
            Nonterminal nt = hc.labelOf(ntId);
            if (nt instanceof IndexedNonterminal) {
                IndexedNonterminal indexedNt = (IndexedNonterminal) nt;
                if (!indexedNt.getIndex().hasConcreteIndex()) {
                    return false;
                }
            }
        }
        return true;
    }
}
