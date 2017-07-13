package de.rwth.i2.attestor.graph.morphism.feasibility;

import de.rwth.i2.attestor.graph.morphism.CandidatePair;
import de.rwth.i2.attestor.graph.morphism.FeasibilityFunction;
import de.rwth.i2.attestor.graph.morphism.VF2State;

/**
 * Applies different FeasibilityFunctions depending on whether the provided pattern candidate node is external or not.
 *
 * @author Christoph
 */
public class EmbeddingBranchOnPatternExternal implements FeasibilityFunction {

    /**
     * The FeasibilityFunction to execute if the pattern candidate node is external.
     */
	private final FeasibilityFunction onExternal;

	/**
     * The FeasibilityFunction to execute if the pattern candidate node is not external.
     */
	private final FeasibilityFunction onInternal;

    /**
     * Initializes this FeasibilityFunction.
     * @param onExternal The FeasibilityFunction to execute if the pattern candidate node is external.
     * @param onInternal The FeasibilityFunction to execute if the pattern candidate node is not external.
     */
	public EmbeddingBranchOnPatternExternal(FeasibilityFunction onExternal, FeasibilityFunction onInternal) {
		this.onExternal = onExternal;
		this.onInternal = onInternal;
	}
	
	@Override
	public boolean eval(VF2State state, CandidatePair candidate) {
		
		if( state.getPattern().getGraph().isExternal(candidate.p) ) {
	
			return onExternal.eval(state, candidate);
		} else {
			return onInternal.eval(state, candidate);
		}
	}
}
