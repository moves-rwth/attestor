package de.rwth.i2.attestor.graph.morphism.feasibility;

import de.rwth.i2.attestor.graph.morphism.CandidatePair;
import de.rwth.i2.attestor.graph.morphism.FeasibilityFunction;
import de.rwth.i2.attestor.graph.morphism.VF2GraphData;
import de.rwth.i2.attestor.graph.morphism.VF2State;
import gnu.trove.list.array.TIntArrayList;

/**
 * Determines the whether the current candidate pair cannot belong to a graph morphism due to a mismatch
 * in the lookahead sets in the successors or predecessors of one of the candidate nodes.
 * This set corresponds to the number of nodes in the predecessors or successors of a candidate node
 * that have not been matched yet.
 * A mismatch may either consist of less ingoing edges in the target than in the pattern (checkEquality=false)
 * or an unequal number (checkEquality=true).
 *
 * @author Christoph
 */
public class TwoStepLookahead implements FeasibilityFunction {


    /**
     * Determines whether the lookahead sets of pattern and target
     * have to be equal or target sets are allowed to be larger.
     */
	private final boolean checkEquality;

    /**
     * @param checkEquality Determines whether equal lookahead sets are required.
     */
	public TwoStepLookahead(boolean checkEquality) {
		this.checkEquality = checkEquality;
	}
	
	@Override
	public boolean eval(VF2State state, CandidatePair candidate) {
		
		VF2GraphData pattern = state.getPattern();
		VF2GraphData target = state.getTarget();
		
		int patternPred = computeLookahead(
				pattern.getGraph().getPredecessorsOf(candidate.p),
				pattern
				);
		
		int targetPred = computeLookahead(
				target.getGraph().getPredecessorsOf(candidate.t),
				target
				); 
		
		if(checkEquality) {
			
			if(patternPred != targetPred) {		

				return false;
			}
		} else {
			
			if(targetPred < patternPred) {
				return false;
			}
		}
		
		int patternSucc = computeLookahead(
				pattern.getGraph().getSuccessorsOf(candidate.p),
				pattern
				);
		
		int targetSucc = computeLookahead(
				target.getGraph().getSuccessorsOf(candidate.t),
				target
				);

		if(checkEquality) {
			return patternSucc == targetSucc;
		} else {
			return (patternSucc <= targetSucc);	
		}
	}


    /**
     * Computes the lookahead set for the given set of nodes connected to the considered candidate node.
     * @param nodes The nodes connected to the considered node.
     * @param data Matching data stored for the graph corresponding to the nodes in neighbors.
     * @return The number of nodes in neighbors that have not been matched yet.
     */
	private int computeLookahead(TIntArrayList nodes, VF2GraphData data) {
		
		int lookahead = 0;
		for(int i=0; i < nodes.size(); i++) {
			int next = nodes.get(i);
	    	
			//The original algorithm proposes if(!data.containsNeighbor(next) && !data.containsMatch(next)) {
			// but we are a bit relaxed here due to external nodes
	    	if(!data.containsMatch(next)) {
	    		++lookahead;
	    	}
	    	
	    }
	    
	    return lookahead;
	}

}
