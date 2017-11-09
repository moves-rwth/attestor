package de.rwth.i2.attestor.strategies;

import de.rwth.i2.attestor.stateSpaceGeneration.CanonicalizationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.Semantics;

public class NoCanonicalizationStrategy implements CanonicalizationStrategy {
    @Override
    public ProgramState canonicalize(Semantics semantics, ProgramState state) {
        return state;
    }

    @Override
    public ProgramState canonicalize(ProgramState state) {
        return state;
    }
}
