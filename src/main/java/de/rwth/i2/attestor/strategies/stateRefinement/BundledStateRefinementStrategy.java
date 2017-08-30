package de.rwth.i2.attestor.strategies.stateRefinement;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateRefinementStrategy;

import java.util.List;

public class BundledStateRefinementStrategy implements StateRefinementStrategy {

    private List<StateRefinementStrategy> strategies;

    public BundledStateRefinementStrategy(List<StateRefinementStrategy> strategies) {

        this.strategies = strategies;
    }

    @Override
    public ProgramState refine(ProgramState state) {

        strategies.forEach(f -> f.refine(state));
        return state;
    }
}
