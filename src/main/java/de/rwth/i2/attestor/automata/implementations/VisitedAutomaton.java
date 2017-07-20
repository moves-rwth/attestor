package de.rwth.i2.attestor.automata.implementations;

import de.rwth.i2.attestor.automata.HeapAutomaton;

public class VisitedAutomaton extends HeapAutomaton {

    public VisitedAutomaton() {
        super(new VisitedTransitionRelation());
    }
}
