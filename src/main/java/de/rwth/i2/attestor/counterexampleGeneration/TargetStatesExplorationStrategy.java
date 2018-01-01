package de.rwth.i2.attestor.counterexampleGeneration;

import de.rwth.i2.attestor.stateSpaceGeneration.ExplorationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class TargetStatesExplorationStrategy implements ExplorationStrategy {

    private Collection<ProgramState> targetStates;
    private StateMatchingStrategy matchingStrategy;

    public TargetStatesExplorationStrategy(Collection<ProgramState> targetStates,
                                           StateMatchingStrategy matchingStrategy) {

        this.targetStates = new ArrayList<>(targetStates);
        this.matchingStrategy = matchingStrategy;
    }

    public void addFoundFinalState(ProgramState state) {

        Iterator<ProgramState> iterator = targetStates.iterator();
        while(iterator.hasNext()) {
            ProgramState target = iterator.next();
            if(matchingStrategy.matches(target, state)) {
                iterator.remove();
                break;
            }
        }
    }

    @Override
    public boolean check(ProgramState state, StateSpace stateSpace) {

        if(targetStates.isEmpty()) {
            return false;
        }

        return true;
    }
}
