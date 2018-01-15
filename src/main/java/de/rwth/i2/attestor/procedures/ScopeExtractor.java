package de.rwth.i2.attestor.procedures;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

public interface ScopeExtractor {

   ScopedHeap extractScope(HeapConfiguration heapConfiguration);
}
