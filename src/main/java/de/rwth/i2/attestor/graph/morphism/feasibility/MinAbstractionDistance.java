package de.rwth.i2.attestor.graph.morphism.feasibility;

import de.rwth.i2.attestor.graph.heap.Variable;
import de.rwth.i2.attestor.graph.morphism.*;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.types.GeneralType;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;

/**
 * Restricts the considered morphisms to ones in which the distance from variables to nodes with outgoing selector
 * edges belonging to a morphism is at least the given minAbstractionDistance.
 * If aggressiveConstantAbstraction is set to true, variables that model constants, such as null, are ignored.
 *
 * @author Christoph
 */
public class MinAbstractionDistance implements FeasibilityFunction {

	/**
	 * The minimal distance of variables to nodes belonging to the morphism we are searching for.
	 */
	private final int minAbstractionDistance;

	private final boolean aggressiveConstantAbstraction;

	/**
	 * @param minAbstractionDistance The minimal distance of variables to nodes in the morphism.
	 * @param aggressiveConstantAbstraction True if and only if the minimal distance should be ignored
	 *                                         for variable edges representing constants.
	 */
	public MinAbstractionDistance(int minAbstractionDistance, boolean aggressiveConstantAbstraction
	) {
		
		this.minAbstractionDistance = minAbstractionDistance;
		this.aggressiveConstantAbstraction = aggressiveConstantAbstraction;
	}

	@Override
	public boolean eval(VF2State state, int p, int t) {

		if(!hasOutgoingSelectorEdges(state, p))	{
			return true;
		}

		Graph graph = state.getTarget().getGraph();
		TIntArrayList dist = SelectorDistanceHelper.getSelectorDistances(graph, t);

		for(int i=0; i < graph.size();i++) {
			Object nodeLabel = graph.getNodeLabel(i);
			if (nodeLabel.getClass() == Variable.class) {
				String label = ((Variable) nodeLabel).getName();
				if(!(aggressiveConstantAbstraction && Constants.isConstant(label))) {
					int attachedNode = graph.getSuccessorsOf(i).get(0);
					if(dist.get(attachedNode) < minAbstractionDistance)	{
						return false;

					}
				}
			}
		}

		return true;
	}

	private boolean hasOutgoingSelectorEdges(VF2State state, int p) {

		Graph graph = state.getPattern().getGraph();
		TIntIterator iter = graph.getSuccessorsOf(p).iterator();
		while(iter.hasNext()) {
			int succ = iter.next();
			if(graph.getNodeLabel(succ).getClass() == GeneralType.class) {
				return true;
			}
		}
		return false;
	}
}



 