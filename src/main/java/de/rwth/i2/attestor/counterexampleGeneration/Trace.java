package de.rwth.i2.attestor.counterexampleGeneration;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

import java.util.List;

public interface Trace {

    List<ProgramState> getTrace();
}
