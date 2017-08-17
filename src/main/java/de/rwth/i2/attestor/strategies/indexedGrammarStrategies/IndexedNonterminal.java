package de.rwth.i2.attestor.strategies.indexedGrammarStrategies;

import de.rwth.i2.attestor.graph.Nonterminal;

import java.util.List;

public interface IndexedNonterminal extends Nonterminal {


    Index getIndex();

    IndexedNonterminal getWithShortenedStack();

    IndexedNonterminal getWithProlongedStack(IndexSymbol s);

    IndexedNonterminal getWithInstantiation();

    /**
     * removes the last symbol (stackVariable () or abstractStackSymbol) and
     * adds all elements in postfix
     * @param postfix The postfix to prolong the index
     * @return The nonterminal with prolonged index
     */
    IndexedNonterminal getWithProlongedStack(List<IndexSymbol> postfix);

    IndexedNonterminal getWithStack(List<IndexSymbol> stack);
}
