package de.rwth.i2.attestor.ipa.methodExecution;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

public interface ScopeExtractor {

   ScopedHeap extractScope(HeapConfiguration heapConfiguration);
}
