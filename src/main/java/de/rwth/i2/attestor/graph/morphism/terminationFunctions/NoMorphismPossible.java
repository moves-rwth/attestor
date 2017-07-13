package de.rwth.i2.attestor.graph.morphism.terminationFunctions;

import de.rwth.i2.attestor.graph.morphism.TerminationFunction;
import de.rwth.i2.attestor.graph.morphism.VF2GraphData;
import de.rwth.i2.attestor.graph.morphism.VF2State;

/**
 * 
 * Determines whether it is impossible that a given {@link VF2State} can be extended
 * by a search algorithm to a full graph morphism.
 * 
 * @author Christoph
 *
 */
public class NoMorphismPossible implements TerminationFunction {

	@Override
	public boolean eval(VF2State state) {
		
		VF2GraphData pattern = state.getPattern();
		VF2GraphData target = state.getTarget();
		
		return 
				pattern.getGraph().size() > target.getGraph().size()
				|| pattern.getTerminalInSize() > target.getTerminalInSize()
				|| pattern.getTerminalOutSize() > target.getTerminalOutSize()
				;
		
	}

}
