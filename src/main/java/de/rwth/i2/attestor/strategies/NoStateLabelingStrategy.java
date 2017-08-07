package de.rwth.i2.attestor.strategies;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateLabelingStrategy;


/**
 * A simple state labeling strategy that returns an empty set of atomic propositions for every state.
 *
 * @author Christoph
 */
public class NoStateLabelingStrategy implements StateLabelingStrategy {

	@Override
	public void computeAtomicPropositions(ProgramState programState) {
	}

}
