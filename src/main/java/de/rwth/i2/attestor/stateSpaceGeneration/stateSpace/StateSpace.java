package de.rwth.i2.attestor.stateSpaceGeneration.stateSpace;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

import java.util.Set;

public interface StateSpace {

    boolean contains(ProgramState state);

    Set<ProgramState> getStates();
    Set<ProgramState> getInitialStates();
    Set<ProgramState> getFinalStates();

    Set<ProgramState> getControlFlowSuccessorsOf(ProgramState state);
    Set<ProgramState> getMaterializationSuccessorsOf(ProgramState state);

    void addState(ProgramState state);
    void addInitialState(ProgramState state);
    void setFinal(ProgramState state);

    void addMaterializationTransition(ProgramState from, ProgramState to);
    void addControlFlowTransition(ProgramState from, ProgramState to);

    int getMaximalStateSize();
}
