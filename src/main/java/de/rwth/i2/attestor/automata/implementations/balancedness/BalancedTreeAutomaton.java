package de.rwth.i2.attestor.automata.implementations.balancedness;

import de.rwth.i2.attestor.automata.HeapAutomaton;

/**
 * Specialized heap automaton to determine whether a binary tree is balanced.
 * During executing the automaton, selectors of the provided heap configurations are
 * annotated with additional balancedness information.
 *
 * @author Christoph
 */
public class BalancedTreeAutomaton extends HeapAutomaton {

    public BalancedTreeAutomaton() {

        super(new BalancedTreeTransitionRelation());
    }
}
