package de.rwth.i2.attestor.graph.morphism.feasibility;

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
	public boolean eval(VF2State state, int p, int t) {
		
		if( state.getPattern().getGraph().isExternal(p) ) {
	
			return onExternal.eval(state, p, t);
		} else {
			return onInternal.eval(state, p, t);
		}
	}
}
