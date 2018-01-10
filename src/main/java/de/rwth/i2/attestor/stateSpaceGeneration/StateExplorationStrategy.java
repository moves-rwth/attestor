package de.rwth.i2.attestor.stateSpaceGeneration;

public interface StateExplorationStrategy {

    boolean hasUnexploredStates();

    ProgramState getNextUnexploredState();

    void addUnexploredState(ProgramState state, boolean isMaterializedState);
}
