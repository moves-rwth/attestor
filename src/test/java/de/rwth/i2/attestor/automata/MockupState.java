package de.rwth.i2.attestor.automata;

/**
 * Created by cmath on 7/19/17.
 */
public class MockupState implements HeapAutomatonState {

    private int state;
    private boolean finalState;

    public MockupState(int state, boolean finalState) {
        this.state = state;
        this.finalState = finalState;
    }

    @Override
    public boolean isFinal() {
        return finalState;
    }

    public int getState() {
        return state;
    }

    public boolean equals(Object o) {
        if(o instanceof MockupState) {
            MockupState s = (MockupState) o;
            return s.state == state && s.finalState == finalState;
        }
        return false;
    }

    public int hashCode() {
        return state;
    }
}
