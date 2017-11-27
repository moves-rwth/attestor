package de.rwth.i2.attestor.stateSpaceGeneration.impl;

import de.rwth.i2.attestor.stateSpaceGeneration.CanonicalizationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

public class NoCanonicalizationStrategy implements CanonicalizationStrategy {

    @Override
    public ProgramState canonicalize(ProgramState state) {
        return state;
    }
}
