package de.rwth.i2.attestor.graph;

import java.util.Collection;

import de.rwth.i2.attestor.graph.digraph.NodeLabel;

/**
 * General interface for nonterminal symbols.
 *
 * @author Christoph
 */
public interface Nonterminal extends NodeLabel {

    /**
     * @return The number of nodes that has to be attached to this nonterminal.
     */
    int getRank();

    /**
     * @param tentacle The position in the sequence of attached nodes that should be checked.
     * @return True if and only if the provided provision never produces outgoing selector edges
     * for this Nonterminal.
     */
    boolean isReductionTentacle(int tentacle);

    /**
     * @param tentacle The position in the sequence of attached nodes that should be marked
     *                 as a reduction tentacle.
     */
    void setReductionTentacle(int tentacle);

    /**
     * @param tentacle The position in the sequence of attached nodes that should be marked
     *                 as "not a reduction tentacle".
     */
    void unsetReductionTentacle(int tentacle);
    
    /**
     * the set of tentacles potentially (by application of rules) reachable from the given tentacle
     * @param tentacle the tentacle from which the requested tentacles are reachable
     * @return the set of tentacles reachable from the given tentacle
     */
    Collection<Integer> reachableTentaclesFrom( int tentacle );

    /**
     * @return The label underlying this nonterminal symbol.
     */
    String getLabel();

}
