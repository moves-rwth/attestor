package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

public interface TransformationStep {
    HeapConfiguration getRule();

    int getNtEdge();

    int match(int id);
}