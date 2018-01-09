package de.rwth.i2.attestor.procedures;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

public interface ContractCollection {

    void addContract(Contract contract);

    ContractMatch matchContract(HeapConfiguration precondition);
}
