package de.rwth.i2.attestor.graph.morphism.feasibility;

import de.rwth.i2.attestor.graph.morphism.CandidatePair;
import de.rwth.i2.attestor.graph.morphism.FeasibilityFunction;
import de.rwth.i2.attestor.graph.morphism.Graph;
import de.rwth.i2.attestor.graph.morphism.VF2State;
import de.rwth.i2.attestor.types.GeneralType;

/**
 * Checks whether the labels of the nodes in the candidate pair coincide.
 *
 * @author Christoph
 */
public class CompatibleNodeTypes implements FeasibilityFunction {

	@Override
	public boolean eval(VF2State state, CandidatePair candidate) {

		Graph patternGraph = state.getPattern().getGraph();
		Graph targetGraph = state.getTarget().getGraph();

		if( patternGraph.getNodeLabel(candidate.p) instanceof GeneralType){
			GeneralType nodeType = (GeneralType) patternGraph.getNodeLabel(candidate.p);
			return nodeType.typeEquals( targetGraph.getNodeLabel(candidate.t) );
		}else{
			return patternGraph.getNodeLabel(candidate.p)
					.matches(
							targetGraph.getNodeLabel(candidate.t)
							);
		}
	}

}
