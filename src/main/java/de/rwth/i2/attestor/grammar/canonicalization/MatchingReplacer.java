package de.rwth.i2.attestor.grammar.canonicalization;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;

public interface MatchingReplacer {

	HeapConfiguration replaceIn(HeapConfiguration graph, Nonterminal lhs, Matching matching);

}
