package de.rwth.i2.attestor.phases.report;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.grammar.GrammarExporter;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationExporter;
import de.rwth.i2.attestor.io.FileUtils;
import de.rwth.i2.attestor.io.jsonExport.cytoscapeFormat.*;
import de.rwth.i2.attestor.io.jsonExport.inputFormat.ContractToInputFormatExporter;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.PhaseRegistry;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.communication.OutputSettings;
import de.rwth.i2.attestor.phases.modelChecking.modelChecker.ModelCheckingResult;
import de.rwth.i2.attestor.phases.symbolicExecution.stateSpaceGenerationImpl.InternalStateSpace;
import de.rwth.i2.attestor.phases.transformers.*;
import de.rwth.i2.attestor.procedures.Contract;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceExporter;
import de.rwth.i2.attestor.util.ZipUtils;

import java.io.*;
import java.util.*;

public class ReportGenerationPhase extends AbstractPhase {

    private Program program;
    private OutputSettings outputSettings;
    private final PhaseRegistry registry;

    private final List<String> summaryMessages = new ArrayList<>();

    public ReportGenerationPhase(PhaseRegistry registry, Scene scene) {

        super(scene);
        this.registry = registry;
    }

    @Override
    public String getName() {

        return "Report generation";
    }

    @Override
    public void executePhase() {

        outputSettings = getPhase(OutputSettingsTransformer.class).getOutputSettings();
        program = getPhase(ProgramTransformer.class).getProgram();

        try {
            exportReport();
            exportGrammar();
            exportLargeStates();
            exportContracts();
            saveContracts();
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }

    }

    private void exportReport() throws IOException {

        String location = outputSettings.getExportPath();
        if(location == null) {
            return;
        }

        logger.info("Exporting report...");

        StateSpace stateSpace = getPhase(StateSpaceTransformer.class).getStateSpace();
        exportStateSpace(stateSpace, location, "data");
        exportCounterexamples(location);

        InputStream zis = getClass().getClassLoader().getResourceAsStream("viewer.zip");
        File targetDirectory = new File(location + File.separator);
        ZipUtils.unzip(zis, targetDirectory);

        exportOverview(location);

        String summary = "Report exported to " + location;
        logger.info(summary);
        summaryMessages.add(summary);
    }

    private void exportStateSpace(StateSpace stateSpace, String location, String directory) throws IOException {

        logger.info("Exporting state space...");

        exportStateSpace(
                location + File.separator + directory,
                stateSpace,
                program
        );

        Set<ProgramState> states = stateSpace.getStates();
        for (ProgramState state : states) {
            int i = state.getStateSpaceId();
            exportHeapConfiguration(
                    location + File.separator + directory,
                    "hc_" + i + ".json",
                    state.getHeap()
            );
        }
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

    private void exportHeapConfiguration(String directory, String filename, HeapConfiguration hc)
            throws IOException {

        FileUtils.createDirectories(directory);
        FileWriter writer = new FileWriter(directory + File.separator + filename);
        HeapConfigurationExporter exporter = new JsonHeapConfigurationExporter(writer);
        exporter.export(hc);
        writer.close();
    }

    private void exportCounterexamples(String location) throws IOException {

        ModelCheckingResultsTransformer transformer = getPhase(ModelCheckingResultsTransformer.class);
        int counter = 0;
        for(Map.Entry<LTLFormula, ModelCheckingResult> entry : transformer.getLTLResults().entrySet()) {
            if(entry.getValue() == ModelCheckingResult.UNSATISFIED) {
                LTLFormula formula = entry.getKey();
                StateSpace stateSpace = transformer.getTraceOf(formula).getStateSpace();
                exportStateSpace(stateSpace, location, "cex_" + String.valueOf(counter));
                ++counter;
            }
        }
    }

    private void exportOverview(String location) throws IOException {

        logger.info("Exporting overview...");
        String directory = location + File.separator + "data";

        FileUtils.createDirectories(directory);
        Writer writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(directory + File.separator + "overview.json"))
        );
        JsonOverviewExporter exporter = new JsonOverviewExporter(writer);
        exporter.export(registry);
        writer.close();
    }

    private void exportGrammar() throws IOException {

        String location = outputSettings.getExportGrammarPath();

        if(location == null) {
            return;
        }

        logger.info("Exporting grammar...");

        // Copy necessary libraries
        InputStream zis = getClass().getClassLoader().getResourceAsStream("grammarViewer.zip");

        File targetDirectory = new File(location + File.separator);
        ZipUtils.unzip(zis, targetDirectory);

        // Generate JSON files
        GrammarExporter exporter = new JsonGrammarExporter();
        exporter.export(location + File.separator + "grammarData",
                getPhase(GrammarTransformer.class).getGrammar());

        String summary = "Grammar exported to " + location;
        logger.info(summary);
        summaryMessages.add(summary);
    }

    private void exportLargeStates() throws IOException {

        String location = outputSettings.getExportLargeStatesPath();

        if(location == null) {
            return;
        }

        int threshold = scene().options().getMaxHeap();
        StateSpace stateSpace = getPhase(StateSpaceTransformer.class).getStateSpace();
        StateSpace largeStatesSpace = new InternalStateSpace(stateSpace.size());

        for(ProgramState state : stateSpace.getStates()) {
            if(state.size() > threshold) {
                largeStatesSpace.addState(state.clone());
            }
        }

        exportStateSpace(largeStatesSpace, location, "data");

        InputStream zis = getClass().getClassLoader().getResourceAsStream("viewer.zip");
        File targetDirectory = new File(location + File.separator);
        ZipUtils.unzip(zis, targetDirectory);

        exportOverview(location);

        logger.info("Exporting large states...");
        String summary = "Large states exported to " + location;
        logger.info(summary);
        summaryMessages.add(summary);
    }

    private void exportContracts() throws IOException {

        String location = outputSettings.getExportContractsPath();

        if(location == null) {
            return;
        }

        // Copy necessary libraries
        InputStream zis = getClass().getClassLoader().getResourceAsStream("contractViewer.zip");

        File targetDirectory = new File(location + File.separator);
        ZipUtils.unzip(zis, targetDirectory);

        Map<String,Collection<Contract>> contracts = new HashMap<>();
        for( Method method : scene().getRegisteredMethods() ){
            contracts.put(method.getName(), method.getContractsForExport());
        }

        // Generate JSON files
        JsonContractExporter exporter = new JsonContractExporter();
        exporter.export(location + File.separator + "contractData", contracts);


        logger.info("Exporting contracts...");
        String summary = "Contracts exported to " + location;
        logger.info(summary);
        summaryMessages.add(summary);
    }

    private void saveContracts() throws IOException {

        String location = outputSettings.getSaveContractsPath();

        if(location == null) {
            return;
        }

        FileUtils.createDirectories(location);

        for( Method method : scene().getRegisteredMethods() ){
            String name = method.getName();
            String filename = location + File.separator + name + ".json";
            FileWriter writer = new FileWriter(filename);
            ContractToInputFormatExporter exporter = new ContractToInputFormatExporter(writer);
            exporter.export(name, method.getContractsForExport());
            writer.close();
            logger.info("Saved contracts of method " + name + " in " + filename);
        }

        logger.info("Saving contracts...");
        String summary = "Contracts saved in " + location;
        logger.info(summary);
        summaryMessages.add(summary);
    }

    //--------------------------------------------------------------------------------------------------------


    @Override
    public void logSummary() {

        if(summaryMessages.isEmpty()) {
            return;
        }

        logHighlight("Exports:");
        for(String message : summaryMessages) {
            logSum(message);
        }
    }

    @Override
    public boolean isVerificationPhase() {

        return false;
    }
}
