package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.AnalysisTask;
import de.rwth.i2.attestor.main.AnalysisTaskBuilder;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.SSGBuilder;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerator;
import de.rwth.i2.attestor.tasks.GeneralAnalysisTaskBuilder;
import de.rwth.i2.attestor.tasks.defaultTask.DefaultState;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple builder class to create MockupTasks for testing.
 */
public class MockupTaskBuilder extends GeneralAnalysisTaskBuilder {

    @Override
    public AnalysisTask build() {

        SSGBuilder builder = StateSpaceGenerator.builder()
                .setProgram(program)
                .setMaterializationStrategy(new MockupMaterializationStrategy())
                .setCanonizationStrategy(new MockupCanonicalizationStrategy())
                .setAbortStrategy(new MockupAbortStrategy())
                .setInclusionStrategy(new MockupInclusionStrategy())
                .setStateLabelingStrategy(new MockupStateLabellingStrategy())
                .setStateRefinementStrategy(state -> state);


        for(ProgramState state : setupInitialStates()) {
            builder.addInitialState(state);
        }

        return new MockupTask(builder.build());
    }

    @Override
    public AnalysisTaskBuilder loadInput(String filename) throws FileNotFoundException {
        return this;
    }

    @Override
    public AnalysisTaskBuilder loadProgram(String classpath, String filename, String entryPoint) {
        return this;
    }

    @Override
    protected List<ProgramState> setupInitialStates() {

        List<ProgramState> initialStates = new ArrayList<>();
        for (HeapConfiguration input : inputs) {
            DefaultState state = new DefaultState(input);
            state.setProgramCounter(0);
            initialStates.add(state);
        }
        return initialStates;
    }
}
