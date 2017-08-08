package de.rwth.i2.attestor.grammar.canoncalization;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import de.rwth.i2.attestor.graph.heap.matching.EmbeddingChecker;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.ReturnValueStmt;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.ReturnVoidStmt;
import de.rwth.i2.attestor.stateSpaceGeneration.Semantics;

public class EmbeddingCheckerProvider {
	
	private int aggressiveAbstractionThreshold;
	private boolean aggressiveReturnAbstraction;

	public EmbeddingCheckerProvider(int aggressiveAbstractionThreshold, boolean aggressiveReturnAbstraction) {
		this.aggressiveAbstractionThreshold = aggressiveAbstractionThreshold;
		this.aggressiveReturnAbstraction = aggressiveReturnAbstraction;
	}

	public AbstractMatchingChecker getEmbeddingChecker(HeapConfiguration graph, HeapConfiguration pattern,
			Semantics semantics) {
		
		if( graph.countNodes() > aggressiveAbstractionThreshold ){
	
			return new EmbeddingChecker( pattern, graph );
		}else if( aggressiveReturnAbstraction
				&& 
				( semantics instanceof ReturnValueStmt || semantics instanceof ReturnVoidStmt ) ){
			return new EmbeddingChecker( pattern, graph );
		}

		return graph.getEmbeddingsOf(pattern);
	}

}
