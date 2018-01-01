package de.rwth.i2.attestor.counterexampleGeneration;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

public interface StateMatchingStrategy {

    boolean matches(ProgramState state, ProgramState otherState);
}
