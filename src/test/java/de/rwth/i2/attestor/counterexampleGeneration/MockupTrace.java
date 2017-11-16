package de.rwth.i2.attestor.counterexampleGeneration;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MockupTrace implements Trace {

    private List<ProgramState> states = new ArrayList<>();

    MockupTrace addState(ProgramState state) {
        states.add(state);
        return this;
    }

    @Override
    public ProgramState getInitialState() {
        return states.get(0);
    }

    @Override
    public ProgramState getFinalState() {
        return states.get(states.size()-1);
    }

    @Override
    public int size() {
        return states.size();
    }

    @Override
    public ProgramState getSuccessor(ProgramState state) {

       int index = states.indexOf(state);
       if(index < size()) {
           return states.get(index+1);
       }
       return null;
    }

    @Override
    public boolean containsSubsumingState(ProgramState state) {
        return states.contains(state);
    }

    @Override
    public boolean isEmpty() {
        return states.isEmpty();
    }

    @Override
    public Iterator<ProgramState> iterator() {
        return states.iterator();
    }
}
