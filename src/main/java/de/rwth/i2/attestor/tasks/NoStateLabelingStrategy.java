package de.rwth.i2.attestor.tasks;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateLabelingStrategy;

import java.util.HashSet;
import java.util.Set;


/**
 * A simple state labeling strategy that returns an empty set of atomic propositions for every state.
 *
 * @author Christoph
 */
public class NoStateLabelingStrategy implements StateLabelingStrategy {

    /**
     * The empty dummy set assigned to every state.
     */
	private static final Set<String> dummy = new HashSet<>();
	
	@Override
	public Set<String> computeAtomicPropositions(ProgramState programState) {
	
		return dummy;
	}

}
