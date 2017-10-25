package de.rwth.i2.attestor.graph.morphism.checkers;

import de.rwth.i2.attestor.graph.morphism.VF2Algorithm;
import de.rwth.i2.attestor.graph.morphism.feasibility.*;
import de.rwth.i2.attestor.graph.morphism.terminationFunctions.IsomorphismFound;
import de.rwth.i2.attestor.graph.morphism.terminationFunctions.NoMorphismPossible;

public class  VF2StrongerPreconditionChecker extends AbstractVF2MorphismChecker {
	/**
	 * Specification of the algorithm used to determine isomorphisms.
	 */
	private static final VF2Algorithm matchingAlgorithm = VF2Algorithm.builder()
					.setMatchingCondition( new IsomorphismFound() )
					.setMatchingImpossibleCondition( new NoMorphismPossible() )
					.addFeasibilityCondition( new CompatiblePredecessors(true) )
					.addFeasibilityCondition( new CompatibleSuccessors(true) )
					.addFeasibilityCondition( new OneStepLookaheadIn(true) ) // lookahead sets are compared for equality
					.addFeasibilityCondition( new OneStepLookaheadOut(true) )
					.addFeasibilityCondition( new TwoStepLookahead(true) )
					.addFeasibilityCondition( new WeaklyCompatibleExternalNodes() )
					.addFeasibilityCondition( new IdenticalNodeTypes() )
					.addFeasibilityCondition( new CompatibleEdgeLabels() )
					.build();
	
	
	public VF2StrongerPreconditionChecker() {
		super(matchingAlgorithm);
	}
}
