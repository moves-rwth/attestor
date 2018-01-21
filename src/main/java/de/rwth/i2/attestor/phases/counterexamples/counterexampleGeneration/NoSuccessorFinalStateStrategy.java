package de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration;

import de.rwth.i2.attestor.stateSpaceGeneration.FinalStateStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.SemanticsCommand;

import java.util.Collection;

public class NoSuccessorFinalStateStrategy implements FinalStateStrategy {

    @Override
    public boolean isFinalState(ProgramState state, Collection<ProgramState> successorStates, SemanticsCommand semanticsCommand) {
        return successorStates.isEmpty();
    }
}
