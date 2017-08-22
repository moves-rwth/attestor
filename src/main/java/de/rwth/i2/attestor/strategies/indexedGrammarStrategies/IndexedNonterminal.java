package de.rwth.i2.attestor.strategies.indexedGrammarStrategies;

import java.util.List;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.Index;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.IndexSymbol;

public interface IndexedNonterminal extends Nonterminal {


    Index getIndex();

    IndexedNonterminal getWithShortenedIndex();

    IndexedNonterminal getWithProlongedIndex(IndexSymbol s);

    IndexedNonterminal getWithInstantiation();

    /**
     * removes the last symbol (stackVariable () or abstractStackSymbol) and
     * adds all elements in postfix
     * @param postfix The postfix to prolong the index
     * @return The nonterminal with prolonged index
     */
    IndexedNonterminal getWithProlongedIndex(List<IndexSymbol> postfix);

    IndexedNonterminal getWithIndex(List<IndexSymbol> stack);
}
