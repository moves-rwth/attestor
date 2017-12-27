package de.rwth.i2.attestor.ipa.methods;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

import java.util.Collection;

public interface MethodExecutor {

    Collection<ProgramState> getResultStates(ProgramState input);
}
