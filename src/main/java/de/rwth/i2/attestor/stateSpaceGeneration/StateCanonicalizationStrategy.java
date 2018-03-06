package de.rwth.i2.attestor.stateSpaceGeneration;

import java.util.Collection;

public interface StateCanonicalizationStrategy {

    Collection<ProgramState> canonicalize(ProgramState state);
}
