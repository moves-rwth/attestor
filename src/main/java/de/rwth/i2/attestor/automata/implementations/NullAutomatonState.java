package de.rwth.i2.attestor.automata.implementations;

import de.rwth.i2.attestor.automata.AutomatonState;

import java.util.HashSet;
import java.util.Set;

public class NullAutomatonState implements AutomatonState {

    @Override
    public boolean isFinal() {

        return false;
    }

    @Override
    public Set<String> getAtomicPropositions() {

        return new HashSet<>();
    }

    @Override
    public Set<String> getAllAtomicPropositions() {

        return new HashSet<>();
    }
}
