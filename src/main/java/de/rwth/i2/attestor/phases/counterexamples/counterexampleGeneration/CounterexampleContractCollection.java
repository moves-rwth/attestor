package de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.procedures.Contract;
import de.rwth.i2.attestor.procedures.ContractCollection;
import de.rwth.i2.attestor.procedures.ContractMatch;

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
}
