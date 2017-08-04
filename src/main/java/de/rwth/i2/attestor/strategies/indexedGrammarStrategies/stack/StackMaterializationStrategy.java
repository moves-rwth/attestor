package de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminal;

import java.util.List;

public interface StackMaterializationStrategy {

	IndexedNonterminal materializeStack( IndexedNonterminal nt, StackSymbol s );
	List<StackSymbol> getRuleCreatingSymbolFor(StackSymbol s1, StackSymbol s2);
	void materializeStacks(HeapConfiguration heapConfiguration, StackSymbol originalStackSymbol,
			StackSymbol desiredStackSymbol);
	boolean canCreateSymbolFor(StackSymbol originalStackSymbol, StackSymbol desiredStackSymbol);

}
