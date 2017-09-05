package de.rwth.i2.attestor.refinement;

import java.util.Collections;
import java.util.Set;

public class ErrorHeapAutomatonState extends HeapAutomatonState {

    public final static ErrorHeapAutomatonState instance = new ErrorHeapAutomatonState();

    @Override
    public Set<String> toAtomicPropositions() {

        return Collections.emptySet();
    }

    @Override
    public boolean equals(Object otherObject) {

        if(otherObject == null) {
            return false;
        }

        return otherObject.getClass() == ErrorHeapAutomatonState.class;
    }

    @Override
    public int hashCode() {

        return 0;
    }

    @Override
    public String toString() {

        return "sink";
    }
}
