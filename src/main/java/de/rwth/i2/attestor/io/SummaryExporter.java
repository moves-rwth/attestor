package de.rwth.i2.attestor.io;

import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.communication.ModelCheckingSettings;
import de.rwth.i2.attestor.phases.modelChecking.ModelCheckingPhase;
import de.rwth.i2.attestor.phases.parser.CLIPhase;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;

import java.util.List;

/**
 * Created by christina on 05.12.17.
 */
public interface SummaryExporter {

    void exportForReport(Scene scene, StateSpace spatespace, ModelCheckingPhase mcPhase, ModelCheckingSettings mcSetting, CLIPhase cliPhase, List<AbstractPhase> phases);
}
