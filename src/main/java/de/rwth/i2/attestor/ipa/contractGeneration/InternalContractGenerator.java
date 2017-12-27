package de.rwth.i2.attestor.ipa.contractGeneration;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.ipa.contracts.InternalContract;
import de.rwth.i2.attestor.ipa.methods.Contract;
import de.rwth.i2.attestor.ipa.methods.ContractGenerator;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InternalContractGenerator implements ContractGenerator {

    private MethodExecutor executor;

    public InternalContractGenerator(MethodExecutor executor) {
        this.executor = executor;
    }

    @Override
    public Contract generateContract(ProgramState initialState) {

        Collection<ProgramState> finalStates = executor.execute(initialState);
        List<HeapConfiguration> postconditions = new ArrayList<>(finalStates.size());
        for(ProgramState state : finalStates) {
            postconditions.add(state.getHeap());
        }
        return new InternalContract(initialState.getHeap(), postconditions);
    }
}
