package de.rwth.i2.attestor.procedures;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.Collection;

public interface ContractMatch {

    ContractMatch NO_CONTRACT_MATCH = NoContractMatch.NO_CONTRACT_MATCH;

    boolean hasMatch();
    int[] getExternalReordering();
    Collection<HeapConfiguration> getPostconditions();
}