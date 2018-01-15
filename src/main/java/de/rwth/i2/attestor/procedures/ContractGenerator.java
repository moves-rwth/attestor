package de.rwth.i2.attestor.procedures;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

public interface ContractGenerator {

    Contract generateContract(ProgramState initialState);
}
