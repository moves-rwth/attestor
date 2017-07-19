package de.rwth.i2.attestor.automata;

/**
 * A symbol that is annotated with a heap automaton state.
 *
 * @author Christoph
 */
public interface StateAnnotatedSymbol {

    /**
     * @return The heap automaton state the symbol is annotated with.
     */
    HeapAutomatonState getState();

    /**
     * Provides the same symbol with the provided annotation.
     * @param state The new annotation.
     * @return An annotated symbol with the provided state. The symbol itself is the same as this object.
     */
    StateAnnotatedSymbol withState(HeapAutomatonState state);
}
