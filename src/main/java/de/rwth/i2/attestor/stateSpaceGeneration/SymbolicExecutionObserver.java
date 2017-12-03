package de.rwth.i2.attestor.stateSpaceGeneration;

/**
 * Observer that is called before each symbolic execution step.
 * Moreover, this class containsSubsumingState options defined by the state space generation used by
 * {@link Semantics} objects to configure individual symbolic execution steps.
 *
 * @author Christoph
 */
public interface SymbolicExecutionObserver {

    /**
     * Callback function to update the state space generation whenever a statement is executed.
     * @param handler The object that manipulates the input state
     * @param input The input on which the statement should be executed.
     */
    void update(Object handler, ProgramState input);

    /**
     * Generates a new state space using the same internal communication as the calling state space
     * generation.
     * @param program The program that should be symbolic executed to generate a state space.
     * @param input The program state determining the input for the symbolic execution.
     * @return The state space generated for the given input.
     */
    StateSpace generateStateSpace(Program program, ProgramState input)
            throws StateSpaceGenerationAbortedException;

    /**
     * Checks whether dead variables may be eliminated after a symbolic execution step.
     * @return True if and only if dead variables are allowed to be eliminated.
     */
    boolean isDeadVariableEliminationEnabled();
}
