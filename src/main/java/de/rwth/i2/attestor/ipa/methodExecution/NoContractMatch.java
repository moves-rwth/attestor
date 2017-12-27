package de.rwth.i2.attestor.ipa.methodExecution;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.Collection;

public final class NoContractMatch implements ContractMatch {

    static final NoContractMatch NO_CONTRACT_MATCH = new NoContractMatch();

    private NoContractMatch() {}

    @Override
    public boolean hasMatch() {
        return false;
    }

    @Override
    public int[] getExternalReordering() {
        return new int[0];
    }

    @Override
    public Collection<HeapConfiguration> getPostconditions() {
        return null;
    }
}
