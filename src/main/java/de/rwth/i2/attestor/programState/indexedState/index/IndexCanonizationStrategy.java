package de.rwth.i2.attestor.programState.indexedState.index;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

public interface IndexCanonizationStrategy {

    void canonizeIndex(HeapConfiguration hc);
}
