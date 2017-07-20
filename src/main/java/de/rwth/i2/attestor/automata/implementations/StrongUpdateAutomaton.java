package de.rwth.i2.attestor.automata.implementations;

import de.rwth.i2.attestor.automata.HeapAutomaton;

public class StrongUpdateAutomaton extends HeapAutomaton {

    public StrongUpdateAutomaton() {
        super(new StrongUpdateTransitionRelation());
    }
}
