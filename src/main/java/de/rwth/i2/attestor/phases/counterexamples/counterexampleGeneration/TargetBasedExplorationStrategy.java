package de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration;

import de.rwth.i2.attestor.stateSpaceGeneration.ExplorationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

import java.util.Collection;

public class TargetBasedExplorationStrategy implements ExplorationStrategy {

    private final Collection<ProgramState> targetStates;
    private final StateSubsumptionStrategy subsumptionStrategy;

    public TargetBasedExplorationStrategy(Collection<ProgramState> targetStates,
                                          StateSubsumptionStrategy subsumptionStrategy) {

        this.targetStates = targetStates;
        this.subsumptionStrategy = subsumptionStrategy;
    }

    @Override
    public boolean check(ProgramState state, boolean isMaterializedState) {

        ProgramState foundState = null;
        for(ProgramState tState : targetStates) {
            if(subsumptionStrategy.subsumes(state, tState)) {
               foundState = tState;
               break;
            }
        }

        if(foundState != null) {
           targetStates.remove(foundState);
        }

        return !targetStates.isEmpty();
    }
}
