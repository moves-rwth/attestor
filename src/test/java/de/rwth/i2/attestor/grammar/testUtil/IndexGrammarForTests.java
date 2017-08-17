package de.rwth.i2.attestor.grammar.testUtil;

import java.util.ArrayList;
import java.util.List;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminal;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.IndexMaterializationStrategy;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.IndexSymbol;

public class IndexGrammarForTests implements IndexMaterializationStrategy {

	public IndexGrammarForTests() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IndexedNonterminal materializeStack(IndexedNonterminal nt, IndexSymbol s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IndexSymbol> getRuleCreatingSymbolFor(IndexSymbol originalStackSymbol, 
													  IndexSymbol desiredStackSymbol ) {
		List<IndexSymbol> result = new ArrayList<>();
		result.add(desiredStackSymbol);
		if( ! desiredStackSymbol.isBottom() ){
			result.add( originalStackSymbol );
		}
		return result;
	}

	@Override
	public void materializeStacks(HeapConfiguration heapConfiguration, IndexSymbol originalStackSymbol,
			IndexSymbol desiredStackSymbol) {
	}

	@Override
	public boolean canCreateSymbolFor(IndexSymbol originalStackSymbol, IndexSymbol desiredStackSymbol) {
		return true;
	}

}
