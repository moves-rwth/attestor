package de.rwth.i2.attestor.automata;

import java.util.Set;

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

    /**
     * @return Provides a representation of this state as a set of strings.
     */
    Set<String> getAtomicPropositions();
}
