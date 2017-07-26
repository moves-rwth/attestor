package de.rwth.i2.attestor.automata;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.List;

/**
 * Interface for the transition relation of a heap automaton.
 *
 * @author Christoph
 */
public interface TransitionRelation {

    /**
     * Applies the transition relation to a heap configuration.
     * In case the provided heap configuration contains nonterminal edges, it is required that they are annotated
     * with a heap automaton state.
     * Note that labels of the provided heap configuration may be altered in order to add
     * information that have been computed to determine the resulting state.
     *
     * @param heapConfiguration A heap configuration without nonterminal edges.
     * @return The state obtained for the given heap configuration.
     */
    AutomatonState move(List<AutomatonState> ntAssignment, HeapConfiguration heapConfiguration);

}
