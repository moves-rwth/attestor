package de.rwth.i2.attestor.graph.morphism.feasibility;

import de.rwth.i2.attestor.graph.morphism.CandidatePair;
import de.rwth.i2.attestor.graph.morphism.FeasibilityFunction;
import de.rwth.i2.attestor.graph.morphism.VF2GraphData;
import de.rwth.i2.attestor.graph.morphism.VF2State;
import gnu.trove.list.array.TIntArrayList;

/**
 * Determines the whether the current candidate pair cannot belong to a graph morphism due to a mismatch
 * in the outgoing lookahead sets in the successors or predecessors of one of the candidate nodes.
 * This set corresponds to the number of nodes in the predecessors or successors of a candidate node
 * that have not been matched yet, but that are reachable from the candidate node via a single outgoing edge.
 * A mismatch may either consist of less ingoing edges in the target than in the pattern (checkEquality=false)
 * or an unequal number (checkEquality=true).
 *
 * @author Christoph
 */
public class OneStepLookaheadOut implements FeasibilityFunction {

	/**
	 * Determines whether the outgoing lookahead sets of pattern and target
	 * have to be equal or target sets are allowed to be larger.
	 */
	private final boolean checkEquality;

	/**
	 * @param checkEquality Determines whether equal lookahead sets are required.
	 */
	public OneStepLookaheadOut(boolean checkEquality) {
		
		this.checkEquality = checkEquality;
	}
	
	@Override
	public boolean eval(VF2State state, CandidatePair candidate) {
		
		VF2GraphData pattern = state.getPattern();
		VF2GraphData target = state.getTarget();
		
		int patternSucc = computeLookahead(
				pattern.getGraph().getSuccessorsOf(candidate.p),
				pattern
				);
		
		int targetSucc = computeLookahead(
				target.getGraph().getSuccessorsOf(candidate.t),
				target
				);
		
		if(checkEquality) {
			if(targetSucc != patternSucc) {
				return false;
			}	
		} else {
			if(!(targetSucc <= patternSucc)) {
				return false;
			}
		}
		
		int patternPred = computeLookahead(
				pattern.getGraph().getPredecessorsOf(candidate.p),
				pattern
				);
		
		int targetPred = computeLookahead(
				target.getGraph().getPredecessorsOf(candidate.t),
				target
				);
		
		if(checkEquality) {
			return (targetPred == patternPred);	
		} else {
			return (targetPred <= patternPred);
		}
	}


	/**
	 * Computes the outgoing lookahead set for the given set of nodes connected to the considered candidate node.
	 * @param neighbors The nodes connected to the considered node.
	 * @param data Matching data stored for the graph corresponding to the nodes in neighbors.
	 * @return The number of nodes in neighbors that have not been matched yet, but that are reachable via a single
	 *         outgoing edge from the candidate node.
	 */
	private int computeLookahead(TIntArrayList neighbors, VF2GraphData data) {
		int lookaheadIn = 0;
		for(int i=0; i < neighbors.size(); i++) {
			int next = neighbors.get(i);
			
			if(data.containsMatch(next) && data.containsOutgoing(next)) {
				++lookaheadIn;
			}
		}
		
		return lookaheadIn;
	}

}
