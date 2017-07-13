package de.rwth.i2.attestor.graph.morphism;

/**
 * A builder class to conveniently construct {@link VF2Algorithm}s step by step.
 * 
 * @author Christoph
 *
 */
public final class VF2AlgorithmBuilder {
	
	/**
	 * The VF2Algorithm under construction.
	 */
	private final VF2Algorithm algorithm;
	
	VF2AlgorithmBuilder() {
		algorithm = new VF2Algorithm();
	}
	
	/**
	 * Checks and returns the specified VF2Algorithm.
	 * @return The VF2Algorithm specified by this builder.
	 */
	public VF2Algorithm build() {
		assert(algorithm.morphismFoundCheck != null);
		assert(algorithm.morphismImpossibleCheck != null);
		
		return algorithm;
	}
	
	/**
	 * Specifies the condition that determines that a suitable matching has been found.
	 * @param condition The successful termination condition to be used by the algorithm.
	 * @return The builder.
	 */
	public VF2AlgorithmBuilder setMatchingCondition(TerminationFunction condition) {
		algorithm.morphismFoundCheck = condition;
		return this;
	}
	
	/**
	 * Specifies the condition that determines that no suitable matching can be found.
	 * @param condition The negative termination condition to be used by the algorithm.
	 * @return The builder.
	 */
	public VF2AlgorithmBuilder setMatchingImpossibleCondition(TerminationFunction condition) {
		algorithm.morphismImpossibleCheck = condition;
		return this;
	}
	
	/**
	 * Adds a FeasibilityFunction used to prune the search space when searching for potential graph morphisms.
	 * @param condition The condition to be used by the algorithm.
	 * @return The builder.
	 */
	public VF2AlgorithmBuilder addFeasibilityCondition(FeasibilityFunction condition) {
		algorithm.feasibilityChecks.add(condition);
		return this;
	}
	
	/**
	 * Determines whether the constructed algorithm should determine whether a morphism exists or should determine
	 * all existing morphisms.
	 * @param checkExistence true if and only if it suffices to check whether a morphism exists.
	 * @return The builder.
	 */
	public VF2AlgorithmBuilder setCheckExistence(boolean checkExistence) {
		algorithm.checkExistence = checkExistence;
		return this;
	}
	
}