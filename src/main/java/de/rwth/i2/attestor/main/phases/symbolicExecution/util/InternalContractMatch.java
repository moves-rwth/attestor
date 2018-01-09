package de.rwth.i2.attestor.main.phases.symbolicExecution.util;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.procedures.methodExecution.ContractMatch;

import java.util.Collection;

class InternalContractMatch implements ContractMatch {

    private int[] externalReordering;
    private Collection<HeapConfiguration> postconditions;

    public InternalContractMatch(int[] externalReordering, Collection<HeapConfiguration> postconditions) {

        this.externalReordering = externalReordering;
        this.postconditions = postconditions;
    }

    @Override
    public boolean hasMatch() {
        return true;
    }

    @Override
    public int[] getExternalReordering() {

        return externalReordering;
    }

    @Override
    public Collection<HeapConfiguration> getPostconditions() {

        return postconditions;
    }
}
