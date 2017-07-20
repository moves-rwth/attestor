package de.rwth.i2.attestor.automata.implementations;

import de.rwth.i2.attestor.automata.HeapAutomaton;

public class ReachabilityAutomaton extends HeapAutomaton {

    public ReachabilityAutomaton() {
        super(new ReachabilityTransitionRelation());
    }
}
