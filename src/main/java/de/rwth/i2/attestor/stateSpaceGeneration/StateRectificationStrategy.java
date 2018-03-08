package de.rwth.i2.attestor.stateSpaceGeneration;

import java.util.Collection;

public interface StateRectificationStrategy {

    Collection<ProgramState> rectify(ProgramState state);
}
