package de.rwth.i2.attestor.grammar.testUtil;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminal;
import de.rwth.i2.attestor.programState.indexedState.index.IndexMaterializationStrategy;
import de.rwth.i2.attestor.programState.indexedState.index.IndexSymbol;

import java.util.ArrayList;
import java.util.List;

public class IndexGrammarForTests implements IndexMaterializationStrategy {

    public IndexGrammarForTests() {

    }

    @Override
    public IndexedNonterminal materializeIndex(IndexedNonterminal nt, IndexSymbol s) {

        return null;
    }

    @Override
    public List<IndexSymbol> getRuleCreatingSymbolFor(IndexSymbol originalIndexSymbol,
                                                      IndexSymbol desiredIndexSymbol) {

        List<IndexSymbol> result = new ArrayList<>();
        result.add(desiredIndexSymbol);
        if (!desiredIndexSymbol.isBottom()) {
            result.add(originalIndexSymbol);
        }
        return result;
    }

    @Override
    public void materializeIndices(HeapConfiguration heapConfiguration, IndexSymbol originalIndexSymbol,
                                   IndexSymbol desiredIndexSymbol) {

    }

    @Override
    public boolean canCreateSymbolFor(IndexSymbol originalIndexSymbol, IndexSymbol desiredIndexSymbol) {

        return true;
    }

}
