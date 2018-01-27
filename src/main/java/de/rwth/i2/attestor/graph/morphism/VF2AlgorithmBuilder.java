package de.rwth.i2.attestor.graph.morphism;

import java.util.ArrayList;
import java.util.List;

/**
 * A builder class to conveniently construct {@link VF2Algorithm}s step by step.
 *
 * @author Christoph
 */
public final class VF2AlgorithmBuilder {

    /**
     * The VF2Algorithm under construction.
     */
    private final VF2Algorithm algorithm;

    private final List<FeasibilityFunction> feasibilityFunctions = new ArrayList<>();

    VF2AlgorithmBuilder() {

        algorithm = new VF2Algorithm();
    }

    /**
     * Checks and returns the specified VF2Algorithm.
     *
     * @return The VF2Algorithm specified by this builder.
     */
    public VF2Algorithm build() {

        assert (algorithm.morphismFoundCheck != null);

        algorithm.feasibilityChecks = feasibilityFunctions.toArray(new FeasibilityFunction[feasibilityFunctions.size()]);

        return algorithm;
    }

    /**
     * Specifies the condition that determines that a suitable matching has been found.
     *
     * @param condition The successful termination condition to be used by the algorithm.
     * @return The builder.
     */
    public VF2AlgorithmBuilder setMatchingCondition(TerminationFunction condition) {

        algorithm.morphismFoundCheck = condition;
        return this;
    }

    /**
     * Adds a FeasibilityFunction used to prune the search space when searching for potential graph morphisms.
     *
     * @param condition The condition to be used by the algorithm.
     * @return The builder.
     */
    public VF2AlgorithmBuilder addFeasibilityCondition(FeasibilityFunction condition) {

        feasibilityFunctions.add(condition);
        return this;
    }

}