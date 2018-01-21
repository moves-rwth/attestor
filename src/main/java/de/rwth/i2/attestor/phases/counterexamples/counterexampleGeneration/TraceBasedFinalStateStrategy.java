package de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration;

import de.rwth.i2.attestor.stateSpaceGeneration.FinalStateStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.SemanticsCommand;

import java.util.Collection;

public class TraceBasedFinalStateStrategy implements FinalStateStrategy {

    private final StateSubsumptionStrategy stateSubsumptionStrategy;
    private final ProgramState traceFinalState;

    public TraceBasedFinalStateStrategy(StateSubsumptionStrategy subsumptionStrategy, ProgramState traceFinalState) {

        this.stateSubsumptionStrategy = subsumptionStrategy;
        this.traceFinalState = traceFinalState;
    }

    @Override
    public boolean isFinalState(ProgramState state, Collection<ProgramState> successorStates,
                                SemanticsCommand semanticsCommand) {

        return stateSubsumptionStrategy.subsumes(state, traceFinalState);
    }


}
