package de.rwth.i2.attestor.stateSpaceGeneration;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;

import java.util.Map;
import java.util.Set;

public interface StateSpace {

    Set<ProgramState> getStates();

    Set<ProgramState> getInitialStates();

    TIntSet getInitialStateIds();

    Set<ProgramState> getFinalStates();

    TIntSet getFinalStateIds();

    int size();

    Set<ProgramState> getControlFlowSuccessorsOf(ProgramState state);

    Set<ProgramState> getMaterializationSuccessorsOf(ProgramState state);

    Set<ProgramState> getArtificialInfPathsSuccessorsOf(ProgramState state);

    TIntArrayList getControlFlowSuccessorsIdsOf(int stateSpaceId);

    TIntArrayList getMaterializationSuccessorsIdsOf(int stateSpaceId);

    TIntArrayList getArtificialInfPathsSuccessorsIdsOf(int stateSpaceId);

    boolean addState(ProgramState state);

    boolean addStateIfAbsent(ProgramState state);

    void addInitialState(ProgramState state);

    void setFinal(ProgramState state);

    void setAborted(ProgramState state);

    boolean containsAbortedStates();

    void updateFinalStates(Set<ProgramState> newFinalStates, Map<Integer, Integer> idMapping);

    void addMaterializationTransition(ProgramState from, ProgramState to);

    void addControlFlowTransition(ProgramState from, ProgramState to);

    void addArtificialInfPathsTransition(ProgramState cur);

    ProgramState getState(int id);

    int getMaximalStateSize();

    boolean satisfiesAP(int stateId, String expectedAP);


}
