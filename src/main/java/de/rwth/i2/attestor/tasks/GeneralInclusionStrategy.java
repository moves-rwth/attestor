package de.rwth.i2.attestor.tasks;

import de.rwth.i2.attestor.stateSpaceGeneration.InclusionStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

/**
 * A simple approximation of the inclusion problem by checking for isomorphic states.
 *
 * @author Christoph
 */
public class GeneralInclusionStrategy implements InclusionStrategy {

	@Override
	public boolean isIncludedIn(ProgramState left, ProgramState right) {

		return left.equals(right);
	}

}
