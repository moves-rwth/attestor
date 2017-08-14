

package de.rwth.i2.attestor.grammar.canonicalization;

import java.util.HashSet;
import java.util.Set;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.util.SingleElementUtil;

public class GeneralCanonicalizationStrategy implements CanonicalizationStrategy {

	private Grammar grammar; 
	private EmbeddingCheckerProvider provider; 
	private MatchingReplacer replacer;
	
	public GeneralCanonicalizationStrategy( Grammar grammar, 
											EmbeddingCheckerProvider provider, 
											MatchingReplacer replacer ) {
		
		this.grammar = grammar;
		this.provider = provider;
		this.replacer = replacer;
	}

	@Override
	public Set<ProgramState> canonicalize(Semantics semantics, ProgramState state ) {
		
		if( !semantics.permitsCanonicalization() ) { //TODO was ist hier gemeint??

			return SingleElementUtil.createSet( state );
		}
		
		return performCanonicalization( semantics, state );
	}

	private Set<ProgramState> performCanonicalization(Semantics semantics, ProgramState state) {
		
		Set<ProgramState> result = new HashSet<>();
		
		boolean isConfluent = grammar.isConfluent();
		boolean success = false;
		
		for( Nonterminal lhs : grammar.getAllLeftHandSides() ){
			
			if( success && isConfluent ) { break; }
			
			for( HeapConfiguration rhs : grammar.getRightHandSidesFor(lhs) ){
				
				if( success && isConfluent ) { break; }
				
				success = tryReplaceMatching(state, rhs, lhs, result, semantics, isConfluent );
			}			
		}
		
		if(result.isEmpty()) {	

			result.add(state);
		}

		return result;
	}

	private boolean tryReplaceMatching( ProgramState state, HeapConfiguration rhs, Nonterminal lhs,
										Set<ProgramState> result,
										Semantics semantics, boolean isConfluent ) {
		boolean success = false;
		
		AbstractMatchingChecker checker = 
				provider.getEmbeddingChecker(state.getHeap(), rhs, semantics);
		
		while(checker.hasNext() && ( !isConfluent || !success ) ) {
			
			success = true;
			
			ProgramState toAbstract  = state;
			
			Matching embedding = checker.getNext();

			
			if( success ){
				ProgramState abstracted = replaceEmbeddingBy( toAbstract, embedding, lhs );
				result.addAll( performCanonicalization( semantics, abstracted ) );
			}
		}
		return success;
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
		HeapConfiguration abstracted = replacer.replaceIn(toAbstract, nonterminal, embedding );
		return stateToAbstract.shallowCopyWithUpdateHeap( abstracted );
	}


}
