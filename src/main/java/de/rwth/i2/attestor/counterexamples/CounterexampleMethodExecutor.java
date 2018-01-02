package de.rwth.i2.attestor.counterexamples;

import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.ipa.methodExecution.*;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

import java.util.Collection;

public class CounterexampleMethodExecutor extends AbstractMethodExecutor {

    private final CounterexampleContractGenerator counterexampleContractGenerator;
    private final CanonicalizationStrategy canonicalizationStrategy;

    public CounterexampleMethodExecutor(ScopeExtractor scopeExtractor, ContractCollection contractCollection,
                                        CounterexampleContractGenerator contractGenerator,
                                        CanonicalizationStrategy canonicalizationStrategy) {

        super(scopeExtractor, contractCollection, contractGenerator);
        this.counterexampleContractGenerator = contractGenerator;
        this.canonicalizationStrategy = canonicalizationStrategy;
    }

    @Override
    protected Collection<HeapConfiguration> getPostconditions(ProgramState inputState, ScopedHeap scopedHeap) {

        HeapConfiguration abstractedHeapInScope = canonicalizationStrategy.canonicalize(scopedHeap.getHeapInScope());
        ContractMatch abstractMatch = getContractCollection().matchContract(abstractedHeapInScope);
        if(!abstractMatch.hasMatch()) {
            throw new IllegalStateException("Could not match contract during counterexample generation.");
        }
        counterexampleContractGenerator.setRequiredFinalHeaps(abstractMatch.getPostconditions());

        ContractMatch contractMatch = computeNewContract(inputState, scopedHeap);
        return scopedHeap.merge(contractMatch);
    }
}
