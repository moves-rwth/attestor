package de.rwth.i2.attestor.refinement;

import java.util.Set;

public abstract class HeapAutomatonState {

    public abstract Set<String> toAtomicPropositions();

    public abstract boolean isError();

    @Override
    public abstract boolean equals(Object otherObject);

    @Override
    public abstract int hashCode();
}
