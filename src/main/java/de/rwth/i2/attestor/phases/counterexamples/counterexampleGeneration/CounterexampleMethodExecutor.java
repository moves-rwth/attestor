package de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration;

import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.procedures.*;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

import java.util.Collection;
import java.util.Collections;

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
                                                              ScopedHeap scopedHeap) {

        HeapConfiguration abstractedHeapInScope = canonicalizationStrategy.canonicalize(scopedHeap.getHeapInScope());
        ContractMatch abstractMatch = getContractCollection().matchContract(abstractedHeapInScope);
        if(!abstractMatch.hasMatch()) {
            throw new IllegalStateException("Could not match contract during counterexample generation.");
        }
        counterexampleContractGenerator.setRequiredFinalHeaps(abstractMatch.getPostconditions());

        ContractMatch contractMatch = computeNewContract(callingState, scopedHeap);
        if(contractMatch.hasMatch()) {
            return scopedHeap.merge(contractMatch);
        }
        return Collections.emptySet();
    }

    private ContractMatch computeNewContract(ProgramState someState, ScopedHeap scopedHeap) {

        HeapConfiguration heapInScope = scopedHeap.getHeapInScope();
        ProgramState initialState = someState.shallowCopyWithUpdateHeap(heapInScope);
        Contract generatedContract = counterexampleContractGenerator.generateContract(initialState);
        ContractCollection contractCollection = getContractCollection();
        contractCollection.addContract(generatedContract);
        return contractCollection.matchContract(heapInScope);
    }
}
