package de.rwth.i2.attestor.graph.morphism.checkers;

import de.rwth.i2.attestor.graph.morphism.MorphismChecker;
import de.rwth.i2.attestor.graph.morphism.VF2Algorithm;
import de.rwth.i2.attestor.graph.morphism.feasibility.*;
import de.rwth.i2.attestor.graph.morphism.terminationFunctions.IsomorphismFound;

/**
 * A specialized {@link MorphismChecker} to find isomorphisms between two graphs.
 *
 * @author Christoph
 */
public class VF2IsomorphismChecker extends AbstractVF2MorphismChecker {

    /**
     * Specification of the algorithm used to determine isomorphisms.
     */
    private static final VF2Algorithm matchingAlgorithm = VF2Algorithm.builder()
            .setMatchingCondition(new IsomorphismFound())
            .addFeasibilityCondition(new CompatiblePredecessors(true))
            .addFeasibilityCondition(new CompatibleSuccessors(true))
            .addFeasibilityCondition(new OneStepLookaheadIn(true)) // lookahead sets are compared for equality
            .addFeasibilityCondition(new OneStepLookaheadOut(true))
            .addFeasibilityCondition(new TwoStepLookahead(true))
            .addFeasibilityCondition(new CompatibleExternalNodes())
            .addFeasibilityCondition(new IdenticalNodeTypes())
            .addFeasibilityCondition(new CompatibleEdgeLabels())
            .build();


    public VF2IsomorphismChecker() {

        super(matchingAlgorithm);
    }
}
