package de.rwth.i2.attestor.automata.composition;

import de.rwth.i2.attestor.automata.AutomatonState;

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

    public NegationAutomatonState(AutomatonState state) {
        this.state = state;
    }

    @Override
    public boolean isFinal() {

        return !state.isFinal();
    }

    @Override
    public Set<String> getAtomicPropositions() {

        return null;
    }
}
