package de.rwth.i2.attestor.refinement;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.Set;

/**
 * A very simplified heap automaton that has exactly one state.
 * In particular this means that no grammar refinement is required.
 *
 * @author Christoph
 */
public interface StatelessHeapAutomaton {

    /**
     * Perform a transition on the given graph and compute the corresponding atomic propositions
     *
     * @param heapConfiguration The heap configuration to analyze.
     * @return The corresponding set of atomic propositions.
     */
    Set<String> transition(HeapConfiguration heapConfiguration);
}
