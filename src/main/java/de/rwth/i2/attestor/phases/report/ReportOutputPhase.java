package de.rwth.i2.attestor.phases.report;

import de.rwth.i2.attestor.grammar.GrammarExporter;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationExporter;
import de.rwth.i2.attestor.io.FileUtils;
import de.rwth.i2.attestor.io.SummaryExporter;
import de.rwth.i2.attestor.io.jsonExport.cytoscapeFormat.JsonGrammarExporter;
import de.rwth.i2.attestor.io.jsonExport.cytoscapeFormat.JsonHeapConfigurationExporter;
import de.rwth.i2.attestor.io.jsonExport.cytoscapeFormat.JsonStateSpaceExporter;
import de.rwth.i2.attestor.io.SummaryExporter;
import de.rwth.i2.attestor.io.jsonExport.report.JSONOptionExporter;
import de.rwth.i2.attestor.io.jsonExport.report.JSONSummaryExporter;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.PhaseRegistry;
import de.rwth.i2.attestor.phases.communication.InputSettings;
import de.rwth.i2.attestor.phases.communication.OutputSettings;
import de.rwth.i2.attestor.phases.transformers.*;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.communication.OutputSettings;
import de.rwth.i2.attestor.phases.modelChecking.ModelCheckingPhase;
import de.rwth.i2.attestor.phases.parser.CLIPhase;
import de.rwth.i2.attestor.phases.transformers.*;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceExporter;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONWriter;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.impl.client.HttpClientBuilder;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created by christina on 01.12.17.
 *
 * Takes care of producing the necessary output files for the report.
 */
public class ReportOutputPhase extends AbstractPhase {

    private List<AbstractPhase> phases;

    private InputSettings inputSettings;
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
    public void executePhase() throws IOException {

        inputSettings = getPhase(InputSettingsTransformer.class).getInputSettings();
        outputSettings = getPhase(OutputSettingsTransformer.class).getOutputSettings();

        if (outputSettings.isNoExport()) {
            return;
        }

        sendBenchmarkRegisterRequest(scene().getIdentifier(), inputSettings.getName());

        stateSpace = getPhase(StateSpaceTransformer.class).getStateSpace();
        program = getPhase(ProgramTransformer.class).getProgram();

        String outputDirectory = outputSettings.getFolderForReportOutput();

        /* Export the attestor input relevant for the report */

        // Export the initial heap configurations (consequtively numbered)
        exportSummaryInitialHCs(outputDirectory);
        exportInitialHCs(outputDirectory);
        // Copy the settingsfile
        copySettingsFile(outputDirectory);
        // Export _all_ options
        exportOptions(outputDirectory);


        // Copy input class definition
        copyInputProgram(outputDirectory);

        // Export grammar (without preceding elements!!)
        exportGrammar(outputDirectory);

        /* Export the analysis output relevant for the report */

        // Export analysis summary
        exportSummary(outputDirectory);

        // Export state space (without preceding elements!!)
        exportStateSpace(outputDirectory);
    }

    private void exportSummaryInitialHCs(String outputDirectory) {
    }

    private void exportOptions(String outputDirectory) throws IOException {
        FileUtils.createDirectories(outputDirectory);
        FileWriter writer = new FileWriter(outputDirectory + File.separator + "attestorInput" + File.separator + "options.json");
        JSONOptionExporter exporter = new JSONOptionExporter(writer);
        exporter.exportForReport(scene());
        writer.close();
    }

    private void copySettingsFile(String location) throws IOException {
        Path sourcePath = FileSystems.getDefault().getPath(inputSettings.getPathToSettingsFile(), "");
        Path targetPath = FileSystems.getDefault().getPath(location + File.separator + "attestorInput", "settings.json");

        Files.copy(sourcePath, targetPath, REPLACE_EXISTING);

    }

    private void copyInputProgram(String location) throws IOException {
        Path sourcePath = FileSystems.getDefault().getPath(inputSettings.getClasspath(), inputSettings.getClassName() + ".java");
        Path targetPath = FileSystems.getDefault().getPath(location + File.separator + "attestorInput", "analysedClass.java");

        Files.copy(sourcePath, targetPath, REPLACE_EXISTING);
    }

    private void exportInitialHCs(String location) throws IOException {
        logger.info("Exporting initial HCs for report...");

        List<HeapConfiguration> initialHCs = getPhase(InputTransformer.class).getInputs();

        FileUtils.createDirectories(location);
        FileWriter writer = new FileWriter(location + File.separator + "attestorInput" + File.separator + "initialHCsSummary.json");
        exportSummaryInitialHCs(writer, initialHCs);
        writer.close();

        int i = 0;
        for (HeapConfiguration initialHC : initialHCs) {

            location = location + File.separator + "attestorInput";
            String filename = "initialHC" + i + ".json";
            try {
                exportHeapConfiguration(location, filename , initialHC);
                i++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void exportSummaryInitialHCs(FileWriter writer, List<HeapConfiguration> initialHCs) {

        JSONWriter jsonWriter = new JSONWriter(writer);

        jsonWriter.object()
                    .key("number").value(initialHCs.size())
                    .endObject();
    }

    private void exportGrammar(String location) throws IOException {

        logger.info("Exporting grammar for report...");

        location = location + File.separator + "grammarData";

        // Generate JSON files
        GrammarExporter exporter = new JsonGrammarExporter();
        exporter.exportForReport(location,
                getPhase(GrammarTransformer.class).getGrammar());

        logger.info("done. Grammar for report exported to " + location );
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

        logger.info("done. State space for report exported to "
                + location
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

        logger.info("done. Analysis summary for report exported to "
                + location
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

    private void sendBenchmarkRegisterRequest(int id, String bName) throws UnsupportedEncodingException {
        // Set up http client
        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpPost httppost = new HttpPost("http://localhost:9200/benchmark");
        httppost.addHeader("content-type", "application/json");

        // Request parameters
        StringEntity params = new StringEntity("{\"id\":" + id + ",\"name\":\"" + bName + "\"} ");
        httppost.setEntity(params);

        //Execute
        try {
            httpclient.execute(httppost);
        } catch (IOException e) {
            logger.warn("Not able to register benchmark with the API.");
        }
    }

}
