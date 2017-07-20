package de.rwth.i2.attestor.stateSpaceGeneration;

/**
 * A general method to export a StateSpace into one or more files.
 * The exact format has to be specified by an implementation.
 */
public interface StateSpaceExporter {

    /**
     * Exports a StateSpace into one or more files with the given name.
     * @param name The name of the file(s) associated with the exported StateSpace.
     * @param stateSpace The StateSpace that should be exported.
     */
	void export( String name, StateSpace stateSpace );

}
