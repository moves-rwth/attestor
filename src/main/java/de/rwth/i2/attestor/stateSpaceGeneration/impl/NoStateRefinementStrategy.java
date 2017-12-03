package de.rwth.i2.attestor.stateSpaceGeneration.impl;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.Semantics;
import de.rwth.i2.attestor.stateSpaceGeneration.StateRefinementStrategy;

public class NoStateRefinementStrategy implements StateRefinementStrategy {

    @Override
    public ProgramState refine(Semantics semantics, ProgramState state) {

        return state;
    }
}
