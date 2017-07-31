package de.rwth.i2.attestor.automata.composition;

import de.rwth.i2.attestor.automata.HeapAutomaton;

public class IntersectionAutomaton extends HeapAutomaton {

    public IntersectionAutomaton(HeapAutomaton lhs, HeapAutomaton rhs) {

        super(new IntersectionTransitionRelation(
                lhs.getTransitionRelation(),
                rhs.getTransitionRelation()
        ));
    }
}
