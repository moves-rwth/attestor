package de.rwth.i2.attestor.stateSpaceGeneration.impl;

import de.rwth.i2.attestor.stateSpaceGeneration.ExplorationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

public class ExploreAllStrategy implements ExplorationStrategy {
    @Override
    public boolean check(ProgramState state, boolean isMaterializedState) {
        return true;
    }
}
