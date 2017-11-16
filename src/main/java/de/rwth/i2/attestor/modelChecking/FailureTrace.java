package de.rwth.i2.attestor.modelChecking;

import de.rwth.i2.attestor.counterexampleGeneration.Trace;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class FailureTrace implements Trace {

    private final LinkedList<Integer> stateIdTrace = new LinkedList<>();
    private final LinkedList<ProgramState> stateTrace = new LinkedList<>();

    public FailureTrace() {

    }

    FailureTrace(Assertion failureAssertion, StateSpace stateSpace) {

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

    @Override
    public ProgramState getInitialState() {
        return stateTrace.getFirst();
    }

    @Override
    public ProgramState getFinalState() {
        return stateTrace.getLast();
    }

    @Override
    public int size() {
        return stateTrace.size();
    }

    @Override
    public ProgramState getSuccessor(ProgramState state) {
        Iterator<ProgramState> iterator = stateTrace.iterator();
        while(iterator.hasNext()) {
           ProgramState s = iterator.next();
           if(s.getStateSpaceId() == state.getStateSpaceId()) {
               if(iterator.hasNext()) {
                   return iterator.next();
               }
               return null;
           }
        }
        return null;
    }

    @Override
    public boolean containsSubsumingState(ProgramState state) {
        for(ProgramState s : stateTrace) {
            if(state.isSubsumedBy(s)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return stateIdTrace.isEmpty();
    }

    @Override
    public Iterator<ProgramState> iterator() {
        return stateTrace.iterator();
    }

    public String toString() {

        return stateIdTrace.toString();
    }


}
