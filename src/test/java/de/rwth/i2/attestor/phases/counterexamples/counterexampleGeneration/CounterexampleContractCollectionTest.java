package de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.procedures.methodExecution.Contract;
import de.rwth.i2.attestor.procedures.methodExecution.ContractCollection;
import de.rwth.i2.attestor.procedures.methodExecution.ContractMatch;
import org.junit.Test;

import java.util.Collection;

import static fj.data.Validation.fail;
import static org.junit.Assert.assertEquals;

public class CounterexampleContractCollectionTest {

    @Test
    public void testSimple() {

        ContractCollection mockup = new ContractCollection() {

            @Override
            public void addContract(Contract contract) {
                fail("Should not be called");
            }

            @Override
            public ContractMatch matchContract(HeapConfiguration precondition) {
                return new ContractMatch() {
                    @Override
                    public boolean hasMatch() {
                        return true;
                    }

                    @Override
                    public int[] getExternalReordering() {
                        int[] result = new int[1];
                        result[0] = 23; // marks that this has been called
                        return result;
                    }

                    @Override
                    public Collection<HeapConfiguration> getPostconditions() {
                        return null;
                    }
                };
            }
        };

        CounterexampleContractCollection collection = new CounterexampleContractCollection(mockup);
        collection.addContract(new Contract() {
            @Override
            public void addPostconditions(Collection<HeapConfiguration> postconditions) {

            }

            @Override
            public HeapConfiguration getPrecondition() {
                return null;
            }

            @Override
            public Collection<HeapConfiguration> getPostconditions() {
                return null;
            }
        });
        // should not fail

        ContractMatch match = collection.matchContract(null);
        assertEquals(23, match.getExternalReordering()[0]); // checks delegation to mockup
    }
}
