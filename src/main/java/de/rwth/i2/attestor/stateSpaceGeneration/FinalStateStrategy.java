package de.rwth.i2.attestor.stateSpaceGeneration;

import java.util.Collection;

public interface FinalStateStrategy {

    /**
     * Checks whether the given state is a final state
     * @param state The state to check
     * @param successorStates The successor states when executing one further step
     * @param semanticsCommand The statement that is executed for the given state
     * @return True if state should be marked as a final state.
     */
    boolean isFinalState(ProgramState state,
                         Collection<ProgramState> successorStates, SemanticsCommand semanticsCommand);
}
