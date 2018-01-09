package de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateExplorationStrategy;

import java.util.LinkedList;

public class TraceBasedStateExplorationStrategy implements StateExplorationStrategy {

    private LinkedList<ProgramState> unexploredStates = new LinkedList<>();

    private final CounterexampleTrace trace;
    private final StateSubsumptionStrategy stateSubsumptionStrategy;
    private ProgramState current = null;

    public TraceBasedStateExplorationStrategy(CounterexampleTrace trace, StateSubsumptionStrategy stateSubsumptionStrategy) {

        this.trace = trace;
        this.stateSubsumptionStrategy = stateSubsumptionStrategy;
    }

    @Override
    public boolean hasUnexploredStates() {

        return !unexploredStates.isEmpty();
    }

    @Override
    public ProgramState getNextUnexploredState() {

        return unexploredStates.removeFirst();
    }

    @Override
    public void addUnexploredState(ProgramState state, boolean isMaterializedState) {

        if(checkTrace(state, isMaterializedState)) {
            unexploredStates.addLast(state);
        }
    }

    private boolean checkTrace(ProgramState state, boolean isMaterializedState) {

        if(isMaterializedState) {
            return true;
        }

        if(current == null) {
            if(trace.hasNext()) {
                current = trace.next();
            } else {
                return false;
            }
        }

        if(stateSubsumptionStrategy.subsumes(state, current)) {
            current = null; // force to move to next state next time
            return true;
        }

        if(state.getProgramCounter() == -1) {

            return stateSubsumptionStrategy.subsumes(state, current);
        }

        return false;
    }
}
