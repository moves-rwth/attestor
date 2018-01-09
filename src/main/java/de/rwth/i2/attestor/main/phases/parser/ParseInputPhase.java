package de.rwth.i2.attestor.main.phases.parser;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.io.FileReader;
import de.rwth.i2.attestor.io.jsonImport.JsonToDefaultHC;
import de.rwth.i2.attestor.io.jsonImport.JsonToIndexedHC;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.phases.communication.InputSettings;
import de.rwth.i2.attestor.main.phases.transformers.GrammarTransformer;
import de.rwth.i2.attestor.main.phases.transformers.InputSettingsTransformer;
import de.rwth.i2.attestor.main.phases.transformers.InputTransformer;
import de.rwth.i2.attestor.main.scene.Scene;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ParseInputPhase extends AbstractPhase implements InputTransformer {

    private final List<HeapConfiguration> inputs = new ArrayList<>();

    public ParseInputPhase(Scene scene) {

        super(scene);
    }

    @Override
    public String getName() {

        return "Parse input";
    }

    @Override
    protected void executePhase() {

        String str;

        InputSettings inputSettings = getPhase(InputSettingsTransformer.class).getInputSettings();

        try {
            if (inputSettings.getInputName() != null) {
                logger.debug("Reading user-defined initial state.");
                str = FileReader.read(inputSettings.getInputLocation());
            } else {
                logger.debug("Reading predefined empty initial state.");
                str = FileReader.read(inputSettings.getInitialStatesURL().openStream());
            }
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }

        String renamedStr = renamingInitialState(str);
        if (!str.equals(renamedStr)) {
            str = renamedStr;
            logger.warn("Renamed types or fields in initial state. Please ignore this warning if types or fields");
            logger.warn("from predefined grammars were used in initial state by accident.");
        }

        JSONObject jsonObj = new JSONObject(str);

        HeapConfiguration originalInput;

        Consumer<String> addUsedSelectorLabel = scene().options()::addUsedSelectorLabel;

        if (scene().options().isIndexedMode()) {
            JsonToIndexedHC importer = new JsonToIndexedHC(this);
            originalInput = importer.jsonToHC(jsonObj, addUsedSelectorLabel);
        } else {
            JsonToDefaultHC importer = new JsonToDefaultHC(this);
            originalInput = importer.jsonToHC(jsonObj, addUsedSelectorLabel);
        }
        inputs.add(originalInput);
    }

    private String renamingInitialState(String str) {

        // Modify initial state (replace all keys in rename by its values)
        Map<String, String> renamingMap = getPhase(GrammarTransformer.class).getRenamingMap();
        if (renamingMap != null) {
            for (HashMap.Entry<String, String> renaming : renamingMap.entrySet()) {
                str = str.replaceAll("\"" + renaming.getKey() + "\"", "\"" + renaming.getValue() + "\"");
            }
        }
        return str;
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
