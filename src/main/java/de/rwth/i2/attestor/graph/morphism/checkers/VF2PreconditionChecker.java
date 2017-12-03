package de.rwth.i2.attestor.graph.morphism.checkers;

import de.rwth.i2.attestor.graph.morphism.VF2Algorithm;
import de.rwth.i2.attestor.graph.morphism.feasibility.*;
import de.rwth.i2.attestor.graph.morphism.terminationFunctions.IsomorphismFound;

public class VF2PreconditionChecker extends AbstractVF2MorphismChecker {

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
            .addFeasibilityCondition(new WeaklyCompatibleExternalNodes())
            .addFeasibilityCondition(new IdenticalNodeTypes())
            .addFeasibilityCondition(new CompatibleEdgeLabels())
            .build();


    public VF2PreconditionChecker() {

        super(matchingAlgorithm);
    }
}
