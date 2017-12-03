package de.rwth.i2.attestor.stateSpaceGeneration;

/**
 * The strategy performed to canonicalize (abstract) a ProgramState, which results in one or more
 * abstract program states.
 *
 * @author Christoph
 */
public interface CanonicalizationStrategy {

    /**
     * Performs the canonicalization of a single program state.
     *
     * @param state The ProgramState that should be abstracted.
     * @return An abstract program states that covers the original program state conf.
     */
    ProgramState canonicalize(ProgramState state);
}
