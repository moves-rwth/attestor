package de.rwth.i2.attestor.graph.morphism.feasibility;

import de.rwth.i2.attestor.graph.morphism.FeasibilityFunction;
import de.rwth.i2.attestor.graph.morphism.Graph;
import de.rwth.i2.attestor.graph.morphism.VF2GraphData;
import de.rwth.i2.attestor.graph.morphism.VF2State;
import gnu.trove.list.array.TIntArrayList;

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

		VF2GraphData pattern = state.getPattern();
		Graph patternGraph = pattern.getGraph();
		VF2GraphData target = state.getTarget();
		Graph targetGraph = target.getGraph();

		boolean checkEquality = checkEqualityOnExternal || !patternGraph.isExternal(p);


		TIntArrayList succsOfP = patternGraph.getSuccessorsOf(p);
		for(int i=0; i < succsOfP.size(); i++) {

			int succP = succsOfP.get(i);
			if(pattern.containsMatch(succP)) {
			
				int match = pattern.getMatch(succP);
				TIntArrayList targetSuccessors = targetGraph.getSuccessorsOf(t);
				if(!targetSuccessors.contains(match)) {
					
					return !checkEquality && (targetGraph.isExternal(t) && targetGraph.isExternal(match));
				}
			}
		}
		
		TIntArrayList succsOfT = targetGraph.getSuccessorsOf(t);
		for(int i=0; i < succsOfT.size(); i++) {
			
			int succT = succsOfT.get(i);
			if(target.containsMatch(succT)) {
			
				int match = target.getMatch(succT);
				TIntArrayList patternSuccessors = patternGraph.getSuccessorsOf(p);
				if(!patternSuccessors.contains(match)) {

					return !checkEquality && (patternGraph.isExternal(p) && patternGraph.isExternal(match));
				}
			}
		}
		
		return true;
	}
}
