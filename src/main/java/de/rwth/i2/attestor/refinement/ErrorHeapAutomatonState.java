package de.rwth.i2.attestor.refinement;

import java.util.Collections;
import java.util.Set;

public class ErrorHeapAutomatonState extends HeapAutomatonState {

    @Override
    public Set<String> toAtomicPropositions() {

        return Collections.emptySet();
    }

    @Override
    public boolean equals(Object otherObject) {

        return otherObject == this;
    }

    @Override
    public int hashCode() {

        return 0;
    }
}
