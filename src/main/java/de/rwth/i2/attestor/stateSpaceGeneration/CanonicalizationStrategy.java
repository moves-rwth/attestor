package de.rwth.i2.attestor.stateSpaceGeneration;

import java.util.Set;

/**
 * The strategy performed to canonicalize (abstract) a ProgramState, which results in one or more
 * abstract program states.
 * The actual abstraction performed may depend on the previously executed
 * program statement.
 *
 * @author Christoph
 */
public interface CanonicalizationStrategy {

    /**
     * Performs the canonicalization of a single program state.
     * @param semantics The last statement that has been executed.
     * @param conf The ProgramState that should be abstracted.
     * @return An abstract program states that covers the original program state conf.
     */
	 ProgramState canonicalize(Semantics semantics, ProgramState conf );

}
