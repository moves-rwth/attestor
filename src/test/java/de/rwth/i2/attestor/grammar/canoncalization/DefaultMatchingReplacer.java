package de.rwth.i2.attestor.grammar.canoncalization;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;

public class DefaultMatchingReplacer implements MatchingReplacer {

	@Override
	public HeapConfiguration replaceIn(HeapConfiguration graph, Nonterminal lhs,
			Matching matching) {
		
		return graph.clone().builder().replaceMatching( matching, lhs ).build();
	}

}
