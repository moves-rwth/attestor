package de.rwth.i2.attestor.stateSpaceGeneration;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

/**
 * Collection of all options to influence symbolic program execution during runtime.
 *
 * @author Christoph
 */
public interface SemanticsOptions {

    /**
     * Generates a new state space using the same internal settings as the calling state space
     * generation.
     * @param program The program that should be symbolic executed to generate a state space.
     * @param input The initial heap configuration for the symbolic execution.
     * @param scopeDepth The current depth of the scope.
     * @return The state space generated for the given input.
     */
    StateSpace generateStateSpace(Program program, HeapConfiguration input, int scopeDepth)
            throws StateSpaceGenerationAbortedException;

    /**
     * Checks whether dead variables may be eliminated after a symbolic execution step.
     * @return True if and only if dead variables are allowed to be eliminated.
     */
    boolean isDeadVariableEliminationEnabled();
}
