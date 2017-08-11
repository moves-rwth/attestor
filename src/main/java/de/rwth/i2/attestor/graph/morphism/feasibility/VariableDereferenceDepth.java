package de.rwth.i2.attestor.graph.morphism.feasibility;

import de.rwth.i2.attestor.graph.heap.Variable;
import de.rwth.i2.attestor.graph.morphism.CandidatePair;
import de.rwth.i2.attestor.graph.morphism.FeasibilityFunction;
import de.rwth.i2.attestor.graph.morphism.Graph;
import de.rwth.i2.attestor.graph.morphism.VF2State;
import gnu.trove.list.array.TIntArrayList;

/**
 * Restricts the considered morphisms to ones in which the distance from variables to nodes belonging to a morphism
 * is at least the given depth. The only exception is the node representing null.
 *
 * @author Christoph
 */
public class VariableDereferenceDepth implements FeasibilityFunction {

	/**
	 * The minimal distance of variables to nodes belonging to the morphism we are searching for.
	 */
	private final int minAbstractionDistance;

	private final boolean isNullAbstractionDistanceEnabled;

	/**
	 * @param minAbstractionDistance The minimal distance of variables to nodes in the morphism.
	 * @param isNullAbstractionDistanceEnabled True if and only if the minimal distance should be ignored
	 *                                         for the null node.
	 */
	public VariableDereferenceDepth(int minAbstractionDistance, boolean isNullAbstractionDistanceEnabled) {
		
		this.minAbstractionDistance = minAbstractionDistance;
		this.isNullAbstractionDistanceEnabled = isNullAbstractionDistanceEnabled;
	}

	@Override
	public boolean eval(VF2State state, CandidatePair candidate) {
	
		Graph graph = state.getTarget().getGraph();
		Graph pattern = state.getPattern().getGraph();

		for(int var=0; var < graph.size(); var++) {
			
			if(graph.getNodeLabel(var) instanceof Variable) {
				
				String label = ((Variable) graph.getNodeLabel(var)).getName();
				boolean isNull = (!isNullAbstractionDistanceEnabled) || label.contains("null");
				
				int attachedNode = graph.getSuccessorsOf(var).get(0);
				
				TIntArrayList dist = SelectorDistanceHelper.getSelectorDistances(graph, attachedNode);
				
				for(int i=0; i < pattern.size(); i++) {
					
					if(state.getPattern().containsMatch(i) 
							&& pattern.isExternal(i) 
							&& dist.get(state.getPattern().getMatch(i)) < minAbstractionDistance
							) {
						
						if (isNull || pattern.getSuccessorsOf(i).size() > 0) {							
							
							return false;
						}
					}
				}
			}
		}
		
		return true;
	}
}



 