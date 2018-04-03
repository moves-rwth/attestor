package de.rwth.i2.attestor.phases.parser;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.io.FileReader;
import de.rwth.i2.attestor.io.jsonImport.JsonToHeapConfiguration;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.communication.InputSettings;
import de.rwth.i2.attestor.phases.transformers.InputSettingsTransformer;
import de.rwth.i2.attestor.phases.transformers.InputTransformer;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ParseInputPhase extends AbstractPhase implements InputTransformer {

    private final List<HeapConfiguration> inputs = new ArrayList<>();

    private InputSettings inputSettings;

    public ParseInputPhase(Scene scene) {

        super(scene);
    }

    @Override
    public String getName() {

        return "Parse input";
    }

    @Override
    public void executePhase() {


        inputSettings = getPhase(InputSettingsTransformer.class).getInputSettings();

        if (inputSettings.getInitialHeapFiles().isEmpty()) {
            inputs.add(scene().createHeapConfiguration());
            return;
        }

        for (String initialHeapFile : inputSettings.getInitialHeapFiles()) {
            addInitialHeap(initialHeapFile);
        }
    }

    private void addInitialHeap(String initialHeapFile) {

        String initialHeap;
        try {
                initialHeap = FileReader.read(initialHeapFile);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }

        JSONObject jsonObj = new JSONObject(initialHeap);
        Consumer<String> addUsedSelectorLabel = scene().labels()::addUsedSelectorLabel;
        JsonToHeapConfiguration importer = new JsonToHeapConfiguration(this, inputSettings);
        HeapConfiguration originalInput = importer.parse(jsonObj, addUsedSelectorLabel);
        inputs.add(originalInput);
    }

    @Override
    public void logSummary() {

        // nothing to report
    }

    @Override
    public boolean isVerificationPhase() {

        return false;
    }

    @Override
    public List<HeapConfiguration> getInputs() {

        return inputs;
    }
}
