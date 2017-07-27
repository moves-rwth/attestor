package de.rwth.i2.attestor.grammar.canonicalization;

import de.rwth.i2.attestor.grammar.StackMatcher;
import de.rwth.i2.attestor.grammar.canoncalization.StackEmbeddingResult;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.Matching;

public class EmbeddingStackChecker {

	StackMatcher stackMatcher;
	
	public EmbeddingStackChecker(StackMatcher stackMatcher) {
		this.stackMatcher = stackMatcher;
	}

	public StackEmbeddingResult getStackEmbeddingResult(Matching embedding, Nonterminal lhs) {
		// TODO Auto-generated method stub
		return null;
	}

}
