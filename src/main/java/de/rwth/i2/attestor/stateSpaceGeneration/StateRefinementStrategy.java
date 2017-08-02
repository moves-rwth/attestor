package de.rwth.i2.attestor.stateSpaceGeneration;

public interface StateRefinementStrategy {

    ProgramState refine(ProgramState state);
}
