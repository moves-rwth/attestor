package de.rwth.i2.attestor.procedures;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.Collection;

public interface ScopedHeap {

    HeapConfiguration getHeapInScope();
    HeapConfiguration getHeapOutsideScope();

    Collection<HeapConfiguration> merge(ContractMatch contractMatch);

}
