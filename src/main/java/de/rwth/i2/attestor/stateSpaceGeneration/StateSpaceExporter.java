package de.rwth.i2.attestor.stateSpaceGeneration;

import java.io.IOException;

/**
 * A general method to export a StateSpace.
 * The exact format has to be specified by an implementation.
 *
 * @author Christoph
 */
public interface StateSpaceExporter {

    /**
     * Exports a StateSpace into one or more files with the given name.
     * @param stateSpace The StateSpace that should be exported.
     */
	void export(StateSpace stateSpace ) throws IOException;

}
