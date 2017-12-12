package de.rwth.i2.attestor.graph.heap;

/**
 * A Matching models a mapping from one {@link HeapConfiguration}, called the pattern
 * HeapConfiguration, to another HeapConfiguration, called the target HeapConfiguration.
 *
 * @author Christoph
 */
public interface Matching {

    /**
     * @return The HeapConfiguration that determines that pattern that is matched
     * onto a target HeapConfiguration by this Matching.
     */
    HeapConfiguration pattern();

    /**
     * @param element An element of the pattern HeapConfiguration.
     * @return The element in the target HeapConfiguration corresponding to the given
     * element of the pattern HeapConfiguration.
     */
    int match(int element);
}
