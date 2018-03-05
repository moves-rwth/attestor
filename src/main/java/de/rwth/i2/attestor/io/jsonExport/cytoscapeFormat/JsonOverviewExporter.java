package de.rwth.i2.attestor.io.jsonExport.cytoscapeFormat;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.PhaseRegistry;
import de.rwth.i2.attestor.phases.modelChecking.modelChecker.ModelCheckingResult;
import de.rwth.i2.attestor.phases.transformers.ModelCheckingResultsTransformer;
import org.json.JSONWriter;

import java.io.Writer;
import java.util.Locale;
import java.util.Map;


public class JsonOverviewExporter {

    private Writer writer;

    public JsonOverviewExporter(Writer writer) {

        this.writer = writer;
    }

    public void export(PhaseRegistry registry) {

        JSONWriter jsonWriter = new JSONWriter(writer);
        jsonWriter.object()
                .key("elements")
                .object()
                .key("verification")
                .array();
        exportVerificationResults(jsonWriter, registry.getMostRecentPhase(ModelCheckingResultsTransformer.class));
        jsonWriter.endArray();
        exportRuntimes(jsonWriter, registry);
        jsonWriter.endObject().endObject();

    }

    private void exportVerificationResults(JSONWriter jsonWriter, ModelCheckingResultsTransformer transformer) {

        Map<LTLFormula, ModelCheckingResult> results = transformer.getLTLResults();

        for(Map.Entry<LTLFormula, ModelCheckingResult> entry : results.entrySet()) {

            String status;
            switch (entry.getValue()) {
                case SATISFIED:
                    status = "valid";
                    break;
                case UNSATISFIED:
                    status = "invalid";
                    break;
                default:
                    status = "unknown";
                    break;
            }

            jsonWriter.object()
                    .key("result")
                    .object()
                    .key("formula")
                    .value(entry.getKey().getFormulaString())
                    .key("status")
                    .value(status)
                    .endObject()
                    .endObject();
        }
    }

    private void exportRuntimes(JSONWriter jsonWriter, PhaseRegistry registry) {

        double elapsedVerify = 0;
        double elapsedTotal = 0;


        jsonWriter.key("runtimes").array();

        for(AbstractPhase phase : registry.getPhases()) {
            double elapsed = round(phase.getElapsedTime());
            elapsedTotal += elapsed;

            jsonWriter.object()
                    .key("phase")
                    .object()
                    .key("name")
                    .value(phase.getName())
                    .key("time")
                    .value(format(elapsed))
                    .endObject()
                    .endObject();

            if (phase.isVerificationPhase()) {
                elapsedVerify += elapsed;
            }
        }

        jsonWriter.endArray()
                .key("verificationTime")
                .value(format(elapsedVerify))
                .key("totalTime")
                .value(format(elapsedTotal));
    }

    private String format(double value) {

        return String.format(Locale.ROOT, "%.3f", value);
    }

    private double round(double value) {

        if(value < 0) {
            return 0.0;
        }
        return Math.round(value * 1000.0) / 1000.0;
    }

}
