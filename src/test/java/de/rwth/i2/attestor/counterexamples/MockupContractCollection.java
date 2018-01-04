package de.rwth.i2.attestor.counterexamples;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.procedures.methodExecution.Contract;
import de.rwth.i2.attestor.procedures.methodExecution.ContractCollection;
import de.rwth.i2.attestor.procedures.methodExecution.ContractMatch;

import java.util.ArrayList;
import java.util.Collection;

public class MockupContractCollection implements ContractCollection {

    private HeapConfiguration precondition;
    private Collection<HeapConfiguration> postconditions = new ArrayList<>();

    public  MockupContractCollection(MockupHeaps mockupHeaps) {

        this.precondition = mockupHeaps.getHeapInScope();
        this.postconditions.add(mockupHeaps.getPostcondition());
    }

    @Override
    public void addContract(Contract contract) {
    }

    @Override
    public ContractMatch matchContract(HeapConfiguration precondition) {

        boolean matched = this.precondition.equals(precondition);

            return new ContractMatch() {
                @Override
                public boolean hasMatch() {
                    return matched;
                }

                @Override
                public int[] getExternalReordering() {
                    if(matched) {
                        int[] result = new int[precondition.countExternalNodes()];
                        for (int i = 0; i < result.length; i++) {
                            result[i] = i;
                        }
                        return result;
                    } else {
                        throw new IllegalStateException();
                    }
                }

                @Override
                public Collection<HeapConfiguration> getPostconditions() {
                    if(matched) {
                        return postconditions;
                    } else {
                        throw new IllegalStateException();
                    }
                }
            };
    }
}
