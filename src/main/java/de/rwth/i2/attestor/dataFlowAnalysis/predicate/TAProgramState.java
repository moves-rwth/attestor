package de.rwth.i2.attestor.dataFlowAnalysis.predicate;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.TAHeapConfiguration;

public class TAProgramState {
    final boolean isCritical;
    final boolean isMaterialized;
    final TAHeapConfiguration heap;

    public TAProgramState(boolean isCritical, boolean isMaterialized, HeapConfiguration heap) {
        if (!(heap instanceof TAHeapConfiguration)) {
            throw new IllegalArgumentException("unsupported heap-configuration type");
        }

        this.isCritical = isCritical;
        this.isMaterialized = isMaterialized;
        this.heap = (TAHeapConfiguration) heap;
    }
}
