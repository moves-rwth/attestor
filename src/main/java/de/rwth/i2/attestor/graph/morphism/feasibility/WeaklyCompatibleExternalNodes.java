package de.rwth.i2.attestor.graph.morphism.feasibility;

import de.rwth.i2.attestor.graph.morphism.*;

/**
 * Checks that pattern external iff target external
 * @author Hannah
 *
 */
public class WeaklyCompatibleExternalNodes implements FeasibilityFunction  {
	
	@Override
	public boolean eval(VF2State state, int p, int t) {
		
		Graph patternGraph = state.getPattern().getGraph();
		Graph targetGraph = state.getTarget().getGraph();

		return patternGraph.isExternal(p) == targetGraph.isExternal(t);
	}
}
