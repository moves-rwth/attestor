package de.rwth.i2.attestor.main.phases.impl;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.io.FileReader;
import de.rwth.i2.attestor.io.jsonImport.JsonImporter;
import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.phases.communication.InputSettings;
import de.rwth.i2.attestor.main.phases.transformers.InputSettingsTransformer;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.procedures.Method;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ParseContractsPhase extends AbstractPhase {

    public ParseContractsPhase(Scene scene) {

        super(scene);
    }

    @Override
    public String getName() {

        return "Parse contracts";
    }

    @Override
    protected void executePhase() {

        InputSettings inputSettings = getPhase(InputSettingsTransformer.class).getInputSettings();
        ArrayList<String> fileNames = inputSettings.getContractFileNames();
        if (!fileNames.isEmpty()) {
            String path = inputSettings.getPathToContracts();
            for (String fileName : fileNames) {
                loadContract(path, fileName);
            }
        }

    }

    private void loadContract(String path, String filename) {

        try {
            String str = FileReader.read(path + File.separator + filename);

            JSONObject obj = new JSONObject(str);
            String signature = obj.getString("method");
            Method abstractMethod = scene().getMethod(signature);

            Consumer<String> addUsedSelectorLabel = scene().options()::addUsedSelectorLabel;
            JSONArray array = obj.getJSONArray("contracts");

            JsonImporter importer = new JsonImporter(this);

            for (int i = 0; i < array.length(); i++) {
                JSONObject contract = array.getJSONObject(i);
                final JSONObject jsonPrecondition = contract.getJSONObject("precondition");
                HeapConfiguration precondition = importer.parseHC(jsonPrecondition, addUsedSelectorLabel);

                List<HeapConfiguration> postconditions = new ArrayList<>();
                JSONArray jsonPostConditions = contract.getJSONArray("postconditions");
                for (int p = 0; p < jsonPostConditions.length(); p++) {
                    final JSONObject jsonPostcondition = jsonPostConditions.getJSONObject(p);
                    postconditions.add(importer.parseHC(jsonPostcondition, addUsedSelectorLabel));
                }
                abstractMethod.addContract(
                        scene().createContract(precondition, postconditions)
                );
            }


        } catch (FileNotFoundException e) {
            logger.error("Could not parse contract at location " + path + File.separator + filename + ". Skipping it.");
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
