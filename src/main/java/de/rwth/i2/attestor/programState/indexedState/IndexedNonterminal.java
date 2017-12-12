package de.rwth.i2.attestor.programState.indexedState;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.programState.indexedState.index.Index;
import de.rwth.i2.attestor.programState.indexedState.index.IndexSymbol;

import java.util.List;

public interface IndexedNonterminal extends Nonterminal {


    Index getIndex();

    IndexedNonterminal getWithShortenedIndex();

    IndexedNonterminal getWithProlongedIndex(IndexSymbol s);


    /**
     * removes the last symbol (indexVariable () or abstractIndexSymbol) and
     * adds all elements in postfix
     *
     * @param postfix The postfix to prolong the index
     * @return The nonterminal with prolonged index
     */
    IndexedNonterminal getWithProlongedIndex(List<IndexSymbol> postfix);

    IndexedNonterminal getWithIndex(List<IndexSymbol> index);
}
