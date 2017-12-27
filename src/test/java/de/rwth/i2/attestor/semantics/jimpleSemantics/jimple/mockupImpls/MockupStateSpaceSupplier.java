package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls;

import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceSupplier;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.InternalStateSpace;

public class MockupStateSpaceSupplier implements StateSpaceSupplier {
    @Override
    public StateSpace get() {
        return new InternalStateSpace(1000);
    }
}
