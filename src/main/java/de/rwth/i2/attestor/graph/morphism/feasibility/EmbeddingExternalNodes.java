package de.rwth.i2.attestor.graph.morphism.feasibility;

import de.rwth.i2.attestor.graph.morphism.FeasibilityFunction;
import de.rwth.i2.attestor.graph.morphism.VF2State;

/**
 * Checks whether the given candidate pair of nodes may correspond to an embedding from the pattern
 * graph into the target graph if one of them is external.
 *
 * @author Christoph
 */
public class EmbeddingExternalNodes implements FeasibilityFunction {

    @Override
    public boolean eval(VF2State state, int p, int t) {

        return state.getPattern().getGraph().isExternal(p)
                || !state.getTarget().getGraph().isExternal(t);

    }


}
