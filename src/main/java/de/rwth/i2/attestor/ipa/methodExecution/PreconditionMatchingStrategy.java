package de.rwth.i2.attestor.ipa.methodExecution;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

public interface PreconditionMatchingStrategy {

    ContractMatch match(Contract contract, HeapConfiguration heapInScope);
}
