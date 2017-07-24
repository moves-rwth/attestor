package de.rwth.i2.attestor.automata.implementations;

import de.rwth.i2.attestor.automata.AutomatonState;

import java.util.Set;

public class VisitedAutomatonState implements AutomatonState {

    @Override
    public boolean isFinal() {

        return false;
    }

    @Override
    public Set<String> getAtomicPropositions() {

        return null;
    }
}
