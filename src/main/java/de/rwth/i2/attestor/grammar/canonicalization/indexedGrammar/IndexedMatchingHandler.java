package de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar;

import java.util.HashSet;
import java.util.Set;

import de.rwth.i2.attestor.grammar.canonicalization.*;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.Semantics;

public class IndexedMatchingHandler implements MatchingHandler {

	public EmbeddingCheckerProvider checkerProvider;
	public EmbeddingStackChecker stackChecker;

	
	public IndexedMatchingHandler(EmbeddingCheckerProvider checkerProvider, EmbeddingStackChecker stackChecker) {
		super();
		this.checkerProvider = checkerProvider;
		this.stackChecker = stackChecker;
	}

	/* (non-Javadoc)
	 * @see de.rwth.i2.attestor.grammar.canonicalization.MatchingHandler#tryReplaceMatching(de.rwth.i2.attestor.stateSpaceGeneration.ProgramState, de.rwth.i2.attestor.graph.heap.HeapConfiguration, de.rwth.i2.attestor.graph.Nonterminal, de.rwth.i2.attestor.stateSpaceGeneration.Semantics, boolean)
	 */
	@Override
	public Set<ProgramState> tryReplaceMatching( ProgramState state, HeapConfiguration rhs, Nonterminal lhs,
			Semantics semantics, boolean isConfluent ) {
		
		boolean success = false;
		Set<ProgramState> result  = new HashSet<>();

		AbstractMatchingChecker checker = 
				checkerProvider.getEmbeddingChecker( state.getHeap(), rhs, semantics);

		while( checker.hasNext() && ( !isConfluent || !success ) ) {
		
			ProgramState toAbstract  = state;

			Matching embedding = checker.getNext();
			try {
				StackEmbeddingResult res = 
						stackChecker.getStackEmbeddingResult(toAbstract.getHeap(), embedding, lhs);
				success = true;
				HeapConfiguration abstracted = replaceEmbeddingBy( res.getMaterializedToAbstract(), 
															  embedding, res.getInstantiatedLhs() );
				ProgramState resultState = toAbstract.shallowCopyWithUpdateHeap( abstracted );
				result.add( resultState );
			}catch( CannotMatchException e ) {
				//this may happen. loop will continue.
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
	private HeapConfiguration replaceEmbeddingBy( HeapConfiguration toAbstract, Matching embedding, Nonterminal nonterminal) {
		toAbstract = toAbstract.clone();
		HeapConfiguration abstracted = toAbstract.builder()
											     .replaceMatching(embedding, nonterminal)
											     .build();
		return abstracted;
	}


}
