package de.rwth.i2.attestor.ipa.contractGeneration;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

import java.util.Collection;

public interface MethodExecutor {

    Collection<ProgramState> execute(ProgramState initialState);
}
