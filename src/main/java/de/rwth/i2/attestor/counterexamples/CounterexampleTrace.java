package de.rwth.i2.attestor.counterexamples;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

import java.util.Iterator;

public interface CounterexampleTrace extends Iterator<ProgramState> {

    ProgramState getInitialState();
}
