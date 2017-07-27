package de.rwth.i2.attestor.automata.implementations.balancedness;

import de.rwth.i2.attestor.automata.HeapAutomaton;

public class BalancedTreeAutomaton extends HeapAutomaton {

    public BalancedTreeAutomaton() {

        super(new BalancedTreeTransitionRelation());
    }
}
