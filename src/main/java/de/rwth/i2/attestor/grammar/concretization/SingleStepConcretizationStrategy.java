package de.rwth.i2.attestor.grammar.concretization;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.Iterator;

public interface SingleStepConcretizationStrategy {

    Iterator<HeapConfiguration> concretize(HeapConfiguration heapConfiguration, int edge);
}
