package de.rwth.i2.attestor.graph.morphism.terminationFunctions;

import de.rwth.i2.attestor.graph.morphism.TerminationFunction;
import de.rwth.i2.attestor.graph.morphism.VF2PatternGraphData;
import de.rwth.i2.attestor.graph.morphism.VF2State;

/**
 * Determines whether a {@link VF2State} corresponds to a graph isomorphism.
 * 
 * Note that this function does <b>not</b> check whether a state really is a graph isomorphism.
 * Instead it merely checks whether an algorithm to determine a graph isomorphism may stop its
 * execution, because it already found an isomorphism.
 * 
 * @author Christoph
 *
 */
public class IsomorphismFound implements TerminationFunction {

	@Override
	public boolean eval(VF2State state) {
		
		VF2PatternGraphData pattern = state.getPattern();
		
		return pattern.getMatchingSize() == pattern.getGraph().size()
				&& pattern.getGraph().size() == state.getTarget().getGraph().size();
	}

}
