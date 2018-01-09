package de.rwth.i2.attestor.phases.report;

import de.rwth.i2.attestor.grammar.GrammarExporter;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationExporter;
import de.rwth.i2.attestor.io.CustomHcListExporter;
import de.rwth.i2.attestor.io.FileUtils;
import de.rwth.i2.attestor.io.jsonExport.cytoscapeFormat.JsonCustomHcListExporter;
import de.rwth.i2.attestor.io.jsonExport.cytoscapeFormat.JsonGrammarExporter;
import de.rwth.i2.attestor.io.jsonExport.cytoscapeFormat.JsonHeapConfigurationExporter;
import de.rwth.i2.attestor.io.jsonExport.cytoscapeFormat.JsonStateSpaceExporter;
import de.rwth.i2.attestor.io.jsonExport.inputFormat.ContractToInputFormatExporter;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.communication.OutputSettings;
import de.rwth.i2.attestor.phases.transformers.GrammarTransformer;
import de.rwth.i2.attestor.phases.transformers.OutputSettingsTransformer;
import de.rwth.i2.attestor.phases.transformers.ProgramTransformer;
import de.rwth.i2.attestor.phases.transformers.StateSpaceTransformer;
import de.rwth.i2.attestor.procedures.methodExecution.Contract;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceExporter;
import de.rwth.i2.attestor.util.ZipUtils;

import java.io.*;
import java.util.Collection;
import java.util.Set;

public class ReportGenerationPhase extends AbstractPhase {

    private StateSpace stateSpace;
    private Program program;
    private OutputSettings outputSettings;

    public ReportGenerationPhase(Scene scene) {

        super(scene);
    }

    @Override
    public String getName() {

        return "Report generation";
    }

    @Override
    protected void executePhase() {

        outputSettings = getPhase(OutputSettingsTransformer.class).getOutputSettings();

        if (outputSettings.isNoExport()) {
            return;
        }

        stateSpace = getPhase(StateSpaceTransformer.class).getStateSpace();
        program = getPhase(ProgramTransformer.class).getProgram();

        try {
            if (outputSettings.isExportGrammar()) {
                exportGrammar();
            }

            if (outputSettings.isExportStateSpace()) {
                exportStateSpace();
            }

            if (outputSettings.isExportCustomHcs()) {
                exportCustomHcs();
            }

            if (outputSettings.isExportContracts()) {
                exportContracts();
            }

        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }

    }

    private void exportContracts() throws IOException {

        String directory = outputSettings.getDirectoryForContracts();
        FileUtils.createDirectories(directory);
        for (String signature : outputSettings.getContractRequests().keySet()) {

            String filename = outputSettings.getContractRequests().get(signature);
            FileWriter writer = new FileWriter(directory + File.separator + filename);

            Collection<Contract> contracts = scene().getMethod(signature).getContracts();

            ContractToInputFormatExporter exporter = new ContractToInputFormatExporter(writer);
            exporter.export(signature, contracts);
            writer.close();
        }
    }

    private void exportCustomHcs() throws IOException {

        String location = outputSettings.getLocationForCustomHcs();

        // Copy necessary libraries
        InputStream zis = getClass().getClassLoader().getResourceAsStream("customHcViewer" +
                ".zip");

        File targetDirectory = new File(location + File.separator);
        ZipUtils.unzip(zis, targetDirectory);

        // Generate JSON files for prebooked HCs and their summary
        CustomHcListExporter exporter = new JsonCustomHcListExporter();
        exporter.export(location + File.separator + "customHcsData", outputSettings.getCustomHcSet());

        logger.info("Custom HCs exported to '"
                + location
        );
    }

    private void exportStateSpace() throws IOException {

        logger.info("Exporting state space...");
        String location = outputSettings.getLocationForStateSpace();

        exportStateSpace(
                location + File.separator + "data",
                stateSpace,
                program
        );

        Set<ProgramState> states = stateSpace.getStates();
        for (ProgramState state : states) {
            int i = state.getStateSpaceId();
            exportHeapConfiguration(
                    location + File.separator + "data",
                    "hc_" + i + ".json",
                    state.getHeap()
            );
        }

        InputStream zis = getClass().getClassLoader().getResourceAsStream("viewer.zip");

        File targetDirectory = new File(location + File.separator);
        ZipUtils.unzip(zis, targetDirectory);

        logger.info("done. State space exported to '"
                + location
                + "'"
        );
    }

    private void exportGrammar() throws IOException {

        logger.info("Exporting grammar...");

        String location = outputSettings.getLocationForGrammar();

        // Copy necessary libraries
        InputStream zis = getClass().getClassLoader().getResourceAsStream("grammarViewer" +
                ".zip");

        File targetDirectory = new File(location + File.separator);
        ZipUtils.unzip(zis, targetDirectory);

        // Generate JSON files
        GrammarExporter exporter = new JsonGrammarExporter();
        exporter.export(location + File.separator + "grammarData",
                getPhase(GrammarTransformer.class).getGrammar());

        logger.info("done. Grammar exported to '" + location + "'");
    }

    private void exportHeapConfiguration(String directory, String filename, HeapConfiguration hc)
            throws IOException {

        FileUtils.createDirectories(directory);
        FileWriter writer = new FileWriter(directory + File.separator + filename);
        HeapConfigurationExporter exporter = new JsonHeapConfigurationExporter(writer);
        exporter.export(hc);
        writer.close();
    }

    private void exportStateSpace(String directory, StateSpace stateSpace, Program program)
            throws IOException {

        FileUtils.createDirectories(directory);
        Writer writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(directory + File.separator + "statespace.json"))
        );
        StateSpaceExporter exporter = new JsonStateSpaceExporter(writer);
        exporter.export(stateSpace, program);
        writer.close();
    }

    @Override
    public void logSummary() {

        if (!outputSettings.isNoExport() && outputSettings.isExportStateSpace()) {
            String location = outputSettings.getLocationForStateSpace();
            logHighlight("State space has been exported to:");
            logSum(location);
        }
    }

    @Override
    public boolean isVerificationPhase() {

        return false;
    }
}
