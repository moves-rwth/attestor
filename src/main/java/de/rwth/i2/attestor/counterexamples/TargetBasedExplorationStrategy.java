package de.rwth.i2.attestor.counterexamples;

import de.rwth.i2.attestor.stateSpaceGeneration.ExplorationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;

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
    public boolean check(ProgramState state, StateSpace stateSpace) {

        ProgramState foundState = null;
        for(ProgramState tState : targetStates) {
            if(subsumptionStrategy.subsumes(state, tState)) {
               foundState = tState;
               break;
            }
        }

        if(foundState != null) {
           targetStates.remove(foundState);
           return true;
        }
        return false;
    }
}
