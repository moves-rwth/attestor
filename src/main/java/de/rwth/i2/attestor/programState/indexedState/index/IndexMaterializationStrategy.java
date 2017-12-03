package de.rwth.i2.attestor.programState.indexedState.index;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminal;

import java.util.List;

public interface IndexMaterializationStrategy {

    IndexedNonterminal materializeIndex(IndexedNonterminal nt, IndexSymbol s);

    List<IndexSymbol> getRuleCreatingSymbolFor(IndexSymbol s1, IndexSymbol s2);

    void materializeIndices(HeapConfiguration heapConfiguration, IndexSymbol originalIndexSymbol,
                            IndexSymbol desiredIndexSymbol);

    boolean canCreateSymbolFor(IndexSymbol originalIndexSymbol, IndexSymbol desiredIndexSymbol);

}
