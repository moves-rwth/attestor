package de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar;

import de.rwth.i2.attestor.grammar.StackMatcher;
import de.rwth.i2.attestor.grammar.canonicalization.EmbeddingCheckerProvider;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.StackMaterializer;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import de.rwth.i2.attestor.stateSpaceGeneration.Semantics;

public class IndexedEmbeddingCheckerProvider extends EmbeddingCheckerProvider {

	
	private StackMatcher matcher;
	private StackMaterializer materializer;

	public IndexedEmbeddingCheckerProvider( StackMatcher matcher,
											StackMaterializer materializer,
											int aggressiveAbstractionThreshold, 
											boolean aggressiveReturnAbstraction) {
		
		super(aggressiveAbstractionThreshold, aggressiveReturnAbstraction);
		this.matcher = matcher;
		this.materializer = materializer;
	}

	public AbstractMatchingChecker getEmbeddingChecker(HeapConfiguration graph, HeapConfiguration pattern,
			Semantics semantics) {
		
		return new IndexedMatchingChecker( graph, pattern,
											super.getCorrectMorphismChecker(graph, semantics),
											new EmbeddingStackChecker(matcher, materializer) );
	}
	
}
