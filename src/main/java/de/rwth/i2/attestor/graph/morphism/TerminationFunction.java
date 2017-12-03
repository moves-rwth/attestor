package de.rwth.i2.attestor.graph.morphism;

/**
 * A TerminationFunction determines whether the search for a graph Morphism has to be stopped.
 * Such functions may, for example, indicate that a morphism has been found or that it is
 * impossible to find another graph morphism starting in the current state.
 *
 * @author Christoph
 */
public interface TerminationFunction {

    /**
     * Checks whether searching for a morphism can be stopped.
     *
     * @param state The current state of the morphism constructed so far.
     * @return True if and only if the search for a morphism should be terminated.
     */
    boolean eval(VF2State state);

}
