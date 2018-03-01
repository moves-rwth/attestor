package de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl;

import java.util.Collection;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.procedures.ContractMatch;

class InternalContractMatch implements ContractMatch {

    private int[] externalReordering;
    private Collection<HeapConfiguration> postconditions;
	private HeapConfiguration matchedPrecondition;

    public InternalContractMatch( int[] externalReordering,
    							  HeapConfiguration matchedPrecondition,
    							  Collection<HeapConfiguration> postconditions ) {

        this.externalReordering = externalReordering;
        this.matchedPrecondition = matchedPrecondition;
        
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

	@Override
	public HeapConfiguration getPrecondition() {
		return this.matchedPrecondition;
	}
}
