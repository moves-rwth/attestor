package de.rwth.i2.attestor.stateSpaceGeneration;

public interface LabelledProgramState {
	
	/**
	 * Checks whether a program state satisfies an atomic proposition.
	 * @param ap, the proposition the state is checked for
	 * @return true, if the proposition holds, i.e. is contained in the label of the state
	 * 			false, otherwise
	 */
	boolean satisfiesAP(String ap);
	
}
