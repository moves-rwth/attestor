package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls;

import java.io.FileNotFoundException;

import de.rwth.i2.attestor.main.AnalysisTask;
import de.rwth.i2.attestor.main.AnalysisTaskBuilder;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerator;
import de.rwth.i2.attestor.tasks.GeneralAnalysisTaskBuilder;
import de.rwth.i2.attestor.tasks.defaultTask.DefaultState;

/**
 * Simple builder class to create MockupTasks for testing.
 */
public class MockupTaskBuilder extends GeneralAnalysisTaskBuilder {

    @Override
    public AnalysisTask build() {

        StateSpaceGenerator stateSpaceGenerator = StateSpaceGenerator.builder()
                .setProgram(program)
                .setInitialState(setupInitialState())
                .setMaterializationStrategy(new MockupMaterializationStrategy())
                .setCanonizationStrategy(new MockupCanonicalizationStrategy())
                .setAbortStrategy(new MockupAbortStrategy())
                .setInclusionStrategy(new MockupInclusionStrategy())
                .setStateLabelingStrategy(new MockupStateLabellingStrategy())
                .build();


        return new MockupTask(stateSpaceGenerator);
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
    protected ProgramState setupInitialState() {
        DefaultState state = new DefaultState(input);
        state.setProgramCounter(0);
        return state;
    }
}
