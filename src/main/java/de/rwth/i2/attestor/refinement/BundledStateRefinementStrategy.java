package de.rwth.i2.attestor.refinement;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.Semantics;
import de.rwth.i2.attestor.stateSpaceGeneration.StateRefinementStrategy;

import java.util.List;

public class BundledStateRefinementStrategy implements StateRefinementStrategy {

    private final List<StateRefinementStrategy> strategies;

    public BundledStateRefinementStrategy(List<StateRefinementStrategy> strategies) {

        this.strategies = strategies;
    }

    @Override
    public ProgramState refine(Semantics semantics, ProgramState state) {

        strategies.forEach(f -> f.refine(semantics, state));
        return state;
    }
}
