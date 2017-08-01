package de.rwth.i2.attestor.automata;

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

}
