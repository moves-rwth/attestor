package de.rwth.i2.attestor.grammar.testUtil;

import java.util.ArrayList;
import java.util.List;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminal;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack.StackMaterializationStrategy;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack.StackSymbol;

public class StackGrammarForTests implements StackMaterializationStrategy {

	public StackGrammarForTests() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IndexedNonterminal materializeStack(IndexedNonterminal nt, StackSymbol s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<StackSymbol> getRuleCreatingSymbolFor(StackSymbol originalStackSymbol, 
													  StackSymbol desiredStackSymbol ) {
		List<StackSymbol> result = new ArrayList<>();
		result.add(desiredStackSymbol);
		if( ! desiredStackSymbol.isBottom() ){
			result.add( originalStackSymbol );
		}
		return result;
	}

	@Override
	public void materializeStacks(HeapConfiguration heapConfiguration, StackSymbol originalStackSymbol,
			StackSymbol desiredStackSymbol) {
	}

	@Override
	public boolean canCreateSymbolFor(StackSymbol originalStackSymbol, StackSymbol desiredStackSymbol) {
		return true;
	}

}
