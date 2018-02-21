package de.rwth.i2.attestor.procedures;

import java.util.Collection;
import java.util.LinkedHashSet;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

public abstract class AbstractMethodExecutor implements MethodExecutor {

    private ScopeExtractor scopeExtractor;
    private ContractCollection contractCollection;

    public AbstractMethodExecutor(ScopeExtractor scopeExtractor, ContractCollection contractCollection) {

        this.scopeExtractor = scopeExtractor;
        this.contractCollection = contractCollection;
    }

    public ScopeExtractor getScopeExtractor() {
        return scopeExtractor;
    }

    public ContractCollection getContractCollection() {
        return contractCollection;
    }

    @Override
    public Collection<ProgramState> getResultStates(ProgramState callingState, ProgramState input) {

        HeapConfiguration inputHeap = input.getHeap();
        ScopedHeap scopedHeap = scopeExtractor.extractScope(inputHeap);
        Collection<HeapConfiguration> postconditions = getPostconditions(callingState, scopedHeap);
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

    protected abstract Collection<HeapConfiguration> getPostconditions(ProgramState callingState,
                                                                       ScopedHeap scopedHeap);

    @Override
    public void addContract(Contract contract) {

        contractCollection.addContract(contract);
    }
    
    @Override
    public Collection<Contract> getContractsForExport() {
    	return contractCollection.getContractsForExport();
    }

}
