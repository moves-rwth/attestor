package de.rwth.i2.attestor.procedures.methodExecution;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

public interface PreconditionMatchingStrategy {

    ContractMatch match(Contract contract, HeapConfiguration heapInScope);
}
