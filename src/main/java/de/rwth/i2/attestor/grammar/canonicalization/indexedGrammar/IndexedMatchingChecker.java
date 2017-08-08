package de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar;

import de.rwth.i2.attestor.grammar.canonicalization.CannotMatchException;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import de.rwth.i2.attestor.graph.morphism.MorphismChecker;

public class IndexedMatchingChecker extends AbstractMatchingChecker {

	EmbeddingStackChecker stackChecker;
	boolean hasMorphism = false;
	Matching next;
	
	IndexedMatchingChecker( HeapConfiguration pattern,
							HeapConfiguration target,
							MorphismChecker graphChecker,
							EmbeddingStackChecker stackChecker ) {
		
		super( pattern, target, graphChecker );
		this.stackChecker = stackChecker;
	}

	
	@Override
	public boolean hasMatching() {
		
		while( !hasMorphism && super.hasNext() ){
			
			Matching morphismCandidate = super.getNext();
			try{
				//TODO
				stackChecker.getStackEmbeddingResult(super.getTarget(), morphismCandidate, null);
				next = morphismCandidate;
				hasMorphism = true;
			}catch( CannotMatchException e ){
				//expected to happen
			}
		}
		
		return hasMorphism;
	}
	
	@Override
	public boolean hasNext() {
		if( next != null ){
			return true;
		}
		
		boolean hasNext = false;
		while( !hasNext && super.hasNext() ){
			
			Matching morphismCandidate = super.getNext();
			try{
				//TODO
				stackChecker.getStackEmbeddingResult(super.getTarget(), morphismCandidate, null);
				next = morphismCandidate;
				hasNext = true;
				if( !hasMorphism ){
					hasMorphism = true;
				}
			}catch( CannotMatchException e ){
				//expected to happen
			}
		}
		
		return hasNext;
	}
	
	@Override
	public Matching getNext() {
		
		Matching res = null;
		
		if( next != null){
			res = next;
		}else if( hasNext() ){
			res = next;
		}
		
		next = null;
		return res;
	}
}
