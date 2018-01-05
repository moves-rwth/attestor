package de.rwth.i2.attestor.io.jsonExport.report;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.io.SummaryExporter;
import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.phases.communication.ModelCheckingSettings;
import de.rwth.i2.attestor.main.phases.parser.CLIPhase;
import de.rwth.i2.attestor.main.phases.modelChecking.ModelCheckingPhase;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import org.json.JSONWriter;

import java.io.Writer;
import java.util.List;
import java.util.Map;

/**
 * Created by christina on 05.12.17.
 */
public class JSONSummaryExporter implements SummaryExporter {


    protected final Writer writer;

    public JSONSummaryExporter(Writer writer) {

        this.writer = writer;
    }

    public void exportForReport(Scene scene, StateSpace statespace, ModelCheckingPhase mcPhase, ModelCheckingSettings mcSettings, CLIPhase cliPhase, List<AbstractPhase> phases){

        JSONWriter jsonWriter = new JSONWriter(writer);

        jsonWriter.array();

        writeSummary(jsonWriter, cliPhase, scene, statespace, mcPhase, mcSettings);

        writeRuntime(jsonWriter, phases);

        writeStateSpaceInfo(jsonWriter, scene, statespace);

        writeMessage(jsonWriter);

        writeMCResults(jsonWriter, mcPhase);

        jsonWriter.endArray();

    }

    private void writeMCResults(JSONWriter jsonWriter, ModelCheckingPhase mcPhase) {
        jsonWriter.object()
                .key("mcresults")
                .array();

        for (Map.Entry<LTLFormula, Boolean> result : mcPhase.getLTLResults().entrySet()) {
            jsonWriter.object()
                    .key("formula")
                    .value(result.getKey().getFormulaString())
                    .key("satisfied")
                    .value(result.getValue().toString())
                    .endObject();
        }


        jsonWriter.endArray()
                .endObject();
    }

    private void writeMessage(JSONWriter jsonWriter) {

        jsonWriter.object()
                .key("messages")
                .array()
                .endArray()
                .endObject();
    }

    private void writeStateSpaceInfo(JSONWriter jsonWriter, Scene scene, StateSpace statespace) {
        jsonWriter.object()
                .key("stateSpace")
                .array()
                .object()
                .key("name")
                .value("w/ procedure calls")
                .key("states")
                .value(scene.getNumberOfGeneratedStates())
                .endObject()
                .object()
                .key("name")
                .value("w/o procedure calls")
                .key("states")
                .value(statespace.getStates().size())
                .endObject()
                .object()
                .key("name")
                .value("final states")
                .key("states")
                .value(statespace.getFinalStateIds().size())
                .endObject()
                .endArray()
                .endObject();

    }

    private void writeRuntime(JSONWriter jsonWriter, List<AbstractPhase> phases) {

        jsonWriter.object()
                .key("runtime")
                .array()
                .object()
                .key("phases")
                .array();

        int elapsedTotal = 0;
        int elapsedVerify = 0;
        // Generate JSON output and calculate total time
        for (AbstractPhase p : phases) {
            double elapsed = p.getElapsedTime();
            elapsedTotal += elapsed;

            jsonWriter.object()
                    .key("name")
                    .value(p.getName())
                    .key("time")
                    .value(elapsed)
                    .endObject();
            if (p.isVerificationPhase()) {
                elapsedVerify += elapsed;
            }
        }

        jsonWriter.endArray()
                .endObject()
                .object()
                .key("total")
                .array()
                .object()
                .key("name")
                .value("sum")
                .key("time")
                .value(elapsedTotal)
                .endObject()
                .object()
                .key("name")
                .value("sumVerification")
                .key("time")
                .value(elapsedVerify)
                .endObject()
                .endArray()
                .endObject()
                .endArray()
                .endObject();
    }

    private void writeSummary(JSONWriter jsonWriter, CLIPhase cliPhase, Scene scene, StateSpace statespace, ModelCheckingPhase mcPhase, ModelCheckingSettings mcSettings) {
        jsonWriter.object()
                .key("summary")
                .array()
                .object()
                .key("name")
                .value(cliPhase.getInputSettings().getScenario())
                .endObject()
                .object()
                .key("numberStates")
                .value(scene.getNumberOfGeneratedStates())
                .endObject()
                .object()
                .key("numberTerminalStates")
                .value(statespace.getFinalStateIds().size())
                .endObject()
                .object()
                .key("numberFormulaeSuccess")
                .value(mcPhase.getNumberSatFormulae())
                .endObject()
                .object()
                .key("numberFormulaeFail")
                .value(mcSettings.getFormulae().size() - mcPhase.getNumberSatFormulae())

                .endObject()
                .endArray()
                .endObject()
        ;
    }

}
