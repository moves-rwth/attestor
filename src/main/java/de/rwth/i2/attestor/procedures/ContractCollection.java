package de.rwth.i2.attestor.procedures;

import java.util.Collection;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

public interface ContractCollection {

    void addContract(Contract contract);

    ContractMatch matchContract(HeapConfiguration precondition);
    
    Collection<Contract> getContractsForExport();
}
