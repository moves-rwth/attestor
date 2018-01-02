package de.rwth.i2.attestor.counterexamples;

import de.rwth.i2.attestor.grammar.languageInclusion.LanguageInclusionStrategy;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.ipa.contracts.InternalContract;
import de.rwth.i2.attestor.ipa.methodExecution.Contract;
import de.rwth.i2.attestor.ipa.methodExecution.ContractGenerator;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class CounterexampleContractGenerator implements ContractGenerator {

    private Predicate<ProgramState> requiredFinalStatesPredicate;

    private final BiFunction<Predicate<ProgramState>, ProgramState, Collection<ProgramState>> finalStatesComputer;
    private final LanguageInclusionStrategy inclusionStrategy;

    public CounterexampleContractGenerator(
            BiFunction<Predicate<ProgramState>, ProgramState, Collection<ProgramState>> finalStatesComputer,
            LanguageInclusionStrategy inclusionStrategy
    ) {

        assert finalStatesComputer != null;
        this.finalStatesComputer = finalStatesComputer;
        this.inclusionStrategy = inclusionStrategy;
    }

    void setRequiredFinalHeaps(Collection<HeapConfiguration> requiredFinalHeaps) {

        requiredFinalStatesPredicate = state -> {
            HeapConfiguration hc = state.getHeap();
            for(HeapConfiguration requiredHc: requiredFinalHeaps) {
                if(inclusionStrategy.includes(hc, requiredHc)) {
                    return true;
                }
            }
            return false;
        };
    }

    Predicate<ProgramState> getRequiredFinalStatesPredicate() {

        return requiredFinalStatesPredicate;
    }

    @Override
    public Contract generateContract(ProgramState initialState) {

        if(requiredFinalStatesPredicate == null) {
            throw new IllegalStateException("No required final states");
        }

        Collection<ProgramState> finalStates = finalStatesComputer.apply(requiredFinalStatesPredicate, initialState);

        List<HeapConfiguration> postconditions = new ArrayList<>(finalStates.size());
        for(ProgramState state : finalStates) {
            postconditions.add(state.getHeap());
        }

        return new InternalContract(initialState.getHeap(), postconditions);
    }
}
