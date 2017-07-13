package de.rwth.i2.attestor.graph.morphism;

/**
 * An auxiliary class representing a pair of two nodes that represent a candidate for a matching:
 * One node from the pattern Graph and one from the target Graph.
 *
 * @author Christoph
 *
 */
public final class CandidatePair {

	/**
	 * The pattern node of a candidate pair.
	 */
	public final int p;
	
	/**
	 * The target node of a candidate pair.
	 */
	public final int t;
	
	/**
	 * @param patternNode The candidate node belonging to the pattern graph.
	 * @param targetNode The candidate node belonging to the target graph.
	 */
	public CandidatePair(int patternNode, int targetNode) {
		p = patternNode;
		t = targetNode;
	}
	
	/**
	 * @return A string representation of a candidate pair.
	 */
	public String toString() {
		
		return "(" + p + ", " + t + ")";
	}
}
