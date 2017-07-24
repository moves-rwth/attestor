package de.rwth.i2.attestor.automata;

import java.util.List;
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
    public Set<String> getAtomicPropositions();
}
