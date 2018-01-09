package de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MockupTrace implements CounterexampleTrace {

    private List<ProgramState> states = new ArrayList<>();
    private Iterator<ProgramState> iterator = null;

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
    public boolean hasNext() {

        if(iterator == null) {
            iterator = states.iterator();
        }
        return iterator.hasNext();

    }

    @Override
    public ProgramState next() {
        return iterator.next();
    }
}
