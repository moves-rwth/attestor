package de.rwth.i2.attestor.ipa.methodExecution;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.ipa.methods.MethodExecutor;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

import java.util.Collection;
import java.util.LinkedHashSet;

public abstract class AbstractMethodExecutor implements MethodExecutor {

    private ScopeExtractor scopeExtractor;
    private ContractCollection contractCollection;
    private ContractGenerator contractGenerator;

    public AbstractMethodExecutor(ScopeExtractor scopeExtractor, ContractCollection contractCollection,
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
    public Collection<ProgramState> getResultStates(ProgramState input, ProgramState callingState) {

        HeapConfiguration inputHeap = input.getHeap();
        ScopedHeap scopedHeap = scopeExtractor.extractScope(inputHeap);
        Collection<HeapConfiguration> postconditions = getPostconditions(input, scopedHeap, callingState);
        return createResultStates(input, postconditions);
    }

    protected Collection<ProgramState> createResultStates(ProgramState input,
                                                        Collection<HeapConfiguration> postconditions) {

        Collection<ProgramState> result = new LinkedHashSet<>();
        for(HeapConfiguration outputHeap : postconditions) {
            ProgramState resultState = input.shallowCopyWithUpdateHeap(outputHeap);
            resultState.setProgramCounter(0);
            result.add(resultState);
        }
        return result;
    }

    protected abstract Collection<HeapConfiguration> getPostconditions(ProgramState inputState,
                                                                       ScopedHeap scopedHeap,
                                                                       ProgramState callingState);

    protected ContractMatch computeNewContract(ProgramState input, ScopedHeap scopedHeap) {

        HeapConfiguration heapInScope = scopedHeap.getHeapInScope();
        ProgramState initialState = input.shallowCopyWithUpdateHeap(heapInScope);
        Contract generatedContract = contractGenerator.generateContract(initialState);
        contractCollection.addContract(generatedContract);
        return contractCollection.matchContract(heapInScope);
    }

}
