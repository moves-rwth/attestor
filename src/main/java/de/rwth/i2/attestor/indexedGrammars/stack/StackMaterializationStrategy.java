package de.rwth.i2.attestor.indexedGrammars.stack;

import java.util.List;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.indexedGrammars.IndexedNonterminal;

public interface StackMaterializationStrategy {

	public IndexedNonterminal materializeStack( IndexedNonterminal nt, StackSymbol s );
	public List<StackSymbol> getRuleCreatingSymbolFor(StackSymbol s1, StackSymbol s2);
	void materializeStacks(HeapConfiguration heapConfiguration, StackSymbol originalStackSymbol,
			StackSymbol desiredStackSymbol);
	public boolean canCreateSymbolFor(StackSymbol originalStackSymbol, StackSymbol desiredStackSymbol);

}
