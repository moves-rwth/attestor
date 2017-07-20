package de.rwth.i2.attestor.automata.implementations;

import de.rwth.i2.attestor.automata.HeapAutomaton;
import de.rwth.i2.attestor.automata.TransitionRelation;

public class AVLBalanceAutomaton extends HeapAutomaton {

    public AVLBalanceAutomaton() {

        super(new AVLBalanceTransitionRelation());
    }
}
