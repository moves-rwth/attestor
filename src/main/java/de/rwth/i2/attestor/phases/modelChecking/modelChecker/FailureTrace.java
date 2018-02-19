package de.rwth.i2.attestor.phases.modelChecking.modelChecker;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.rwth.i2.attestor.phases.symbolicExecution.stateSpaceGenerationImpl.InternalStateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;

public class FailureTrace implements ModelCheckingTrace {

    private final LinkedList<Integer> stateIdTrace = new LinkedList<>();
    private final LinkedList<ProgramState> stateTrace = new LinkedList<>();
    private Iterator<ProgramState> iterator;

    FailureTrace(Assertion failureAssertion, StateSpace stateSpace) {

        Assertion current = failureAssertion;

        do {
            int stateId = current.getProgramState();
            stateIdTrace.addFirst(stateId);
            ProgramState state = stateSpace.getState(stateId);
            stateTrace.addFirst(state);
            current = current.getParent();
        } while (current != null);

        iterator = stateTrace.iterator();
    }

    @Override
    public List<Integer> getStateIdTrace() {

        return new LinkedList<>(stateIdTrace);
    }


    @Override
    public ProgramState getInitialState() {

        return stateTrace.getFirst();
    }

    @Override
    public ProgramState getFinalState() {

        return stateTrace.getLast();
    }

    public boolean isEmpty() {

        return stateIdTrace.isEmpty();
    }

    public String toString() {

        return stateIdTrace.toString();
    }


    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public ProgramState next() {
        return iterator.next();
    }

    @Override
    public StateSpace getStateSpace() {


        StateSpace stateSpace = new InternalStateSpace(stateTrace.size());

        if(stateTrace.isEmpty()) {
            return stateSpace;
        }

        Iterator<ProgramState> iterator = stateTrace.iterator();

        ProgramState current = iterator.next();
        stateSpace.addInitialState(current);

        while(iterator.hasNext()) {
            ProgramState next = iterator.next();
            stateSpace.addState(next);
            stateSpace.addControlFlowTransition(current, next);
            current = next;
        }

        stateSpace.setFinal(stateTrace.getLast());
        return stateSpace;
    }
}
