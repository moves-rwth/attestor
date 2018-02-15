package de.rwth.i2.attestor.phases.report;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.grammar.GrammarExporter;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationExporter;
import de.rwth.i2.attestor.io.HttpExporter;
import de.rwth.i2.attestor.io.SummaryExporter;
import de.rwth.i2.attestor.io.jsonExport.cytoscapeFormat.JsonGrammarExporter;
import de.rwth.i2.attestor.io.jsonExport.cytoscapeFormat.JsonHeapConfigurationExporter;
import de.rwth.i2.attestor.io.jsonExport.cytoscapeFormat.JsonStateSpaceExporter;
import de.rwth.i2.attestor.io.jsonExport.report.JSONOptionExporter;
import de.rwth.i2.attestor.io.jsonExport.report.JSONSummaryExporter;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.communication.InputSettings;
import de.rwth.i2.attestor.phases.communication.OutputSettings;
import de.rwth.i2.attestor.phases.modelChecking.ModelCheckingPhase;
import de.rwth.i2.attestor.phases.modelChecking.modelChecker.ModelCheckingTrace;
import de.rwth.i2.attestor.phases.parser.CLIPhase;
import de.rwth.i2.attestor.phases.transformers.*;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceExporter;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    private HttpExporter httpExporter;


    public ReportOutputPhase(Scene scene, List<AbstractPhase> phases) {

        super(scene);
        this.phases = phases;
        this.httpExporter = new HttpExporter();
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
    }

    @Override
    public void executePhase() throws IOException {

        inputSettings = getPhase(InputSettingsTransformer.class).getInputSettings();
        outputSettings = getPhase(OutputSettingsTransformer.class).getOutputSettings();

        if (outputSettings.isNoExport()) {
            return;
        }

        this.httpExporter.sendBenchmarkRegisterRequest(scene().getIdentifier(), inputSettings.getName());

        stateSpace = getPhase(StateSpaceTransformer.class).getStateSpace();
        program = getPhase(ProgramTransformer.class).getProgram();

        /* Export the attestor input relevant for the report */

        // Export the initial heap configurations (consequtively numbered)
        exportSummaryInitialHCs();
        exportInitialHCs();

        // Copy the settingsfile
        copySettingsFile();
        // Export _all_ options
        exportOptions();

        // Copy input class definition
        copyInputProgram();

        // Export grammar (without preceding elements!!)
        exportGrammar();

        /* Export the analysis output relevant for the report */

        // Export analysis summary
        exportSummary();

        // Export state space (without preceding elements!!)
        exportStateSpace();

        // Export counterexamples
        exportCounterExamples();
    }

    private void exportCounterExamples() throws IOException {
        // Check counterexample for each formula
        for(LTLFormula formula : getPhase(CounterexampleTransformer.class).getFormulasWithCounterexamples()){
            ModelCheckingTrace trace = getPhase(ModelCheckingResultsTransformer.class).getTraceOf(formula);
            StateSpace counterexample = trace.getStateSpace();
            StateSpaceExporter exporter = new JsonStateSpaceExporter();
            // Send counterexample state space
            this.httpExporter.sendCounterexampleSummaryRequest(scene().getIdentifier(), Objects.hashCode(formula.getFormulaString()), exporter.exportForReport(counterexample, program));

            // Send state HCs
            Set<ProgramState> states = counterexample.getStates();

            HeapConfigurationExporter hcExporter = new JsonHeapConfigurationExporter();
            for (ProgramState state : states) {
                int i = state.getStateSpaceId();
                this.httpExporter.sendCounterexampleHCRequest(scene().getIdentifier(), Objects.hashCode(formula.getFormulaString()), i, hcExporter.exportForReport(state.getHeap()));
            }

            // Send concrete HC
            HeapConfiguration concreteHC = getPhase(CounterexampleTransformer.class).getInputOf(formula).getHeap();
            this.httpExporter.sendCounterexampleConcreteHCRequest(scene().getIdentifier(), Objects.hashCode(formula.getFormulaString()), hcExporter.exportForReport(concreteHC));

        }

    }

    private void exportOptions() throws IOException {
        JSONOptionExporter exporter = new JSONOptionExporter(this.httpExporter);
        exporter.exportForReport(scene());
    }

    private void copySettingsFile() throws IOException {
        Path sourcePath = FileSystems.getDefault().getPath(inputSettings.getPathToSettingsFile(), "");

        String settings = "";
        for(String line : Files.readAllLines(sourcePath)){
            settings += line;
        }

        // Remove unnecessary tabs & whitespaces
        JSONObject jsonSettings = new JSONObject(settings.toString());

        this.httpExporter.sendSettingsFileRequest(scene().getIdentifier(), jsonSettings.toString());


    }

    private void copyInputProgram() throws IOException {
        Path sourcePath = FileSystems.getDefault().getPath(inputSettings.getClasspath(), inputSettings.getClassName() + ".java");

        String settings = "";
        for(String line : Files.readAllLines(sourcePath)){
            settings += line + System.lineSeparator();
        }

        this.httpExporter.sendProgramFileRequest(scene().getIdentifier(), settings);
    }

    private void exportInitialHCs() throws IOException {
        logger.info("Exporting initial HCs for report...");

        List<HeapConfiguration> initialHCs = getPhase(InputTransformer.class).getInputs();

        int i = 0;
        for (HeapConfiguration initialHC : initialHCs) {

            HeapConfigurationExporter hcExporter = new JsonHeapConfigurationExporter();

            this.httpExporter.sendInitialHCRequest(scene().getIdentifier(), i, hcExporter.exportForReport(initialHC));
            i++;
        }
    }

    private void exportSummaryInitialHCs() {

        List<HeapConfiguration> initialHCs = getPhase(InputTransformer.class).getInputs();

        JSONStringer jsonStringer = new JSONStringer();

        jsonStringer.object()
                    .key("number").value(initialHCs.size())
                    .endObject();

        try {
            this.httpExporter.sendSummaryInitialHCsRequest(scene().getIdentifier(),jsonStringer.toString());
        } catch (UnsupportedEncodingException e) {
            // todo, json stringer returns wrong format, this should not happen!!
        }

    }

    private void exportGrammar() throws IOException {

        logger.info("Exporting grammar for report...");

        // Generate JSON files
        GrammarExporter exporter = new JsonGrammarExporter();
        exporter.exportForReport(scene().getIdentifier(), this.httpExporter,
                getPhase(GrammarTransformer.class).getGrammar());

        logger.info("done. Grammar for report exported." );
    }

    private void exportStateSpace() throws IOException {

        logger.info("Exporting state space for report...");

        StateSpaceExporter exporter = new JsonStateSpaceExporter();
        this.httpExporter.sendStateSpaceSummaryRequest(scene().getIdentifier(), exporter.exportForReport(stateSpace, program));

        Set<ProgramState> states = stateSpace.getStates();

        HeapConfigurationExporter hcExporter = new JsonHeapConfigurationExporter();
        for (ProgramState state : states) {
            int i = state.getStateSpaceId();
            this.httpExporter.sendStateSpaceHCRequest(scene().getIdentifier(), i, hcExporter.exportForReport(state.getHeap()));
        }

        logger.info("done. State space for report exported."
        );
    }

    private void exportSummary() throws IOException{

        logger.info("Exporting analysis summary for report...");

        SummaryExporter exporter = new JSONSummaryExporter(this.httpExporter);
        exporter.exportForReport(scene(), stateSpace, (ModelCheckingPhase) getPhase(ModelCheckingResultsTransformer.class), getPhase(MCSettingsTransformer.class).getMcSettings(),(CLIPhase) getPhase(CLIPhase.class), phases);

        logger.info("done. Analysis summary for report exported."
        );
    }

}
