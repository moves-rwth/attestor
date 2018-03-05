package de.rwth.i2.attestor.phases.report;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.grammar.GrammarExporter;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationExporter;
import de.rwth.i2.attestor.io.CustomHcListExporter;
import de.rwth.i2.attestor.io.FileUtils;
import de.rwth.i2.attestor.io.jsonExport.cytoscapeFormat.*;
import de.rwth.i2.attestor.io.jsonExport.inputFormat.ContractToInputFormatExporter;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.PhaseRegistry;
import de.rwth.i2.attestor.main.scene.ElementNotPresentException;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.communication.OutputSettings;
import de.rwth.i2.attestor.phases.modelChecking.modelChecker.ModelCheckingResult;
import de.rwth.i2.attestor.phases.transformers.*;
import de.rwth.i2.attestor.procedures.Contract;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceExporter;
import de.rwth.i2.attestor.util.ZipUtils;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ReportGenerationPhase extends AbstractPhase {

    private Program program;
    private OutputSettings outputSettings;
    private final PhaseRegistry registry;

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

        if (outputSettings.isNoExport()) {
            return;
        }

        program = getPhase(ProgramTransformer.class).getProgram();


        try {

            if (outputSettings.isExportGrammar()) {
                exportGrammar();
            }

            if (outputSettings.isExportStateSpace()) {
                StateSpace stateSpace = getPhase(StateSpaceTransformer.class).getStateSpace();
                exportStateSpace(stateSpace, "data");
                exportCounterexamples();

                String location = outputSettings.getLocationForStateSpace();
                InputStream zis = getClass().getClassLoader().getResourceAsStream("viewer.zip");
                File targetDirectory = new File(location + File.separator);
                ZipUtils.unzip(zis, targetDirectory);

                exportOverview();


            }

            if (outputSettings.isExportCustomHcs()) {
                exportCustomHcs();
            }

            if (outputSettings.isExportContractsForReuse()) {
                exportContractsForReuse();
            }
            
            if( outputSettings.isExportContractsForInspection() ){
            	exportContractsForInspection();
            }

        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }

    }

    private void exportCounterexamples() throws IOException {

        ModelCheckingResultsTransformer transformer = getPhase(ModelCheckingResultsTransformer.class);
        int counter = 0;
        for(Map.Entry<LTLFormula, ModelCheckingResult> entry : transformer.getLTLResults().entrySet()) {
            if(entry.getValue() == ModelCheckingResult.UNSATISFIED) {
                LTLFormula formula = entry.getKey();
                StateSpace stateSpace = transformer.getTraceOf(formula).getStateSpace();
                exportStateSpace(stateSpace, "cex_" + String.valueOf(counter));
                ++counter;
            }
        }
    }

    private void exportContractsForReuse() throws IOException {

        String directory = outputSettings.getDirectoryForReuseContracts();
        FileUtils.createDirectories(directory);
        for (String signature : outputSettings.getContractForReuseRequests().keySet()) {

            String filename = outputSettings.getContractForReuseRequests().get(signature);
            FileWriter writer = new FileWriter(directory + File.separator + filename);

            Collection<Contract> contracts;
			try {
				contracts = scene().getMethodIfPresent(signature).getContractsForExport();
				ContractToInputFormatExporter exporter = new ContractToInputFormatExporter(writer);
	            exporter.export(signature, contracts);
	            
			} catch (ElementNotPresentException e) {
				logger.info("The contract for " + signature + " is not present.");
			}

            
            writer.close();
        }
        logger.info("Exported contracts for reuse to '"
                + directory
                + "'"
        );
    }
	
    private void exportContractsForInspection() throws IOException {
    		
        logger.info("Exporting contracts for inspection ...");

        String location = outputSettings.getLocationForContractsForInspection();

        // Copy necessary libraries
        InputStream zis = getClass().getClassLoader().getResourceAsStream("contractViewer" +
                ".zip");

        File targetDirectory = new File(location + File.separator);
        ZipUtils.unzip(zis, targetDirectory);
        
        Map<String,Collection<Contract>> contracts = new HashMap<>();
        for( Method method : scene().getRegisteredMethods() ){
        	contracts.put(method.getName(), method.getContractsForExport());
        }

        // Generate JSON files
        JsonContractExporter exporter = new JsonContractExporter();
        exporter.export(location + File.separator + "contractData", contracts);

        logger.info("done. Contracts exported to '" + location + "'");
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

    private void exportStateSpace(StateSpace stateSpace, String directory) throws IOException {

        logger.info("Exporting state space...");
        String location = outputSettings.getLocationForStateSpace();

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


        logger.info("done. State space exported to '"
                + location
                + "'"
        );
    }

    private void exportOverview() throws IOException {

        logger.info("Exporting overview...");
        String location = outputSettings.getLocationForStateSpace();
        exportOverview(
                location + File.separator + "data"
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

    private void exportOverview(String directory) throws IOException {

        FileUtils.createDirectories(directory);
        Writer writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(directory + File.separator + "overview.json"))
        );
        JsonOverviewExporter exporter = new JsonOverviewExporter(writer);
        exporter.export(registry);
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
