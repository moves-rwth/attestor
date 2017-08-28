package de.rwth.i2.attestor.grammar.canonicalization;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import de.rwth.i2.attestor.graph.heap.matching.EmbeddingChecker;
import de.rwth.i2.attestor.semantics.TerminalStatement;
import de.rwth.i2.attestor.stateSpaceGeneration.Semantics;

public class EmbeddingCheckerProvider {
	
	private int minDereferenceDepth;
	private int aggressiveAbstractionThreshold;
	private boolean aggressiveReturnAbstraction;

	public EmbeddingCheckerProvider( int minDereferenceDepth,
			int aggressiveAbstractionThreshold, boolean aggressiveReturnAbstraction) {
		this.minDereferenceDepth = minDereferenceDepth;
		this.aggressiveAbstractionThreshold = aggressiveAbstractionThreshold;
		this.aggressiveReturnAbstraction = aggressiveReturnAbstraction;
	}

	public AbstractMatchingChecker getEmbeddingChecker(HeapConfiguration graph, HeapConfiguration pattern,
			Semantics semantics) {
		
		if( graph.countNodes() > aggressiveAbstractionThreshold
                || (aggressiveReturnAbstraction && semantics instanceof TerminalStatement)) {
	
			return new EmbeddingChecker( pattern, graph );
		}

		return graph.getEmbeddingsOf(pattern, minDereferenceDepth);
	}

}
