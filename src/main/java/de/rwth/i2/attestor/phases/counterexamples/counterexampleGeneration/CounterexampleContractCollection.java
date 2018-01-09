package de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.procedures.methodExecution.Contract;
import de.rwth.i2.attestor.procedures.methodExecution.ContractCollection;
import de.rwth.i2.attestor.procedures.methodExecution.ContractMatch;

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
