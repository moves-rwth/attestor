package de.rwth.i2.attestor.main.phases.report;

import de.rwth.i2.attestor.grammar.GrammarExporter;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationExporter;
import de.rwth.i2.attestor.io.FileUtils;
import de.rwth.i2.attestor.io.SummaryExporter;
import de.rwth.i2.attestor.io.jsonExport.cytoscapeFormat.JsonGrammarExporter;
import de.rwth.i2.attestor.io.jsonExport.cytoscapeFormat.JsonHeapConfigurationExporter;
import de.rwth.i2.attestor.io.jsonExport.cytoscapeFormat.JsonStateSpaceExporter;
import de.rwth.i2.attestor.io.jsonExport.report.JSONSummaryExporter;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.phases.communication.OutputSettings;
import de.rwth.i2.attestor.main.phases.modelChecking.ModelCheckingPhase;
import de.rwth.i2.attestor.main.phases.parser.CLIPhase;
import de.rwth.i2.attestor.main.phases.transformers.*;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceExporter;

import java.io.*;
import java.util.List;
import java.util.Set;

/**
 * Created by christina on 01.12.17.
 *
 * Takes care of producing the necessary output files for the report.
 */
public class ReportOutputPhase extends AbstractPhase {

    private List<AbstractPhase> phases;

    private OutputSettings outputSettings;

    private StateSpace stateSpace;
    private Program program;


    public ReportOutputPhase(Scene scene, List<AbstractPhase> phases) {

        super(scene);
        this.phases = phases;
    }

    @Override
    public String getName() {

        return "Report output generation";
    }

    @Override
    public boolean isVerificationPhase() {

        return false;
    }

    @Override
    public void logSummary(){
       if(!outputSettings.isNoExport() && outputSettings.isExportReportOutput()) {

            logSum("Output for report generated in " + outputSettings.getFolderForReportOutput());
       }
    }

    @Override
    protected void executePhase() throws IOException {

        outputSettings = getPhase(OutputSettingsTransformer.class).getOutputSettings();

        if (outputSettings.isNoExport()) {
            return;
        }

        stateSpace = getPhase(StateSpaceTransformer.class).getStateSpace();
        program = getPhase(ProgramTransformer.class).getProgram();

        String outputDirectory = outputSettings.getFolderForReportOutput();

        // Export analysis summary
        exportSummary(outputDirectory);

        // Export grammar (without preceding elements!!)
        exportGrammar(outputDirectory);

        // Export state space (without preceding elements!!)
        exportStateSpace(outputDirectory);
    }

    private void exportGrammar(String location) throws IOException {

        logger.info("Exporting grammar for report...");

        location = location + File.separator + "grammarData";

        // Generate JSON files
        GrammarExporter exporter = new JsonGrammarExporter();
        exporter.exportForReport(location,
                getPhase(GrammarTransformer.class).getGrammar());

        logger.info("done. Grammar for report exported to '" + location + "'");
    }

    private void exportStateSpace(String location) throws IOException {

        logger.info("Exporting state space for report...");

        location = location + File.separator + "stateSpaceData";

        exportStateSpace(
                location,
                stateSpace,
                program
        );

        Set<ProgramState> states = stateSpace.getStates();
        for (ProgramState state : states) {
            int i = state.getStateSpaceId();
            exportHeapConfiguration(
                    location,
                    "hc_" + i + ".json",
                    state.getHeap()
            );
        }

        logger.info("done. State space for report exported to '"
                + location
                + "'"
        );
    }

    private void exportSummary(String location) throws IOException{

        logger.info("Exporting analysis summary for report...");

        location = location + File.separator + "attestorOutput";

        FileUtils.createDirectories(location);
        FileWriter writer = new FileWriter(location + File.separator + "analysisSummary.json");

        SummaryExporter exporter = new JSONSummaryExporter(writer);
        exporter.exportForReport(scene(), stateSpace, (ModelCheckingPhase) getPhase(ModelCheckingResultsTransformer.class), getPhase(MCSettingsTransformer.class).getMcSettings(),(CLIPhase) getPhase(CLIPhase.class), phases);
        writer.close();

        logger.info("done. Analysis summary for report exported to '"
                + location
                + "'"
        );
    }

    private void exportStateSpace(String directory, StateSpace stateSpace, Program program)
            throws IOException {

        FileUtils.createDirectories(directory);
        Writer writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(directory + File.separator + "statespace.json"))
        );
        StateSpaceExporter exporter = new JsonStateSpaceExporter(writer);
        exporter.exportForReport(stateSpace, program);
        writer.close();
    }

    private void exportHeapConfiguration(String directory, String filename, HeapConfiguration hc)
            throws IOException {

        FileUtils.createDirectories(directory);
        FileWriter writer = new FileWriter(directory + File.separator + filename);
        HeapConfigurationExporter exporter = new JsonHeapConfigurationExporter(writer);
        exporter.exportForReport(hc);
        writer.close();
    }

}
