package de.rwth.i2.attestor.markings;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.Collection;

public interface MarkingGenerator {

    Collection<HeapConfiguration> markHeapConfiguration(HeapConfiguration input);
}
