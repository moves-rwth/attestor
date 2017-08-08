package de.rwth.i2.attestor.grammar.canonicalization;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import de.rwth.i2.attestor.graph.morphism.MorphismChecker;
import de.rwth.i2.attestor.graph.morphism.checkers.VF2EmbeddingChecker;
import de.rwth.i2.attestor.graph.morphism.checkers.VF2MinDepthEmbeddingChecker;
import de.rwth.i2.attestor.main.settings.Settings;
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
		
		MorphismChecker morphismChecker = getCorrectMorphismChecker(graph, semantics);
		return new AbstractMatchingChecker( pattern, graph, morphismChecker );
	}
	
	protected MorphismChecker getCorrectMorphismChecker( HeapConfiguration graph, 
														 Semantics semantics){
		
		int depth = Settings.getInstance().options().getMinDereferenceDepth();
		if( graph.countNodes() > aggressiveAbstractionThreshold ){
			
			return new VF2MinDepthEmbeddingChecker(depth);
		}else if( aggressiveReturnAbstraction
				&& 
				( semantics instanceof ReturnValueStmt || semantics instanceof ReturnVoidStmt ) ){
			return new VF2EmbeddingChecker();
		}

		return new VF2MinDepthEmbeddingChecker(depth);
	}

}
