package de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration;

import java.util.Collection;
import java.util.Collections;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.procedures.*;

public class CounterexampleContractCollection implements ContractCollection {

    private ContractCollection delegate;

    public CounterexampleContractCollection(ContractCollection delegate) {

        this.delegate = delegate;
    }

    @Override
    public void addContract(Contract contract) {
        // do nothing
    }

    @Override
    public ContractMatch matchContract(HeapConfiguration precondition) {
        return delegate.matchContract(precondition);
    }

	@Override
	public Collection<Contract> getContractsForExport() {
		return Collections.emptyList();
	}
}
