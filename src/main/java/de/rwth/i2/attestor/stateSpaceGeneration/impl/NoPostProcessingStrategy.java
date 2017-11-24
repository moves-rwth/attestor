package de.rwth.i2.attestor.stateSpaceGeneration.impl;

import de.rwth.i2.attestor.stateSpaceGeneration.PostProcessingStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerator;

public class NoPostProcessingStrategy implements PostProcessingStrategy {
    @Override
    public void process(StateSpace originalStateSpace) {
    }
}
