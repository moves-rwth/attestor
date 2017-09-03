package de.rwth.i2.attestor.stateSpaceGeneration;

import de.rwth.i2.attestor.stateSpaceGeneration.stateSpace.StateSpace;

import java.io.IOException;

/**
 * A general method to export a OldStateSpace.
 * The exact format has to be specified by an implementation.
 *
 * @author Christoph
 */
public interface StateSpaceExporter {

    /**
     * Exports a OldStateSpace into one or more files with the given name.
     * @param stateSpace The OldStateSpace that should be exported.
     * @param program The program used to generate the state space.
     * @throws IOException if writing of exported files fails.
     */
	void export(StateSpace stateSpace, Program program) throws IOException;

}
