package de.rwth.i2.attestor.stateSpaceGeneration.impl;

import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

public class NoCanonicalizationStrategy implements CanonicalizationStrategy {

    @Override
    public HeapConfiguration canonicalize(HeapConfiguration heapConfiguration) {

        return heapConfiguration;
    }
}
