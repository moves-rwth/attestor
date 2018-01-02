package de.rwth.i2.attestor.markingGeneration;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.Collection;


public interface MarkingGenerator {

    Collection<HeapConfiguration> marked(HeapConfiguration initialHeap);
}
