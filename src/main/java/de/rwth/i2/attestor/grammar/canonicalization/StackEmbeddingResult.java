package de.rwth.i2.attestor.grammar.canonicalization;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

/**
 * Data class holding the result of a stack matching between two graphs
 * (embedding of one graph in another).
 * @author Hannah
 *
 */
public class StackEmbeddingResult {

	private final boolean canMatch;
	private final HeapConfiguration materializedToAbstract;
	private final Nonterminal instantiatedLhs;
	
	public StackEmbeddingResult( boolean canMatch, 
							     HeapConfiguration materializedToAbstract,
							     Nonterminal instantiatedLhs ){
		
		this.canMatch = canMatch;
		this.materializedToAbstract = materializedToAbstract;
		this.instantiatedLhs = instantiatedLhs;
	}
	
	public boolean canMatch() {
		return this.canMatch;
	}

	public HeapConfiguration getMaterializedToAbstract() {
		return this.materializedToAbstract;
	}

	public Nonterminal getInstantiatedLhs() {
		return this.instantiatedLhs;
	}

}
