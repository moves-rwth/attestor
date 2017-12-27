package de.rwth.i2.attestor.ipa.methods;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

public interface ContractGenerator {

    Contract generateContract(ProgramState initialState);
}
