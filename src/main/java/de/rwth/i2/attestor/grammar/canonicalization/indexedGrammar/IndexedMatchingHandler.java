package de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar;

import java.util.Set;

import de.rwth.i2.attestor.grammar.canonicalization.MatchingHandler;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.Semantics;

public class IndexedMatchingHandler implements MatchingHandler {

	@Override
	public Set<ProgramState> tryReplaceMatching(ProgramState state, HeapConfiguration rhs, Nonterminal lhs,
			Semantics semantics, boolean isConfluent) {
		// TODO Auto-generated method stub
		return null;
	}

}
