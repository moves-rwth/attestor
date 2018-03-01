package de.rwth.i2.attestor.procedures;

import java.util.Collection;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

public interface ContractMatch {

    ContractMatch NO_CONTRACT_MATCH = NoContractMatch.NO_CONTRACT_MATCH;

    boolean hasMatch();
    int[] getExternalReordering();
    HeapConfiguration getPrecondition();
    Collection<HeapConfiguration> getPostconditions();
}
