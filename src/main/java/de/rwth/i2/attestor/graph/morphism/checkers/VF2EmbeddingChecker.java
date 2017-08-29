package de.rwth.i2.attestor.graph.morphism.checkers;


import de.rwth.i2.attestor.graph.morphism.MorphismChecker;
import de.rwth.i2.attestor.graph.morphism.VF2Algorithm;
import de.rwth.i2.attestor.graph.morphism.feasibility.CompatibleNodeTypes;
import de.rwth.i2.attestor.graph.morphism.feasibility.CompatiblePredecessors;
import de.rwth.i2.attestor.graph.morphism.feasibility.CompatibleSuccessors;
import de.rwth.i2.attestor.graph.morphism.feasibility.EmbeddingBranchOnPatternExternal;
import de.rwth.i2.attestor.graph.morphism.feasibility.EmbeddingEdgeLabels;
import de.rwth.i2.attestor.graph.morphism.feasibility.EmbeddingExternalNodes;
import de.rwth.i2.attestor.graph.morphism.feasibility.OneStepLookaheadIn;
import de.rwth.i2.attestor.graph.morphism.feasibility.OneStepLookaheadOut;
import de.rwth.i2.attestor.graph.morphism.feasibility.TwoStepLookahead;
import de.rwth.i2.attestor.graph.morphism.terminationFunctions.MorphismFound;
import de.rwth.i2.attestor.graph.morphism.terminationFunctions.NoMorphismPossible;

/**
 * 
 * A specialized {@link MorphismChecker} to find all embeddings of a pattern graph in a target graph.
 * 
 * @author Christoph
 *
 */
public class VF2EmbeddingChecker extends AbstractVF2MorphismChecker {

	/**
	 * Specification of the algorithm used to determine embeddings.
	 */
	private static final VF2Algorithm matchingAlgorithm= VF2Algorithm.builder()
					.setMatchingCondition( new MorphismFound() )
					.setMatchingImpossibleCondition( new NoMorphismPossible() )
					.addFeasibilityCondition( new EmbeddingBranchOnPatternExternal(
							new CompatiblePredecessors(false), new CompatiblePredecessors(true)
							))
					.addFeasibilityCondition( new EmbeddingBranchOnPatternExternal(
							new CompatibleSuccessors(false), new CompatibleSuccessors(true)
							))
					.addFeasibilityCondition( new EmbeddingBranchOnPatternExternal(
							new OneStepLookaheadIn(false), new OneStepLookaheadIn(true)
							))
					.addFeasibilityCondition( new EmbeddingBranchOnPatternExternal(
							new OneStepLookaheadOut(false), new OneStepLookaheadOut(true)
							))
					.addFeasibilityCondition( new EmbeddingBranchOnPatternExternal(
							new TwoStepLookahead(false), new TwoStepLookahead(true)
							))
					.addFeasibilityCondition( new EmbeddingExternalNodes() )
					.addFeasibilityCondition( new CompatibleNodeTypes() )
					.addFeasibilityCondition( new EmbeddingEdgeLabels() )
					.build();

	
	public VF2EmbeddingChecker() {
		
		super(matchingAlgorithm);
	}
	
	
	
}
