package de.rwth.i2.attestor.procedures;

import java.util.Collection;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

public interface MethodExecutor {

    void addContract(Contract contract);

    Collection<ProgramState> getResultStates(ProgramState callingState, ProgramState input);

	Collection<Contract> getContractsForExport();
}
