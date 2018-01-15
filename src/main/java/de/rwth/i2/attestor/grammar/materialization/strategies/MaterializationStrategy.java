package de.rwth.i2.attestor.grammar.materialization.strategies;

import de.rwth.i2.attestor.grammar.materialization.util.ViolationPoints;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.Collection;

/**
 * A strategy that determines how (abstract) program states are materialized (partially concretized) such
 * that concrete program semantics becomes applicable afterwards.
 * Materialization is usually applied relative to a given set of violation points that prevent the concrete
 * semantics from being executed.
 *
 * @author Christoph
 */
public interface MaterializationStrategy {

    /**
     * Attempts to materialize a given program states such that the provided
     *
     * @param heapConfiguration The program state that should be materialized.
     * @param potentialViolationPoints A specification of points in the program state that prevent the
     *                                 concrete semantics from being executed.
     * @return A list of materialized program states in which all provided violation points have been resolved.
     */
    Collection<HeapConfiguration> materialize(HeapConfiguration heapConfiguration, ViolationPoints potentialViolationPoints);

}
