package de.rwth.i2.attestor.stateSpaceGeneration;

/**
 * In a {@link StateSpace} the transition relation of the underlying transition system
 * consists of a list of StateSuccessor objects for each state.
 * A StateSuccessor models a target state together with the label of a transition to that state.
 *
 * @author Christoph
 */
public class StateSuccessor {

    /**
     * The label of a transition to the target state.
     */
	private final String label;

    /**
     * The target state.
     */
	private final ProgramState target;

    /**
     * Creates a new StateSuccessor.
     * @param label The label of the transition to the target state.
     * @param target The target state.
     */
	public StateSuccessor(String label, ProgramState target) {
		
		this.label = label;
		this.target = target;
	}

    /**
     * @return The label of the transition to the target state.
     */
	public String getLabel() {
		
		return label;
	}

    /**
     * @return The target state.
     */
	public ProgramState getTarget() {
		
		return target;
	}

    /**
     * @param successor Another StateSuccessor object.
     * @return True if and only if target state and transition label are equal.
     */
	public boolean equals(StateSuccessor successor) {

		return label.equals(successor.label) && target.equals(successor.target);
	}
	
	
}
