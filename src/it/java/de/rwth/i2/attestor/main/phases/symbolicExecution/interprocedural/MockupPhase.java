package de.rwth.i2.attestor.main.phases.symbolicExecution.interprocedural;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.phases.transformers.InputTransformer;
import de.rwth.i2.attestor.main.phases.transformers.ProgramTransformer;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;

import java.io.IOException;
import java.util.List;

public class MockupPhase extends AbstractPhase implements ProgramTransformer, InputTransformer {

    private Program program;
    private List<HeapConfiguration> inputs;

    public MockupPhase(SceneObject sceneObject, Program program, List<HeapConfiguration> inputs) {
        super(sceneObject.scene());

        this.program = program;
        this.inputs = inputs;
    }

    @Override
    public String getName() {

        return "Mockup Phase";
    }

    @Override
    protected void executePhase() throws IOException {

    }

    @Override
    public void logSummary() {

    }

    @Override
    public boolean isVerificationPhase() {

        return false;
    }

    @Override
    public List<HeapConfiguration> getInputs() {

        return inputs;
    }

    @Override
    public Program getProgram() {

        return program;
    }
}
