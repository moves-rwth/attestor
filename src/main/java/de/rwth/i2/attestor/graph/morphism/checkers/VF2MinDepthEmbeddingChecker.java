package de.rwth.i2.attestor.graph.morphism.checkers;


import de.rwth.i2.attestor.graph.morphism.FeasibilityFunction;
import de.rwth.i2.attestor.graph.morphism.MorphismChecker;
import de.rwth.i2.attestor.graph.morphism.TerminationFunction;
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
import de.rwth.i2.attestor.graph.morphism.feasibility.VariableDereferenceDepth;
import de.rwth.i2.attestor.graph.morphism.terminationFunctions.MorphismFound;
import de.rwth.i2.attestor.graph.morphism.terminationFunctions.NoMorphismPossible;
import de.rwth.i2.attestor.main.settings.Settings;

/**
 * A specialized {@link MorphismChecker} to determine embeddings between a pattern graph
 * into a target graph in which each variable in the target graph has at least a given distance to the embedding.
 * 
 * @author Christoph
 *
 */
public class VF2MinDepthEmbeddingChecker extends AbstractVF2MorphismChecker {

	/**
	 * Specification of the condition that an embedding has been found.
	 */
	private static final TerminationFunction matchingCondition = new MorphismFound();
	
	/**
	 * Specification of the condition that no embedding can be found anymore.
	 */
	private static final TerminationFunction matchingImpossibleCondition = new NoMorphismPossible();
	
	/**
	 * Specification of all compatible predecessor nodes.
	 */
	private static final FeasibilityFunction compatiblePredecessors = new EmbeddingBranchOnPatternExternal(
			new CompatiblePredecessors(false), new CompatiblePredecessors(true)
			);
	
	/**
	 * Specification of all compatible successor nodes.
	 */
	private static final FeasibilityFunction compatibleSuccessors = new EmbeddingBranchOnPatternExternal(
			new CompatibleSuccessors(false), new CompatibleSuccessors(true)
			);
	
	/**
	 * Specification of a feasibility function with a one-edge lookahead for incoming edges.
	 */
	private static final FeasibilityFunction oneStepLookaheadIn = new EmbeddingBranchOnPatternExternal(
			new OneStepLookaheadIn(false), new OneStepLookaheadIn(true)
			);
	
	/**
	 * Specification of a feasibility function with a one-edge lookahead for outgoing edges.
	 */
	private static final FeasibilityFunction oneStepLookaheadOut = new EmbeddingBranchOnPatternExternal(
			new OneStepLookaheadOut(false), new OneStepLookaheadOut(true)
			);
	
	/**
	 * Specification of a feasibility function with a two-edge lookahead.
	 */
	private static final FeasibilityFunction twoStepLookahead = new EmbeddingBranchOnPatternExternal(
			new TwoStepLookahead(false), new TwoStepLookahead(true)
			);
	
	/**
	 * Specification of a feasibility function to check external nodes.
	 */
	private static final FeasibilityFunction embeddingExternalNodes = new EmbeddingExternalNodes();
	
	/**
	 * Specification of a feasibility function to check node types.
	 */
	private static final FeasibilityFunction compatibleNodeTypes = new CompatibleNodeTypes();
	
	/**
	 * Specification of a feasibility function to check edge labels for embeddings.
	 */
	private static final FeasibilityFunction embeddingEdgeLabels = new EmbeddingEdgeLabels();
	

	/**
	 * Initializes this checker for a given minimal distance.
	 * @param depth The minimal distance of all variables to a found embedding.
	 */
	public VF2MinDepthEmbeddingChecker(int depth) {

		super(
				VF2Algorithm.builder()
				.setCheckExistence(true)
				.setMatchingCondition( matchingCondition )
				.setMatchingImpossibleCondition( matchingImpossibleCondition )
				.addFeasibilityCondition( compatiblePredecessors )
				.addFeasibilityCondition( compatibleSuccessors )
				.addFeasibilityCondition( oneStepLookaheadIn )
				.addFeasibilityCondition( oneStepLookaheadOut )
				.addFeasibilityCondition( twoStepLookahead )
				.addFeasibilityCondition( embeddingExternalNodes )
				.addFeasibilityCondition( compatibleNodeTypes )
				.addFeasibilityCondition( embeddingEdgeLabels )
				.addFeasibilityCondition( new VariableDereferenceDepth(depth,
						Settings.getInstance().options().getAggressiveNullAbstraction()) )
				.build()
			);
	}
	
	
	
}
