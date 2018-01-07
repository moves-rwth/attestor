package de.rwth.i2.attestor.counterexamples;

import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.procedures.methodExecution.*;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

import java.util.Collection;

public class CounterexampleMethodExecutor extends AbstractMethodExecutor {

    private final CounterexampleContractGenerator counterexampleContractGenerator;
    private final CanonicalizationStrategy canonicalizationStrategy;

    CounterexampleMethodExecutor(ScopeExtractor scopeExtractor, ContractCollection contractCollection,
                                        CounterexampleContractGenerator contractGenerator,
                                        CanonicalizationStrategy canonicalizationStrategy) {

        super(scopeExtractor, contractCollection);
        this.counterexampleContractGenerator = contractGenerator;
        this.canonicalizationStrategy = canonicalizationStrategy;
    }

    @Override
    protected Collection<HeapConfiguration> getPostconditions(ProgramState callingState,
                                                              ProgramState inputState, ScopedHeap scopedHeap) {

        HeapConfiguration abstractedHeapInScope = canonicalizationStrategy.canonicalize(scopedHeap.getHeapInScope());
        ContractMatch abstractMatch = getContractCollection().matchContract(abstractedHeapInScope);
        if(!abstractMatch.hasMatch()) {
            throw new IllegalStateException("Could not match contract during counterexample generation.");
        }
        counterexampleContractGenerator.setRequiredFinalHeaps(abstractMatch.getPostconditions());

        ContractMatch contractMatch = computeNewContract(inputState, scopedHeap);
        return scopedHeap.merge(contractMatch);
    }

    private ContractMatch computeNewContract(ProgramState input, ScopedHeap scopedHeap) {

        HeapConfiguration heapInScope = scopedHeap.getHeapInScope();
        ProgramState initialState = input.shallowCopyWithUpdateHeap(heapInScope);
        Contract generatedContract = counterexampleContractGenerator.generateContract(initialState);
        ContractCollection contractCollection = getContractCollection();
        contractCollection.addContract(generatedContract);
        return contractCollection.matchContract(heapInScope);
    }
}
