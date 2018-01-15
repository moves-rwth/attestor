package de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies;

import de.rwth.i2.attestor.stateSpaceGeneration.AbortStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;

public class NoAbortStrategy implements AbortStrategy {
    @Override
    public void checkAbort(StateSpace stateSpace) throws StateSpaceGenerationAbortedException {

    }
}
