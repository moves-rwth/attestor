package de.rwth.i2.attestor.ipa.methodExecution;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

import java.util.Collection;

public class ContractBasedMethod extends AbstractMethodExecutor {


    public ContractBasedMethod(ScopeExtractor scopeExtractor, ContractCollection contractCollection,
                                  ContractGenerator contractGenerator) {

        super(scopeExtractor, contractCollection, contractGenerator);
    }

    @Override
    protected Collection<HeapConfiguration> getPostconditions(ProgramState input,
                                                            ScopedHeap scopedHeap, ProgramState callingState) {

        ContractMatch contractMatch = getContractCollection().matchContract(scopedHeap.getHeapInScope());
        if(!contractMatch.hasMatch()) {
            contractMatch = computeNewContract(input, scopedHeap);
        }

        // TODO deal with recursion, see AbstractMethod

        return scopedHeap.merge(contractMatch);
    }
}
