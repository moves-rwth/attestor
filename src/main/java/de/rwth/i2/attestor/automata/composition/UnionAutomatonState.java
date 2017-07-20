package de.rwth.i2.attestor.automata.composition;

import de.rwth.i2.attestor.automata.AutomatonState;

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
}
