package de.rwth.i2.attestor.grammar.canonicalization.defaultGrammar;

import java.util.HashSet;
import java.util.Set;

import de.rwth.i2.attestor.grammar.canonicalization.*;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.Semantics;

public class DefaultMatchingHandler implements MatchingHandler {
	
	public EmbeddingCheckerProvider provider;
	public MatchingReplacer replacer;

	
	public DefaultMatchingHandler(EmbeddingCheckerProvider provider, MatchingReplacer replacer) {
		super();
		this.provider = provider;
		this.replacer = replacer;
	}

	/* (non-Javadoc)
	 * @see de.rwth.i2.attestor.grammar.canonicalization.MatchingHandler#tryReplaceMatching(de.rwth.i2.attestor.stateSpaceGeneration.ProgramState, de.rwth.i2.attestor.graph.heap.HeapConfiguration, de.rwth.i2.attestor.graph.Nonterminal, de.rwth.i2.attestor.stateSpaceGeneration.Semantics, boolean)
	 */
	@Override
	public Set<ProgramState> tryReplaceMatching( ProgramState state, 
												 HeapConfiguration rhs, Nonterminal lhs,
												 Semantics semantics) {
		
		boolean success = false;
		Set<ProgramState> result  = new HashSet<>();

		AbstractMatchingChecker checker = 
				provider.getEmbeddingChecker(state.getHeap(), rhs, semantics);

		while( checker.hasNext() && ( !success ) ) {

			success = true;

			ProgramState toAbstract  = state;

			Matching embedding = checker.getNext();

			if( success ){
				ProgramState abstracted = replaceEmbeddingBy( toAbstract, embedding, lhs );
				result.add(abstracted);
			}
		}
		return result;
	}

	/**
	 * replaces the embedding in  abstracted by the given nonterminal
	 * 
	 * @param abstracted the outer graph.
	 * @param embedding the embedding of the inner graph in the outer graph
	 * @param nonterminal the nonterminal to replace the embedding
	 */
	private ProgramState replaceEmbeddingBy( ProgramState stateToAbstract, Matching embedding, Nonterminal nonterminal) {
		HeapConfiguration toAbstract = stateToAbstract.clone().getHeap();
		HeapConfiguration abstracted = toAbstract.clone().builder().replaceMatching( embedding, nonterminal ).build();
		return stateToAbstract.shallowCopyWithUpdateHeap( abstracted );
	}


}