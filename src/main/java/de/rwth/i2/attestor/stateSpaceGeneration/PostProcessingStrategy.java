package de.rwth.i2.attestor.stateSpaceGeneration;

@FunctionalInterface
public interface PostProcessingStrategy {

    void process(StateSpace originalStateSpace);
}
