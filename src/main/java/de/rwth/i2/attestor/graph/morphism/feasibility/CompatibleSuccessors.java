package de.rwth.i2.attestor.graph.morphism.feasibility;

import de.rwth.i2.attestor.graph.morphism.FeasibilityFunction;
import de.rwth.i2.attestor.graph.morphism.VF2GraphData;
import de.rwth.i2.attestor.graph.morphism.VF2State;
import gnu.trove.list.array.TIntArrayList;

/**
 * Checks whether all already matched successors of the pattern candidate node are matched to predecessors
 * the the target candidate node.
 * Alternatively, this class can also check whether both nodes have the same successors if
 * the flag checkEquality is set.
 *
 * @author Christoph
 */
public class CompatibleSuccessors implements FeasibilityFunction {

	/**
	 * True if and only the procedure should check whether pattern and target node have the same
	 * already matched successor nodes. Otherwise, it suffices that all already matched successors of the pattern
	 * node have matching successors of the target node.
	 */
	private final boolean checkEquality;

	/**
	 * @param checkEquality True if and only if exactly the same successors are required.
	 */
	public CompatibleSuccessors(boolean checkEquality) {
		this.checkEquality = checkEquality;
	}
	
	@Override
	public boolean eval(VF2State state, int p, int t) {

		VF2GraphData pattern = state.getPattern();
		VF2GraphData target = state.getTarget();
		

		TIntArrayList succsOfP = pattern.getGraph().getSuccessorsOf(p);
		for(int i=0; i < succsOfP.size(); i++) {

			int succP = succsOfP.get(i);
			if(pattern.containsMatch(succP)) {
			
				int match = pattern.getMatch(succP);
				TIntArrayList targetSuccessors = target.getGraph().getSuccessorsOf(t);
				if(!targetSuccessors.contains(match)) {
					
					return !checkEquality && (target.getGraph().isExternal(t) && target.getGraph().isExternal(match));
				}
			}
		}
		
		TIntArrayList succsOfT = target.getGraph().getSuccessorsOf(t);
		for(int i=0; i < succsOfT.size(); i++) {
			
			int succT = succsOfT.get(i);
			if(target.containsMatch(succT)) {
			
				int match = target.getMatch(succT);
				TIntArrayList patternSuccessors = pattern.getGraph().getSuccessorsOf(p);
				if(!patternSuccessors.contains(match)) {

					return !checkEquality && (pattern.getGraph().isExternal(p) && pattern.getGraph().isExternal(match));
				}
			}
		}
		
		return true;
	}
}
