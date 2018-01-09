package de.rwth.i2.attestor.procedures;

import de.rwth.i2.attestor.procedures.methodExecution.Contract;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

import java.util.Collection;

public interface MethodExecutor {

    void addContract(Contract contract);

    Collection<ProgramState> getResultStates(ProgramState callingState, ProgramState input);
}
