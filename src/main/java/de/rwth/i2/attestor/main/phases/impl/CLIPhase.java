package de.rwth.i2.attestor.main.phases.impl;

import de.rwth.i2.attestor.io.settings.CommandLineReader;
import de.rwth.i2.attestor.io.settings.SettingsFileReader;
import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.phases.communication.InputSettings;
import de.rwth.i2.attestor.main.phases.communication.ModelCheckingSettings;
import de.rwth.i2.attestor.main.phases.communication.OutputSettings;
import de.rwth.i2.attestor.main.phases.transformers.InputSettingsTransformer;
import de.rwth.i2.attestor.main.phases.transformers.MCSettingsTransformer;
import de.rwth.i2.attestor.main.phases.transformers.OutputSettingsTransformer;
import de.rwth.i2.attestor.main.scene.Scene;

public class CLIPhase extends AbstractPhase
        implements InputSettingsTransformer, OutputSettingsTransformer, MCSettingsTransformer {

    private final String[] args;

    private final InputSettings inputSettings = new InputSettings();
    private final OutputSettings outputSettings = new OutputSettings();
    private final ModelCheckingSettings modelCheckingSettings = new ModelCheckingSettings();

    public CLIPhase(Scene scene, String[] args) {

        super(scene);
        this.args = args;
    }

    @Override
    public String getName() {

        return "Command line interface";
    }

    @Override
    protected void executePhase() {

        CommandLineReader commandLineReader = new CommandLineReader(this);
        commandLineReader.setupCLI();
        if (!commandLineReader.loadSettings(args)) {
            commandLineReader.printHelp();
            throw new IllegalArgumentException(commandLineReader.getParsingError());
        }
        if (commandLineReader.hasSettingsFile()) {
            SettingsFileReader settingsReader =
                    new SettingsFileReader(commandLineReader.getPathToSettingsFile());
            settingsReader.getInputSettings(inputSettings);
            settingsReader.getOptionSettings(scene().options());
            settingsReader.getOutputSettings(outputSettings);
            settingsReader.getMCSettings(modelCheckingSettings);
        }
        commandLineReader.getInputSettings(inputSettings);
        commandLineReader.updateOptions(scene().options());
        commandLineReader.getOutputSettings(outputSettings);
        commandLineReader.getMCSettings(modelCheckingSettings);

        if (commandLineReader.hasRootPath()) {
            String rootPath = commandLineReader.getRootPath();
            inputSettings.setRootPath(rootPath);
            outputSettings.setRootPath(rootPath);
        }
    }

    @Override
    public void logSummary() {

        logHighlight("Analyzed method: "
                + inputSettings.getClasspath()
                + "/"
                + inputSettings.getClassName()
                + "."
                + inputSettings.getMethodName()
        );

        String scenario = inputSettings.getScenario();
        if(scenario != null && !scenario.isEmpty()) {
            logSum("Scenario: " + scenario);
        } else {
            logSum("Scenario: not specified");
        }

    }

    @Override
    public boolean isVerificationPhase() {

        return false;
    }

    @Override
    public InputSettings getInputSettings() {

        return inputSettings;
    }

    @Override
    public ModelCheckingSettings getMcSettings() {

        return modelCheckingSettings;
    }

    @Override
    public OutputSettings getOutputSettings() {

        return outputSettings;
    }
}
