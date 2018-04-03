package de.rwth.i2.attestor.graph.morphism.checkers;


import de.rwth.i2.attestor.graph.morphism.*;
import de.rwth.i2.attestor.graph.morphism.feasibility.*;
import de.rwth.i2.attestor.graph.morphism.terminationFunctions.MorphismFound;

/**
 * A specialized {@link MorphismChecker} to determine embeddings between a pattern graph
 * into a target graph in which each variable in the target graph has at least a given distance to the embedding.
 *
 * @author Christoph
 */
public class VF2MinDistanceEmbeddingChecker extends AbstractVF2MorphismChecker {

    /**
     * Specification of the condition that an embedding has been found.
     */
    private static final TerminationFunction matchingCondition = new MorphismFound();

    /**
     * Specification of all compatible predecessor nodes.
     */
    private static final FeasibilityFunction
            compatiblePredecessors = new CompatiblePredecessors(false);

    /**
     * Specification of all compatible successor nodes.
     */
    private static final FeasibilityFunction
            compatibleSuccessors = new CompatibleSuccessors(false);

    /**
     * Specification of a feasibility function with a one-edge lookahead for incoming edges.
     */
    private static final FeasibilityFunction
            oneStepLookaheadIn = new OneStepLookaheadIn(false);

    /**
     * Specification of a feasibility function with a one-edge lookahead for outgoing edges.
     */
    private static final FeasibilityFunction
            oneStepLookaheadOut = new OneStepLookaheadOut(false);

    /**
     * Specification of a feasibility function with a two-edge lookahead.
     */
    private static final FeasibilityFunction
            twoStepLookahead = new TwoStepLookahead(false);

    /**
     * Specification of a feasibility function to check external nodes.
     */
    private static final FeasibilityFunction
            embeddingExternalNodes = new EmbeddingExternalNodes();

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
     *
     * @param options Options guiding how embeddings are computed
     */
    public VF2MinDistanceEmbeddingChecker(MorphismOptions options) {

        super(
                VF2Algorithm.builder()
                        .setMatchingCondition(matchingCondition)
                        .addFeasibilityCondition(compatibleNodeTypes)
                        .addFeasibilityCondition(compatiblePredecessors)
                        .addFeasibilityCondition(compatibleSuccessors)
                        .addFeasibilityCondition(oneStepLookaheadIn)
                        .addFeasibilityCondition(oneStepLookaheadOut)
                        .addFeasibilityCondition(twoStepLookahead)
                        .addFeasibilityCondition(embeddingExternalNodes)
                        .addFeasibilityCondition(embeddingEdgeLabels)
                        .addFeasibilityCondition(new AdmissibleAbstraction(options))
                        .build()
        );
    }


}
