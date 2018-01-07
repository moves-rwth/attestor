package de.rwth.i2.attestor.interprocedural;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.procedures.contracts.InternalContract;
import de.rwth.i2.attestor.procedures.methodExecution.*;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

import java.util.Collection;
import java.util.LinkedHashSet;

public class RecursiveMethodExecutor extends AbstractMethodExecutor {

    private final Method method;
    private ProcedureRegistry procedureRegistry;

    public RecursiveMethodExecutor(Method method, ScopeExtractor scopeExtractor, ContractCollection contractCollection,
                                   ProcedureRegistry procedureRegistry) {

        super(scopeExtractor, contractCollection);
        this.method = method;
        this.procedureRegistry = procedureRegistry;
    }

    @Override
    protected Collection<HeapConfiguration> getPostconditions(ProgramState inputState, ScopedHeap scopedHeap) {

        HeapConfiguration heapInScope = scopedHeap.getHeapInScope();
        ProgramState preconditionState = inputState.shallowCopyWithUpdateHeap(heapInScope);
        ContractMatch contractMatch = getContractCollection().matchContract(heapInScope);
        if(!contractMatch.hasMatch()) {
            procedureRegistry.registerProcedure(method, preconditionState);
            Collection<HeapConfiguration> postconditions = new LinkedHashSet<>();
            ContractCollection contractCollection = getContractCollection();
            contractCollection.addContract(new InternalContract(heapInScope, postconditions));
            contractMatch = contractCollection.matchContract(heapInScope);
        }
        procedureRegistry.registerDependency(inputState, method, preconditionState);
        return scopedHeap.merge(contractMatch);
    }

}
