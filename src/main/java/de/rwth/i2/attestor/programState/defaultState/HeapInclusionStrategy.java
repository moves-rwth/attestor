package de.rwth.i2.attestor.programState.defaultState;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

/**
 * A strategy to check whether the set of all concrete heap configurations captured by one abstract
 * heap configuration is included in the set of all concrete heap configurations
 * captured by another abstract heap configuration.
 *
 * @author Christoph
 */
public interface HeapInclusionStrategy {

    boolean subsumes(HeapConfiguration left, HeapConfiguration right);
}
