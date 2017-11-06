package de.rwth.i2.attestor.stateSpaceGeneration;

@FunctionalInterface
public interface SemanticsOptionsSupplier {

    SemanticsOptions get(StateSpaceGenerator generator);
}
