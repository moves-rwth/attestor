package de.rwth.i2.attestor.graph.morphism.feasibility;

import de.rwth.i2.attestor.graph.morphism.CandidatePair;
import de.rwth.i2.attestor.graph.morphism.FeasibilityFunction;
import de.rwth.i2.attestor.graph.morphism.Graph;
import de.rwth.i2.attestor.graph.morphism.VF2GraphData;
import de.rwth.i2.attestor.graph.morphism.VF2State;
import de.rwth.i2.attestor.util.ListUtil;
import gnu.trove.list.array.TIntArrayList;

/**
 * Checks whether the edge labels from and to the pattern candidate node are covered by the corresponding
 * edge labels from and to the target candidate node.
 * This is, for example, required to check whether a CandidatePair may belong to an embedding of a pattern graph
 * in a target graph.
 *
 * @author Christoph
 */
public class EmbeddingEdgeLabels implements FeasibilityFunction {

	@Override
	public boolean eval(VF2State state, CandidatePair candidate) {
		
		VF2GraphData pattern = state.getPattern();
		VF2GraphData target = state.getTarget();
		
		Graph patternGraph = pattern.getGraph();
		Graph targetGraph = target.getGraph();
		
		TIntArrayList succsOfP = patternGraph.getSuccessorsOf(candidate.p);
		for(int i=0; i < succsOfP.size(); i++) {
			
			int succ = succsOfP.get(i);
			if(pattern.containsMatch(succ)) {
				int match = pattern.getMatch(succ);
				
				if(patternGraph.isExternal(candidate.p) && patternGraph.isExternal(succ)) {
					
					if(!ListUtil.isSubsetAsMultiset(
							patternGraph.getEdgeLabel(candidate.p, succ),
							targetGraph.getEdgeLabel(candidate.t, match))
							) {
						return false;
					}
				} else {
					
					if(!ListUtil.isEqualAsMultiset( 
							patternGraph.getEdgeLabel(candidate.p, succ),
							targetGraph.getEdgeLabel(candidate.t, match))
							) {
						return false;
					}		
				}				
			}
		}
		
		TIntArrayList predsOfP = patternGraph.getPredecessorsOf(candidate.p);
		for(int i=0; i < predsOfP.size(); i++) {

			int pred = predsOfP.get(i);
			if(pattern.containsMatch(pred)) {
				int match = pattern.getMatch(pred);
				
				if(patternGraph.isExternal(candidate.p) && patternGraph.isExternal(pred)) {
					
					if(!ListUtil.isSubsetAsMultiset(
							patternGraph.getEdgeLabel(pred, candidate.p),
							targetGraph.getEdgeLabel(match, candidate.t))
							) {
						return false;
					}
				} else {
					
					if(!ListUtil.isEqualAsMultiset( 
							patternGraph.getEdgeLabel(pred, candidate.p),
							targetGraph.getEdgeLabel(match, candidate.t))
							) {
						return false;
					}		
				}	
			}
		}
		
		return true;	
	}
	

}
