package de.rwth.i2.attestor.graph.morphism;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * Captures a current, possibly incomplete, candidate for a graph morphism that is constructed step by step
 * by a {@link VF2Algorithm}. In particular, VF2State supports method {@link #backtrack()} to reset it to
 * its previous state.
 *
 * @author Christoph
 */
public class VF2State {

	/**
	 * Used for debug output and error reporting.
	 */
	private static final Logger logger = LogManager.getLogger( "VF2State" );

	/**
	 * The currently found partial mapping from pattern to target together
	 * with additional data to prune the search space.
	 */
	private final VF2GraphData pattern;

	/**
	 * The currently found partial mapping from target to pattern together
	 * with additional data to prune the search space.
	 */
	private final VF2GraphData target;

	/**
	 * Computes a new initial state from two graphs
	 * @param patternGraph The pattern graph that should be mapped into the target graph.
	 * @param targetGraph The target graph.
	 */
	public VF2State(Graph patternGraph, Graph targetGraph) {
		pattern = new VF2GraphData(patternGraph, null);
		target = new VF2GraphData(targetGraph, null);
	}

	/**
	 * Creates a copy of a given state.
	 * Note that apart from a few constants, a shallow copy is created.
	 * In particular, the underlying partial matching is shared between the state and its copy.
	 * @param state The state to be copied.
	 */
    private VF2State(VF2State state) {
		pattern = new VF2GraphData(state.pattern);
		target = new VF2GraphData(state.target);
	}

	/**
	 * @return A shallow copy of this state.
	 */
	public VF2State shallowCopy() {
		return new VF2State(this);
	}

	/**
	 * @return The data stored for the pattern graph within this state.
	 */
	public VF2GraphData getPattern() {
		return pattern;
	}

	/**
	 * @return The data stored for the target graph within this state.
	 */
	public VF2GraphData getTarget() {
		return target;
	}

	/**
	 * Undoes the last change to the state.
	 */
	public void backtrack() {		
		pattern.backtrack();
		target.backtrack();
	}

	/**
	 * Adds a new candidate to the underlying partial matching.
	 * @param candidate The candidate to add to the matching described by this state.
	 */
	public void addCandidate(CandidatePair candidate) {
		pattern.setMatch(candidate.p, candidate.t);
		target.setMatch(candidate.t, candidate.p);
	}

	/**
	 * Computes a list of possible candidates that should be tested for suitable pairs
	 * that can be added to the current partial matching.
	 * @return A list of possible candidate pairs consisting of a node from the pattern graph and a node
	 *         from the target graph.
	 */
	public List<CandidatePair> computeCandidates() {
		
		if(!pattern.isOutgoingEmpty() && !target.isOutgoingEmpty()) {
			
			return computeOutgoingCandidates();
		}
		
		if(!pattern.isIngoingEmpty() && !target.isIngoingEmpty()) {

			return computeIngoingCandidates();
		}
		
		return computeAllCandidates();
		
	}

	/**
	 * Computes possible candidate pairs based on nodes reachable via outgoing edges from nodes that already have
	 * been matched.
	 * @return The list of candidates found.
	 */
	private List<CandidatePair> computeOutgoingCandidates() {
		
		List<CandidatePair> result = new LinkedList<>();
		int countPatternNodes = pattern.getGraph().size();
		int countTargetNodes = target.getGraph().size();
		int patternMin = VF2GraphData.NULL_NODE;
		
		for(int p = 0; p < countPatternNodes; p++) {
			
			if(pattern.containsOutgoing(p) && !pattern.isLessThan(patternMin, p)) {
				
				for(int t = 0; t < countTargetNodes; t++) {
				
					if(target.containsOutgoing(t)) {
						result.add(  new CandidatePair(p, t) );
						patternMin = p;
					}
				}
			}
		}
		
		return result;
	}


	/**
	 * Computes possible candidate pairs based on nodes reachable via ingoing edges from nodes that already have
	 * been matched.
	 * @return The list of candidates found.
	 */
	private List<CandidatePair> computeIngoingCandidates() {
		List<CandidatePair> result = new LinkedList<>();
		int countPatternNodes = pattern.getGraph().size();
		int countTargetNodes = target.getGraph().size();
		int patternMin = VF2GraphData.NULL_NODE;
		
		for(int p = 0; p < countPatternNodes; p++) {
			
			if(pattern.containsIngoing(p) && !pattern.isLessThan(patternMin, p)) {
				for(int t = 0; t < countTargetNodes; t++) {
				
					if(target.containsIngoing(t)) {
						result.add(  new CandidatePair(p, t) );
						patternMin = p;
					}
				}
			}
		}
		
		return result;
	}

	/**
	 * Computes all possible candidate pairs based on nodes that have not been matched yet.
	 * @return The list of candidates found.
	 */
	private List<CandidatePair> computeAllCandidates() {
		List<CandidatePair> result = new LinkedList<>();
		int countPatternNodes = pattern.getGraph().size();
		int countTargetNodes = target.getGraph().size();
		int patternMin = VF2GraphData.NULL_NODE;
		
		for(int p = 0; p < countPatternNodes; p++) {
			
			if(!pattern.containsMatch(p) && !pattern.isLessThan(patternMin, p)) {
				
				for(int t = 0; t < countTargetNodes; t++) {
				
					if(!target.containsMatch(t)) {
						result.add(  new CandidatePair(p, t) );
						patternMin = p;
					}
				}
			}
		}
		
		return result;
	}
	
}
