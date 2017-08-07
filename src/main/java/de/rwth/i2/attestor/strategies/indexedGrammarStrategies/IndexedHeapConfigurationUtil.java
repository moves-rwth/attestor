package de.rwth.i2.attestor.strategies.indexedGrammarStrategies;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import gnu.trove.iterator.TIntIterator;

public class IndexedHeapConfigurationUtil {

	public static boolean hasInstantiatiatedStacks( HeapConfiguration hc ){
		TIntIterator ntIterator = hc.nonterminalEdges().iterator();
		while( ntIterator.hasNext() ){
			int ntId = ntIterator.next();
			Nonterminal nt = hc.labelOf( ntId );
			if( nt instanceof IndexedNonterminal){
				IndexedNonterminal indexedNt = (IndexedNonterminal) nt ;
				if( ! indexedNt.getStack().hasConcreteStack() ){
					return false;
				}
			}
		}
		return true;
	}
}
