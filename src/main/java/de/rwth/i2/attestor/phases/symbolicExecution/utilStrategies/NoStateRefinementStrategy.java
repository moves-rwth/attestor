package de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.SemanticsCommand;
import de.rwth.i2.attestor.stateSpaceGeneration.StateRefinementStrategy;

public class NoStateRefinementStrategy implements StateRefinementStrategy {

    @Override
    public ProgramState refine(SemanticsCommand semanticsCommand, ProgramState state) {

        return state;
    }
}
