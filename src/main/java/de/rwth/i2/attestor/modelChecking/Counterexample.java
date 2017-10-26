package de.rwth.i2.attestor.modelChecking;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;

import java.util.LinkedList;
import java.util.List;

public class Counterexample {

    private final LinkedList<Integer> stateIdTrace = new LinkedList<>();
    private final LinkedList<ProgramState> stateTrace = new LinkedList<>();

    public Counterexample() {

    }

    public Counterexample(Assertion failureAssertion, StateSpace stateSpace) {

        Assertion current = failureAssertion;
        do {
            int stateId = current.getProgramState();
            stateIdTrace.addFirst(stateId);
            stateTrace.addFirst(stateSpace.getState(stateId));
            current = current.getParent();
        } while(current != null);
    }

    public List<Integer> getStateIdTrace() {
        return new LinkedList<>(stateIdTrace);
    }

    public List<ProgramState> getTrace() {

        return new LinkedList<>(stateTrace);

    }

    public boolean isEmpty() {
        return stateIdTrace.isEmpty();
    }

    public String toString() {

        return stateIdTrace.toString();
    }


}
