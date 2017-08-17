package de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminal;

import java.util.List;

public interface StackMaterializationStrategy {

	IndexedNonterminal materializeStack( IndexedNonterminal nt, IndexSymbol s );
	List<IndexSymbol> getRuleCreatingSymbolFor(IndexSymbol s1, IndexSymbol s2);
	void materializeStacks(HeapConfiguration heapConfiguration, IndexSymbol originalStackSymbol,
			IndexSymbol desiredStackSymbol);
	boolean canCreateSymbolFor(IndexSymbol originalStackSymbol, IndexSymbol desiredStackSymbol);

}
