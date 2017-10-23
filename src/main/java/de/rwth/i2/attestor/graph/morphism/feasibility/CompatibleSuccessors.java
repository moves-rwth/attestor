package de.rwth.i2.attestor.graph.morphism.feasibility;

import de.rwth.i2.attestor.graph.morphism.*;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

/**
 * Checks whether all already matched successors of the pattern candidate node are matched to predecessors
 * the the target candidate node.
 * Alternatively, this class can also check whether both nodes have the same successors if
 * the flag checkEqualityOnExternal is set.
 *
 * @author Christoph
 */
public class CompatibleSuccessors implements FeasibilityFunction {

	/**
	 * True if and only the procedure should check whether pattern and target node have the same
	 * already matched successor nodes. Otherwise, it suffices that all already matched successors of the pattern
	 * node have matching successors of the target node.
	 */
	private final boolean checkEqualityOnExternal;

	/**
	 * @param checkEqualityOnExternal True if and only if exactly the same successors are required.
	 */
	public CompatibleSuccessors(boolean checkEqualityOnExternal) {
		this.checkEqualityOnExternal = checkEqualityOnExternal;
	}
	
	@Override
	public boolean eval(VF2State state, int p, int t) {

		VF2PatternGraphData pattern = state.getPattern();
		Graph patternGraph = pattern.getGraph();
		VF2TargetGraphData target = state.getTarget();
		Graph targetGraph = target.getGraph();

		boolean checkEquality = checkEqualityOnExternal || !patternGraph.isExternal(p);

		TIntArrayList succsOfP = patternGraph.getSuccessorsOf(p);
		TIntArrayList succsOfT = targetGraph.getSuccessorsOf(t);

		TIntSet targetMatches = new TIntHashSet(succsOfP.size());

		for(int i=0; i < succsOfP.size(); i++) {

			int succP = succsOfP.get(i);
			if(pattern.containsMatch(succP)) {

				int match = pattern.getMatch(succP);
				if(checkEquality && !succsOfT.contains(match)) {
					return false;
				}
				targetMatches.add(match);
			}
		}

		for(int i=0; i < succsOfT.size(); i++) {
			int succT = succsOfT.get(i);
			if(checkEquality && target.containsMatch(succT) && !targetMatches.contains(succT)) {
				return false;
			}
		}

		return true;
	}
}
