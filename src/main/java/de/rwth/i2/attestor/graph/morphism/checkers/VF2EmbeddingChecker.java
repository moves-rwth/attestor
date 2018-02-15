package de.rwth.i2.attestor.graph.morphism.checkers;


import de.rwth.i2.attestor.graph.morphism.MorphismChecker;
import de.rwth.i2.attestor.graph.morphism.VF2Algorithm;
import de.rwth.i2.attestor.graph.morphism.feasibility.*;
import de.rwth.i2.attestor.graph.morphism.terminationFunctions.MorphismFound;

/**
 * A specialized {@link MorphismChecker} to find all embeddings of a pattern graph in a target graph.
 *
 * @author Christoph
 */
public class VF2EmbeddingChecker extends AbstractVF2MorphismChecker {

    /**
     * Specification of the algorithm used to determine embeddings.
     */
    private static final VF2Algorithm matchingAlgorithm = VF2Algorithm.builder()
            .setMatchingCondition(new MorphismFound())
            .addFeasibilityCondition(new CompatibleNodeTypes())
            .addFeasibilityCondition(new CompatiblePredecessors(false))
            .addFeasibilityCondition(new CompatibleSuccessors(false))
            .addFeasibilityCondition(new OneStepLookaheadIn(false))
            .addFeasibilityCondition(new OneStepLookaheadOut(false))
            .addFeasibilityCondition(new TwoStepLookahead(false))
            .addFeasibilityCondition(new EmbeddingExternalNodes())
            .addFeasibilityCondition(new EmbeddingEdgeLabels())
            .build();


    public VF2EmbeddingChecker() {

        super(matchingAlgorithm);
    }


}
