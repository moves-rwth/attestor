package de.rwth.i2.attestor.automata.composition;

import de.rwth.i2.attestor.automata.AutomatonState;

import java.util.Set;

/**
 * State of a heap automaton that realizes the union of two heap automata.
 *
 * @author Christoph
 */
public class UnionAutomatonState implements AutomatonState {

    private AutomatonState firstState;
    private AutomatonState secondState;

    public UnionAutomatonState(AutomatonState firstState, AutomatonState secondState) {

        this.firstState = firstState;
        this.secondState = secondState;
    }

    public AutomatonState first() {

        return firstState;
    }

    public AutomatonState second() {

        return secondState;
    }

    @Override
    public boolean isFinal() {

        return (firstState.isFinal() && secondState == null)
                || (firstState == null && secondState.isFinal());
    }

    @Override
    public Set<String> getAtomicPropositions() {

        return null;
    }
}
