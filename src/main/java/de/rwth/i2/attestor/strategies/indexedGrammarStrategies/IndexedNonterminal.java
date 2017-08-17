package de.rwth.i2.attestor.strategies.indexedGrammarStrategies;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack.Index;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack.IndexSymbol;

import java.util.List;

public interface IndexedNonterminal extends Nonterminal {

    Index getStack();

    IndexedNonterminal getWithShortenedStack();

    IndexedNonterminal getWithProlongedStack(IndexSymbol s);

    IndexedNonterminal getWithInstantiation();

    /**
     * removes the last symbol (stackVariable () or abstractStackSymbol) and
     * adds all elements in postfix
     * @param postfix The postfix to prolong the stack
     * @return The nonterminal with prolonged stack
     */
    IndexedNonterminal getWithProlongedStack(List<IndexSymbol> postfix);

    IndexedNonterminal getWithStack(List<IndexSymbol> stack);
}
