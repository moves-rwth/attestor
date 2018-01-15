package de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

@FunctionalInterface
public interface StateSubsumptionStrategy {

    boolean subsumes(ProgramState subsumed, ProgramState subsuming);
}
