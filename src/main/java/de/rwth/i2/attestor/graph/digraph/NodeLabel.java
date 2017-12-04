package de.rwth.i2.attestor.graph.digraph;

/**
 * General interface for all node labels that may occur in as labels
 * of nodes in a {@link LabeledDigraph}.
 *
 * @author Christoph
 */
public interface NodeLabel {

    /**
     * @param other Some other NodeLabel.
     * @return True if and only if this NodeLabel and other are considered to match.
     * Note that, depending on the label, this might not necessarily coincide
     * with equals().
     */
    default boolean matches(NodeLabel other) {

        return this.equals(other);
    }
}
