package de.rwth.i2.attestor.stateSpaceGeneration;

@FunctionalInterface
public interface StateSpaceSupplier {

    StateSpace get();
}
