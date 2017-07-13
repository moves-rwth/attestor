package de.rwth.i2.attestor.stateSpaceGeneration;

import java.util.Set;

/**
 * A strategy that determines how states are labeled with atomic propositions for further analysis, such as
 * model-checking.
 *
 * @author Christoph
 */
public interface StateLabelingStrategy {

    /**
     * Determines the atomic propositions assigned to the given program state.
     * @param programState The program state whose atomic propositions should be determined.
     * @return The set of atomic propositions assigned to the program state.
     */
	Set<String> computeAtomicPropositions(ProgramState programState);
	
}
