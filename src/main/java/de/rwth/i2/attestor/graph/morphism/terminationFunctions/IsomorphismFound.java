package de.rwth.i2.attestor.graph.morphism.terminationFunctions;

import de.rwth.i2.attestor.graph.morphism.TerminationFunction;
import de.rwth.i2.attestor.graph.morphism.VF2GraphData;
import de.rwth.i2.attestor.graph.morphism.VF2State;

/**
 * Determines whether a {@link VF2State} corresponds to a graph isomorphism.
 * <p>
 * Note that this function does <b>not</b> check whether a state really is a graph isomorphism.
 * Instead it merely checks whether an algorithm to determine a graph isomorphism may stop its
 * execution, because it already found an isomorphism.
 *
 * @author Christoph
 */
public class IsomorphismFound implements TerminationFunction {

    @Override
    public boolean eval(VF2State state) {

        VF2GraphData pattern = state.getPattern();

        return pattern.getMatchingSize() == pattern.getGraph().size()
                && pattern.getMatchingSize() == state.getTarget().getGraph().size();
    }

}
