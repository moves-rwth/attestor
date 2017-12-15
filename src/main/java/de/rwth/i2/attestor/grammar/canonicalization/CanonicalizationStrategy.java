package de.rwth.i2.attestor.grammar.canonicalization;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

/**
 * The strategy performed to canonicalize (abstract) heap configurations which results in one or more
 * abstract heap configurations.
 *
 * @author Christoph
 */
public interface CanonicalizationStrategy {

    /**
     * Performs the canonicalization of a single heap configuration.
     *
     * @param heapConfiguration The heap configuration that should be fully abstracted
     * @return An abstract heap configuration that covers the original one.
     */
    HeapConfiguration canonicalize(HeapConfiguration heapConfiguration);
}
