package de.rwth.i2.attestor.graph.morphism.feasibility;

import de.rwth.i2.attestor.graph.morphism.*;
import gnu.trove.list.array.TIntArrayList;

/**
 * Checks whether all already matched predecessors of the pattern candidate node are matched to predecessors
 * the the target candidate node.
 * Alternatively, this class can also check whether both nodes have the same predecessors if
 * the flag checkEqualityOnExternal is set.
 *
 * @author Christoph
 */
public class CompatiblePredecessors implements FeasibilityFunction {

	/**
	 * True if and only the procedure should check whether pattern and target node have the same
	 * already matched predecessor nodes. Otherwise, it suffices that all already matched predecessors of the pattern
	 * node have matching predecessors of the target node.
	 */
	private final boolean checkEqualityOnExternal;

    /**
     * @param checkEqualityOnExternal True if and only if exactly the same predecessors are required.
     */
	public CompatiblePredecessors(boolean checkEqualityOnExternal) {
		this.checkEqualityOnExternal = checkEqualityOnExternal;
	}
	
	@Override
	public boolean eval(VF2State state, int p, int t) {
		
		VF2GraphData pattern = state.getPattern();
		VF2GraphData target = state.getTarget();
		Graph patternGraph = pattern.getGraph();
		Graph targetGraph = target.getGraph();

		boolean checkEquality = checkEqualityOnExternal || !patternGraph.isExternal(p);

		TIntArrayList predsOfP = patternGraph.getPredecessorsOf(p);
		for(int i=0; i < predsOfP.size(); i++) {
		
			int predP = predsOfP.get(i);
			if(pattern.containsMatch(predP)) {
			
				int match = pattern.getMatch(predP);
				TIntArrayList targetPredecessors = targetGraph.getPredecessorsOf(t);
				if(!targetPredecessors.contains(match)) {
					
					return !checkEquality && (targetGraph.isExternal(t) && targetGraph.isExternal(match));
				}
			}
		}
		
		TIntArrayList predsOfT = targetGraph.getPredecessorsOf(t);
		for(int i=0; i < predsOfT.size(); i++) {
			
			int predT = predsOfT.get(i);
			if(target.containsMatch(predT)) {
			
				int match = target.getMatch(predT);
				TIntArrayList patternPredecessors = patternGraph.getPredecessorsOf(p);
				if(!patternPredecessors.contains(match)) {
					
					return !checkEquality && (patternGraph.isExternal(p) && patternGraph.isExternal(match));

				}
			}
		}
		
		return true;
	}
}
