package de.rwth.i2.attestor.automata.implementations;

import de.rwth.i2.attestor.automata.AutomatonState;

public class ReachabilityAutomatonState implements AutomatonState {

    @Override
    public boolean isFinal() {

        return false;
    }
}
