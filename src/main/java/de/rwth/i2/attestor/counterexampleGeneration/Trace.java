package de.rwth.i2.attestor.counterexampleGeneration;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

import java.util.Iterator;
import java.util.List;

public interface Trace {

    ProgramState getInitialState();
    ProgramState getFinalState();
    int size();
    ProgramState getSuccessor(ProgramState state);
    boolean contains(ProgramState state);
    boolean isEmpty();
    Iterator<ProgramState> iterator();
}
