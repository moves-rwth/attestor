package de.rwth.i2.attestor.counterexamples;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

@FunctionalInterface
public interface StateSubsumptionStrategy {

    boolean subsumes(ProgramState subsumed, ProgramState subsuming);
}
