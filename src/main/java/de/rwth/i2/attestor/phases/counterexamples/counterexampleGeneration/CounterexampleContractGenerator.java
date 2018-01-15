package de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.InternalContract;
import de.rwth.i2.attestor.procedures.Contract;
import de.rwth.i2.attestor.procedures.ContractGenerator;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CounterexampleContractGenerator implements ContractGenerator {

    private Collection<HeapConfiguration> requiredFinalHeaps;

    private final FinalStatesComputer finalStatesComputer;


    public CounterexampleContractGenerator(FinalStatesComputer finalStatesComputer) {

        assert finalStatesComputer != null;
        this.finalStatesComputer = finalStatesComputer;
    }

    void setRequiredFinalHeaps(Collection<HeapConfiguration> requiredFinalHeaps) {

        this.requiredFinalHeaps = requiredFinalHeaps;
    }

    Collection<HeapConfiguration> getRequiredFinalHeaps() {

        return requiredFinalHeaps;
    }

    @Override
    public Contract generateContract(ProgramState initialState) {

        if(requiredFinalHeaps == null || requiredFinalHeaps.isEmpty()) {
            throw new IllegalStateException("No required final states");
        }

        Collection<ProgramState> finalStates = finalStatesComputer.apply(requiredFinalHeaps, initialState);

        List<HeapConfiguration> postconditions = new ArrayList<>(finalStates.size());
        for(ProgramState state : finalStates) {
            postconditions.add(state.getHeap());
        }

        return new InternalContract(initialState.getHeap(), postconditions);
    }
}
