package de.rwth.i2.attestor.counterexampleGeneration;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

import java.util.Iterator;

/**
 * Interface defining a trace within a state space that is used to define actual program states that cause a
 * program to violate an LTL property.
 *
 * @author Christoph
 */
public interface Trace {

    ProgramState getInitialState();
    ProgramState getFinalState();
    int size();
    ProgramState getSuccessor(ProgramState state);
    boolean contains(ProgramState state);
    boolean isEmpty();
    Iterator<ProgramState> iterator();
}
