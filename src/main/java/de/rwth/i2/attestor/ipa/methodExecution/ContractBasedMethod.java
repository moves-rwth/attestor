package de.rwth.i2.attestor.ipa.methodExecution;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.ipa.methods.MethodExecutor;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

import java.util.Collection;
import java.util.LinkedHashSet;

public class ContractBasedMethod implements MethodExecutor {

    private ScopeExtractor scopeExtractor;
    private ContractCollection contractCollection;
    private ContractGenerator contractGenerator;

    public ContractBasedMethod(ScopeExtractor scopeExtractor, ContractCollection contractCollection,
                                  ContractGenerator contractGenerator) {

        this.scopeExtractor = scopeExtractor;
        this.contractCollection = contractCollection;
        this.contractGenerator = contractGenerator;
    }

    public ScopeExtractor getScopeExtractor() {
        return scopeExtractor;
    }

    public ContractCollection getContractCollection() {
        return contractCollection;
    }

    public ContractGenerator getContractGenerator() {
        return contractGenerator;
    }

    @Override
    public Collection<ProgramState> getResultStates(ProgramState input) {

        HeapConfiguration inputHeap = input.getHeap();
        ScopedHeap scopedHeap = scopeExtractor.extractScope(inputHeap);
        Collection<HeapConfiguration> postconditions = getPostconditions(input, scopedHeap);
        return createResultStates(input, postconditions);
    }

    private Collection<ProgramState> createResultStates(ProgramState input,
                                                        Collection<HeapConfiguration> postconditions) {

        Collection<ProgramState> result = new LinkedHashSet<>();
        for(HeapConfiguration outputHeap : postconditions) {
            ProgramState resultState = input.shallowCopyWithUpdateHeap(outputHeap);
            resultState.setProgramCounter(0);
            result.add(resultState);
        }
        return result;
    }

    private Collection<HeapConfiguration> getPostconditions(ProgramState input,
                                                            ScopedHeap scopedHeap) {

        ContractMatch contractMatch = contractCollection.matchContract(scopedHeap.getHeapInScope());
        if(!contractMatch.hasMatch()) {
            contractMatch = computeNewContract(input, scopedHeap);
        }
        return scopedHeap.merge(contractMatch);
    }

    private ContractMatch computeNewContract(ProgramState input, ScopedHeap scopedHeap) {

        HeapConfiguration heapInScope = scopedHeap.getHeapInScope();
        ProgramState initialState = input.shallowCopyWithUpdateHeap(heapInScope);
        Contract generatedContract = contractGenerator.generateContract(initialState);
        contractCollection.addContract(generatedContract);
        return contractCollection.matchContract(heapInScope);
    }
}
