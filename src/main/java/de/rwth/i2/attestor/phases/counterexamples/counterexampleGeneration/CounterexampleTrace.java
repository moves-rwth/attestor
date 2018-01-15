package de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

import java.util.Iterator;

public interface CounterexampleTrace extends Iterator<ProgramState> {

    ProgramState getInitialState();
    ProgramState getFinalState();

}
