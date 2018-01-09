package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.InternalContract;
import de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies.NoStateRefinementStrategy;
import de.rwth.i2.attestor.procedures.Contract;
import de.rwth.i2.attestor.procedures.ContractGenerator;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerator;

import java.util.ArrayList;
import java.util.Collection;

public class MockupContractGenerator implements ContractGenerator {

    private Program program;

    public MockupContractGenerator(Program program) {

        this.program = program;
    }

    @Override
    public Contract generateContract(ProgramState initialState) {

        try {
            Collection<ProgramState> postStates = StateSpaceGenerator.builder()
                    .addInitialState(initialState)
                    .setProgram(program)
                    .setCanonizationStrategy(new MockupCanonicalizationStrategy())
                    .setMaterializationStrategy(new MockupMaterializationStrategy())
                    .setAbortStrategy(new MockupAbortStrategy())
                    .setPostProcessingStrategy(originalStateSpace -> {
                    })
                    .setExplorationStrategy((state, stateSpace) -> true)
                    .setStateRefinementStrategy(new NoStateRefinementStrategy())
                    .setStateLabelingStrategy(new MockupStateLabellingStrategy())
                    .setStateCounter(states -> {
                    })
                    .setStateSpaceSupplier(new MockupStateSpaceSupplier())
                    .build()
                    .generate()
                    .getFinalStates();

            Collection<HeapConfiguration> postconditions = new ArrayList<>();
            for (ProgramState state : postStates) {
                postconditions.add(state.getHeap());
            }

            return new InternalContract(initialState.getHeap(), postconditions);
        } catch (StateSpaceGenerationAbortedException e) {
            assert false;
        }
        return null;
    }
}
