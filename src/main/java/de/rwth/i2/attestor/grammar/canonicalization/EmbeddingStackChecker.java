package de.rwth.i2.attestor.grammar.canonicalization;

import de.rwth.i2.attestor.grammar.StackMatcher;
import de.rwth.i2.attestor.grammar.canoncalization.StackEmbeddingResult;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;

public class EmbeddingStackChecker {

	StackMatcher stackMatcher;
	
	public EmbeddingStackChecker(StackMatcher stackMatcher) {
		this.stackMatcher = stackMatcher;
	}

	public StackEmbeddingResult getStackEmbeddingResult( HeapConfiguration toAbstract, 
														 Matching embedding, 
														 Nonterminal lhs ) {
		return new StackEmbeddingResult(true, toAbstract, lhs );
	}

}
