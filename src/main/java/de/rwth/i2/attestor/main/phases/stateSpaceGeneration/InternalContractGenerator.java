package de.rwth.i2.attestor.main.phases.stateSpaceGeneration;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.ipa.contracts.InternalContract;
import de.rwth.i2.attestor.ipa.methodExecution.Contract;
import de.rwth.i2.attestor.ipa.methodExecution.ContractGenerator;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class InternalContractGenerator implements ContractGenerator {

    private final StateSpaceGeneratorFactory factory;
    private final Program program;

    public InternalContractGenerator(StateSpaceGeneratorFactory factory, Program program) {

        this.factory = factory;
        this.program = program;
    }

    @Override
    public Contract generateContract(ProgramState initialState) {

        Collection<ProgramState> finalStates;
        try {
            finalStates = factory.create(program, initialState)
                    .generate()
                    .getFinalStates();
        } catch (StateSpaceGenerationAbortedException e) {
            throw new IllegalStateException("Unable to generate state space for procedure.");
        }

        List<HeapConfiguration> postconditions = new ArrayList<>(finalStates.size());
        for(ProgramState state : finalStates) {
            postconditions.add(state.getHeap());
        }

        return new InternalContract(initialState.getHeap(), postconditions);
    }
}
