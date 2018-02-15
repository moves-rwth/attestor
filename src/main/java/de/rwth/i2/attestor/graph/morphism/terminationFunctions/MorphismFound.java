package de.rwth.i2.attestor.graph.morphism.terminationFunctions;

import de.rwth.i2.attestor.graph.morphism.TerminationFunction;
import de.rwth.i2.attestor.graph.morphism.VF2GraphData;
import de.rwth.i2.attestor.graph.morphism.VF2State;

/**
 * Determines whether a given {@link VF2State} corresponds to a complete graph morphism
 * and thus no further steps have to be executed to find a morphism.
 *
 * @author Christoph
 */
public class MorphismFound implements TerminationFunction {

    @Override
    public boolean eval(VF2State state) {

        VF2GraphData pattern = state.getPattern();

        return pattern.getMatchingSize() == pattern.getGraph().size();
    }

}
