package de.rwth.i2.attestor.main.phases.impl;

import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.phases.communication.OutputSettings;
import de.rwth.i2.attestor.main.phases.transformers.OutputSettingsTransformer;
import de.rwth.i2.attestor.main.scene.Scene;

/**
 * Created by christina on 01.12.17.
 *
 * Takes care of producing the necessary output files for the report.
 */
public class ReportOutputPhase extends AbstractPhase {

    private OutputSettings outputSettings;


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
    protected void executePhase() {

        outputSettings = getPhase(OutputSettingsTransformer.class).getOutputSettings();

        // TODO!!!

        // Export
    }
}
