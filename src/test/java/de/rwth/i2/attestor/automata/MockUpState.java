package de.rwth.i2.attestor.automata;

/**
 * Simple example of a heap automaton (state) that checks whether selector edges exist.
 *
 * @author Christoph
 */
public class MockUpState implements HeapAutomatonState {

    private int state;
    private boolean finalState;

    public MockUpState(int state, boolean finalState) {
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
        if(o instanceof MockUpState) {
            MockUpState s = (MockUpState) o;
            return s.state == state && s.finalState == finalState;
        }
        return false;
    }

    public int hashCode() {
        return state;
    }
}
