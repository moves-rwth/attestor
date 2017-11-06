package de.rwth.i2.attestor.stateSpaceGeneration;

public interface ExplorationStrategy {

    boolean check(ProgramState state);
}
