package de.rwth.i2.attestor.stateSpaceGeneration.impl;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.CanonicalizationStrategy;

public class NoCanonicalizationStrategy implements CanonicalizationStrategy {

    @Override
    public HeapConfiguration canonicalize(HeapConfiguration heapConfiguration) {

        return heapConfiguration;
    }
}
