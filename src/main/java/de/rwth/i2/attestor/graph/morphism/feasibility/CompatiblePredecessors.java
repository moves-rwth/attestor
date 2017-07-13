package de.rwth.i2.attestor.graph.morphism.feasibility;

import de.rwth.i2.attestor.graph.morphism.CandidatePair;
import de.rwth.i2.attestor.graph.morphism.FeasibilityFunction;
import de.rwth.i2.attestor.graph.morphism.VF2GraphData;
import de.rwth.i2.attestor.graph.morphism.VF2State;
import gnu.trove.list.array.TIntArrayList;

/**
 * Checks whether all already matched predecessors of the pattern candidate node are matched to predecessors
 * the the target candidate node.
 * Alternatively, this class can also check whether both nodes have the same predecessors if
 * the flag checkEquality is set.
 *
 * @author Christoph
 */
public class CompatiblePredecessors implements FeasibilityFunction {

	/**
	 * True if and only the procedure should check whether pattern and target node have the same
	 * already matched predecessor nodes. Otherwise, it suffices that all already matched predecessors of the pattern
	 * node have matching predecessors of the target node.
	 */
	private final boolean checkEquality;

    /**
     * @param checkEquality True if and only if exactly the same predecessors are required.
     */
	public CompatiblePredecessors(boolean checkEquality) {
		this.checkEquality = checkEquality;
	}
	
	@Override
	public boolean eval(VF2State state, CandidatePair candidate) {
		
		VF2GraphData pattern = state.getPattern();
		VF2GraphData target = state.getTarget();
		

		TIntArrayList predsOfP = pattern.getGraph().getPredecessorsOf(candidate.p);
		for(int i=0; i < predsOfP.size(); i++) {
		
			int p = predsOfP.get(i);
			if(pattern.containsMatch(p)) {
			
				int match = pattern.getMatch(p);
				TIntArrayList targetPredecessors = target.getGraph().getPredecessorsOf(candidate.t);
				if(!targetPredecessors.contains(match)) {
					
					return !checkEquality && (target.getGraph().isExternal(candidate.t) && target.getGraph().isExternal(match));
				}
			}
		}
		
		TIntArrayList predsOfT = target.getGraph().getPredecessorsOf(candidate.t);
		for(int i=0; i < predsOfT.size(); i++) {
			
			int t = predsOfT.get(i);
			if(target.containsMatch(t)) {
			
				int match = target.getMatch(t);
				TIntArrayList patternPredecessors = pattern.getGraph().getPredecessorsOf(candidate.p);
				if(!patternPredecessors.contains(match)) {
					
					return !checkEquality && (pattern.getGraph().isExternal(candidate.p) && pattern.getGraph().isExternal(match));

				}
			}
		}
		
		return true;
	}
}
