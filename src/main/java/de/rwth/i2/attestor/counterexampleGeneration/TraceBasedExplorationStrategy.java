package de.rwth.i2.attestor.counterexampleGeneration;

import de.rwth.i2.attestor.stateSpaceGeneration.ExplorationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;

public class TraceBasedExplorationStrategy implements ExplorationStrategy {

    private Trace trace;
    private ProgramState currentStateInTrace;
    private StateMatchingStrategy matchingStrategy;

    public TraceBasedExplorationStrategy(Trace trace, StateMatchingStrategy matchingStrategy) {

        this.trace = trace;
        this.matchingStrategy = matchingStrategy;
        this.currentStateInTrace = trace.getInitialState();
    }

    @Override
    public boolean check(ProgramState state, StateSpace stateSpace) {

        if(currentStateInTrace == null || state == null) {
            return false;
        }

        if(!state.isFromTopLevelStateSpace()) {
            return true;
        }

        if(matchingStrategy.matches(currentStateInTrace, state)) {
            currentStateInTrace = trace.getSuccessor(currentStateInTrace);
            return true;
        }

        return false;
    }
}
