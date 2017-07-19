package de.rwth.i2.attestor.automata;

/**
 * Created by cmath on 7/19/17.
 */
public interface StateAnnotatedSymbol {

    HeapAutomatonState getState();

    StateAnnotatedSymbol withState(HeapAutomatonState state);
}
