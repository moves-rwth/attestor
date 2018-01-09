package de.rwth.i2.attestor.procedures.methodExecution;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

import java.util.Collection;

public class NonRecursiveMethodExecutor extends AbstractMethodExecutor {

    private ContractGenerator contractGenerator;

    public NonRecursiveMethodExecutor(ScopeExtractor scopeExtractor, ContractCollection contractCollection,
                                      ContractGenerator contractGenerator) {

        super(scopeExtractor, contractCollection);
        this.contractGenerator = contractGenerator;
    }

    @Override
    protected Collection<HeapConfiguration> getPostconditions(ProgramState callingState, ProgramState input,
                                                            ScopedHeap scopedHeap) {

        ContractMatch contractMatch = getContractCollection().matchContract(scopedHeap.getHeapInScope());
        if(!contractMatch.hasMatch()) {
            contractMatch = computeNewContract(input, scopedHeap);
        }
        return scopedHeap.merge(contractMatch);
    }

    private ContractMatch computeNewContract(ProgramState input, ScopedHeap scopedHeap) {

        HeapConfiguration heapInScope = scopedHeap.getHeapInScope();
        ProgramState initialState = input.shallowCopyWithUpdateHeap(heapInScope);
        Contract generatedContract = contractGenerator.generateContract(initialState);
        ContractCollection contractCollection = getContractCollection();
        contractCollection.addContract(generatedContract);
        return contractCollection.matchContract(heapInScope);
    }
}
