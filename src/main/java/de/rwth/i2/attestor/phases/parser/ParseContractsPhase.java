package de.rwth.i2.attestor.phases.parser;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.io.FileReader;
import de.rwth.i2.attestor.io.jsonImport.JsonToHeapConfiguration;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.communication.InputSettings;
import de.rwth.i2.attestor.phases.transformers.InputSettingsTransformer;
import de.rwth.i2.attestor.procedures.Method;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ParseContractsPhase extends AbstractPhase {

    private InputSettings inputSettings;

    public ParseContractsPhase(Scene scene) {

        super(scene);
    }

    @Override
    public String getName() {

        return "Parse contracts";
    }

    @Override
    public void executePhase() {

        inputSettings = getPhase(InputSettingsTransformer.class).getInputSettings();
        for(String contractFile : inputSettings.getContractFileNames()) {
            loadContract(contractFile);
        }
    }

    private void loadContract(String filename) {

        try {
            String str = FileReader.read(filename);

            JSONObject obj = new JSONObject(str);
            String signature = obj.getString("method");
            Method abstractMethod = scene().getOrCreateMethod(signature);

            Consumer<String> addUsedSelectorLabel = scene().labels()::addUsedSelectorLabel;
            JSONArray array = obj.getJSONArray("contracts");

            JsonToHeapConfiguration importer = new JsonToHeapConfiguration(this, inputSettings);

            for (int i = 0; i < array.length(); i++) {
                JSONObject contract = array.getJSONObject(i);
                final JSONObject jsonPrecondition = contract.getJSONObject("precondition");
                HeapConfiguration precondition = importer.parse(jsonPrecondition, addUsedSelectorLabel);

                List<HeapConfiguration> postconditions = new ArrayList<>();
                JSONArray jsonPostConditions = contract.getJSONArray("postconditions");
                for (int p = 0; p < jsonPostConditions.length(); p++) {
                    final JSONObject jsonPostcondition = jsonPostConditions.getJSONObject(p);
                    postconditions.add(importer.parse(jsonPostcondition, addUsedSelectorLabel));
                }
                abstractMethod.addContract(
                        scene().createContract(precondition, postconditions)
                );
            }


        } catch (FileNotFoundException e) {
            logger.error("Could not parse contract at location " + filename + ". Skipping it.");
        }
    }

    @Override
    public void logSummary() {
        // nothing to report

    }

    @Override
    public boolean isVerificationPhase() {

        return false;
    }

}
