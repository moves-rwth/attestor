package de.rwth.i2.attestor.main.phases.impl;

import de.rwth.i2.attestor.grammar.GrammarExporter;
import de.rwth.i2.attestor.io.jsonExport.cytoscapeFormat.JsonGrammarExporter;
import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.phases.communication.OutputSettings;
import de.rwth.i2.attestor.main.phases.transformers.GrammarTransformer;
import de.rwth.i2.attestor.main.phases.transformers.OutputSettingsTransformer;
import de.rwth.i2.attestor.main.phases.transformers.ProgramTransformer;
import de.rwth.i2.attestor.main.phases.transformers.StateSpaceTransformer;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.util.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by christina on 01.12.17.
 *
 * Takes care of producing the necessary output files for the report.
 */
public class ReportOutputPhase extends AbstractPhase {

    private OutputSettings outputSettings;

    private StateSpace stateSpace;
    //private Program program;


    public ReportOutputPhase(Scene scene) {

        super(scene);
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
        //program = getPhase(ProgramTransformer.class).getProgram();

        String outputDirectory = outputSettings.getFolderForReportOutput();

        // Export analysis summary

        // Export grammar (without preceding elements!!)
        exportGrammar(outputDirectory);



        // Export state space (without preceding elements!!)
    }

    private void exportGrammar(String location) throws IOException {

        logger.info("Exporting grammar for report...");

        // Generate JSON files
        GrammarExporter exporter = new JsonGrammarExporter();
        exporter.exportForReport(location + File.separator + "grammarData",
                getPhase(GrammarTransformer.class).getGrammar());

        logger.info("done. Grammar exported to '" + location + File.separator + "grammarData" + "'");
    }
}
