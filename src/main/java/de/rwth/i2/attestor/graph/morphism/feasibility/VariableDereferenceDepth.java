package de.rwth.i2.attestor.graph.morphism.feasibility;

import de.rwth.i2.attestor.graph.heap.Variable;
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

	private final boolean aggressiveNullAbstraction;

	/**
	 * @param minAbstractionDistance The minimal distance of variables to nodes in the morphism.
	 * @param aggressiveNullAbstraction True if and only if the minimal distance should be ignored
	 *                                         for the null node.
	 */
	public VariableDereferenceDepth(int minAbstractionDistance, boolean aggressiveNullAbstraction
	) {
		
		this.minAbstractionDistance = minAbstractionDistance;
		this.aggressiveNullAbstraction = aggressiveNullAbstraction;
	}

	@Override
	public boolean eval(VF2State state, int p, int t) {
	
		Graph graph = state.getTarget().getGraph();

		TIntArrayList dist = SelectorDistanceHelper.getSelectorDistances(graph, t);

		for(int i=0; i < graph.size();i++) {
			Object nodeLabel = graph.getNodeLabel(i);
			if (nodeLabel.getClass() == Variable.class) {
				String label = ((Variable) nodeLabel).getName();
				if(!(aggressiveNullAbstraction && isConstant(label))) {
					int attachedNode = graph.getSuccessorsOf(i).get(0);
					if(dist.get(attachedNode) < minAbstractionDistance)	{
						return false;
					}
				}
			}
		}

		return true;
	}

	private boolean isConstant(String label) {
		return label.endsWith("null")
				|| label.endsWith("1")
				|| label.endsWith("0")
				|| label.endsWith("-1")
				|| label.endsWith("false")
				|| label.endsWith("true");
	}
}



 