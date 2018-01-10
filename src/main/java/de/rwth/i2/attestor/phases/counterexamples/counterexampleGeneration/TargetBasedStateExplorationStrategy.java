package de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateExplorationStrategy;

import java.util.Collection;
import java.util.LinkedList;

public class TargetBasedStateExplorationStrategy implements StateExplorationStrategy {

    private LinkedList<ProgramState> unexploredStates = new LinkedList<>();

    private final Collection<ProgramState> targetStates;
    private final StateSubsumptionStrategy subsumptionStrategy;

    public TargetBasedStateExplorationStrategy(Collection<ProgramState> targetStates,
                                          StateSubsumptionStrategy subsumptionStrategy) {

        this.targetStates = targetStates;
        this.subsumptionStrategy = subsumptionStrategy;
    }

    @Override
    public boolean hasUnexploredStates() {

        return !unexploredStates.isEmpty();
    }

    @Override
    public ProgramState getNextUnexploredState() {

        return unexploredStates.removeFirst();
    }

    @Override
    public void addUnexploredState(ProgramState state, boolean isMaterializedState) {

        if(checkTargetTests(state)) {
            unexploredStates.addLast(state);
        }
    }

    private boolean checkTargetTests(ProgramState state) {

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
