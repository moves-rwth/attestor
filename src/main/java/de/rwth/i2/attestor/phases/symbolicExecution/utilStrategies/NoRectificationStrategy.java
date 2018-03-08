package de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateRectificationStrategy;

import java.util.Collection;
import java.util.Collections;

public class NoRectificationStrategy implements StateRectificationStrategy {
    @Override
    public Collection<ProgramState> rectify(ProgramState state) {
        return Collections.singleton(state);
    }
}
