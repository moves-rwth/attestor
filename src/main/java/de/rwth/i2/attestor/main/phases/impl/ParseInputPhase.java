package de.rwth.i2.attestor.main.phases.impl;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.io.JsonToDefaultHC;
import de.rwth.i2.attestor.io.JsonToIndexedHC;
import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.phases.transformers.InputTransformer;
import de.rwth.i2.attestor.util.FileReader;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ParseInputPhase extends AbstractPhase implements InputTransformer {

    private List<HeapConfiguration> inputs = new ArrayList<>();

    @Override
    public String getName() {

        return "Parse input";
    }

    @Override
    protected void executePhase() {

        String str;

        try {
            if (settings.input().getInputName() != null) {
                logger.debug("Reading user-defined initial state.");
                str = FileReader.read(settings.input().getInputLocation());
            } else {
                logger.debug("Reading predefined empty initial state.");
                str = FileReader.read(settings.input().getInitialStatesURL().openStream());
            }
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }

        JSONObject jsonObj = new JSONObject(str);

        HeapConfiguration originalInput;

        Consumer<String> addUsedSelectorLabel = settings.input()::addUsedSelectorLabel;

        if(settings.options().isIndexedMode()) {
            originalInput = JsonToIndexedHC.jsonToHC( jsonObj, addUsedSelectorLabel );
        } else {
            originalInput = JsonToDefaultHC.jsonToHC( jsonObj, addUsedSelectorLabel );
        }
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
