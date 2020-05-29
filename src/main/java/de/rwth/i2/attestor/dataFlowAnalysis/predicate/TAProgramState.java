package de.rwth.i2.attestor.dataFlowAnalysis.predicate;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.TAHeapConfiguration;

public class TAProgramState {
    public final boolean isMaterialized;
    public final TAHeapConfiguration heap;

    public TAProgramState(boolean isMaterialized, HeapConfiguration heap) {
        if (!(heap instanceof TAHeapConfiguration)) {
            throw new IllegalArgumentException("unsupported heap-configuration type");
        }

        this.isMaterialized = isMaterialized;
        this.heap = (TAHeapConfiguration) heap;
    }
}
