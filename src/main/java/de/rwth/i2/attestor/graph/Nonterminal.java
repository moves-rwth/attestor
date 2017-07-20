package de.rwth.i2.attestor.graph;

import de.rwth.i2.attestor.graph.digraph.NodeLabel;

/**
 * General interface for nonterminal symbols.
 * 
 * @author Christoph
 *
 */
public interface Nonterminal extends Comparable<Nonterminal>, NodeLabel{

	/**
	 * @return The number of nodes that has to be attached to this nonterminal.
	 */
	int getRank();
	
	/**
	 * @param tentacle The position in the sequence of attached nodes that should be checked.
	 * @return True if and only if the provided provision never produces outgoing selector edges
	 *         for this Nonterminal.
	 */
	boolean isReductionTentacle( int tentacle );
	
	/** 
	 * @param tentacle The position in the sequence of attached nodes that should be marked
	 *                 as a reduction tentacle.
	 */
	void setReductionTentacle( int tentacle);
	
	/**
	 * @param tentacle The position in the sequence of attached nodes that should be marked
	 *                 as "not a reduction tentacle".
	 */
	void unsetReductionTentacle( int tentacle );

	/**
	 * checks whether the pure labels of the nonterminal (i.e. without stack or other
	 * additional information) matches. 
	 * E.g. B[ssZ], B[sX], B[s()], B[abc] would all be considered to have matching label
	 * <br>
	 * However, this does'nt match for nonterminals of different type, e.g. B and B[Z] should not
	 * match.
	 * @param nonterminal the nonterminal to check against
	 * @return true if the label of this and nonterminl match.
	 */
	boolean labelMatches(Nonterminal nonterminal);
}
