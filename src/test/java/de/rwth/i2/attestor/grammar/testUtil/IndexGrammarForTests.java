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
	public IndexedNonterminal materializeIndex(IndexedNonterminal nt, IndexSymbol s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IndexSymbol> getRuleCreatingSymbolFor(IndexSymbol originalIndexSymbol, 
													  IndexSymbol desiredIndexSymbol ) {
		List<IndexSymbol> result = new ArrayList<>();
		result.add(desiredIndexSymbol);
		if( ! desiredIndexSymbol.isBottom() ){
			result.add( originalIndexSymbol );
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
