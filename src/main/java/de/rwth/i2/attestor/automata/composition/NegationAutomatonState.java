package de.rwth.i2.attestor.automata.composition;

import de.rwth.i2.attestor.automata.AutomatonState;

import java.util.HashSet;
import java.util.Set;

/**
 * State of a heap automaton realizing the negation of another <b>deterministic</b> heap automaton.
 *
 * @author Christoph
 */
public class NegationAutomatonState implements AutomatonState {

    /**
     * The state of the original deterministic heap automaton.
     */
    private AutomatonState state;

    private Set<String> atomicPropositions;

    public NegationAutomatonState(AutomatonState state) {
        this.state = state;
        atomicPropositions = new HashSet<>(state.getAllAtomicPropositions());
        atomicPropositions.removeAll(state.getAtomicPropositions());
    }

    @Override
    public boolean isFinal() {

        return !state.isFinal();
    }

    @Override
    public Set<String> getAtomicPropositions() {

        return atomicPropositions;
    }

    @Override
    public Set<String> getAllAtomicPropositions() {

        return state.getAllAtomicPropositions();
    }
}
