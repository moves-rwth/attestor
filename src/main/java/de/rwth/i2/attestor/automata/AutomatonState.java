package de.rwth.i2.attestor.automata;

import java.util.List;

/**
 * A state of a heap automaton.
 *
 * @author Christoph
 */
public interface AutomatonState {

    /**
     * @return True if and only if this state is a final state.
     */
    boolean isFinal();
}
