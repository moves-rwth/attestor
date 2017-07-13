package de.rwth.i2.attestor.stateSpaceGeneration;

/**
 * A strategy to check whether an abstract program state is subsumed by another
 * program state.
 *
 * @author Christoph
 */
public interface InclusionStrategy {

    /**
     * Checks whether the left program state is subsumed by the right program state.
     * @param left The program state that should be subsumed.
     * @param right The program state that should subsume.
     * @return true if and only if left subsumes right.
     */
	boolean isIncludedIn(ProgramState left, ProgramState right);
}
