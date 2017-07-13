package de.rwth.i2.attestor.graph.morphism.feasibility;

import de.rwth.i2.attestor.graph.morphism.*;
import de.rwth.i2.attestor.tasks.GeneralType;

/**
 * Checks whether the labels of the nodes in the candidate pair coincide.
 *
 * @author Christoph
 */
public class IdenticalNodeTypes implements FeasibilityFunction {

	@Override
	public boolean eval(VF2State state, CandidatePair candidate) {

		Graph patternGraph = state.getPattern().getGraph();
		Graph targetGraph = state.getTarget().getGraph();

		if( patternGraph.getNodeLabel(candidate.p) instanceof GeneralType){
			GeneralType nodeType = (GeneralType) patternGraph.getNodeLabel(candidate.p);
			return nodeType.typeEquals( targetGraph.getNodeLabel(candidate.t) );
		}else{
			return patternGraph.getNodeLabel(candidate.p)
					.equals(
							targetGraph.getNodeLabel(candidate.t)
							);
		}
	}

}