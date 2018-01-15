package de.rwth.i2.attestor.phases.symbolicExecution.recursive;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.phases.communication.InputSettings;
import de.rwth.i2.attestor.phases.transformers.InputSettingsTransformer;
import de.rwth.i2.attestor.phases.transformers.InputTransformer;

import java.io.IOException;
import java.util.List;

public class MockupPhase extends AbstractPhase implements InputTransformer, InputSettingsTransformer {

    private List<HeapConfiguration> inputs;
    private String mainMethodName;

    public MockupPhase(SceneObject sceneObject, List<HeapConfiguration> inputs, String mainMethodName) {
        super(sceneObject.scene());

        this.inputs = inputs;
        this.mainMethodName = mainMethodName;
    }

    @Override
    public String getName() {

        return "Mockup Phase";
    }

    @Override
    public void executePhase() throws IOException {

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
    public InputSettings getInputSettings() {

       InputSettings result = new InputSettings();
       result.setMethodName(mainMethodName);
       return result;
    }
}
