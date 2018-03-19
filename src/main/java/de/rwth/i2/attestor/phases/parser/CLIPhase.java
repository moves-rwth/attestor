package de.rwth.i2.attestor.phases.parser;

import de.rwth.i2.attestor.io.settings.CommandLineReader;
import de.rwth.i2.attestor.io.settings.SettingsFileReader;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.scene.DefaultScene;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.communication.InputSettings;
import de.rwth.i2.attestor.phases.communication.ModelCheckingSettings;
import de.rwth.i2.attestor.phases.communication.OutputSettings;
import de.rwth.i2.attestor.phases.transformers.InputSettingsTransformer;
import de.rwth.i2.attestor.phases.transformers.MCSettingsTransformer;
import de.rwth.i2.attestor.phases.transformers.OutputSettingsTransformer;
import de.rwth.i2.attestor.phases.transformers.StateLabelingStrategyBuilderTransformer;
import de.rwth.i2.attestor.refinement.AutomatonStateLabelingStrategyBuilder;

import java.util.Objects;

public class CLIPhase extends AbstractPhase
        implements InputSettingsTransformer, OutputSettingsTransformer,
        MCSettingsTransformer, StateLabelingStrategyBuilderTransformer {

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
    public void executePhase() {

        CommandLineReader commandLineReader = new CommandLineReader(this);
        commandLineReader.setupCLI();
        if (!commandLineReader.loadSettings(args)) {
            commandLineReader.printHelp();
            throw new IllegalArgumentException(commandLineReader.getParsingError());
        }
        SettingsFileReader settingsReader = new SettingsFileReader(commandLineReader.getPathToSettingsFile());
        settingsReader.getInputSettings(inputSettings);
        inputSettings.setPathToSettingsFile(commandLineReader.getPathToSettingsFile());
        DefaultScene defaultScene = (DefaultScene) scene();
        defaultScene.setIdentifier(Objects.hashCode(commandLineReader.getPathToSettingsFile()));
        settingsReader.getOptionSettings(scene().abstractionOptions());
        settingsReader.getOutputSettings(outputSettings);
        settingsReader.getMCSettings(modelCheckingSettings);
        commandLineReader.getOutputSettings(outputSettings);

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

        String name = inputSettings.getName();
        if(name != null && !name.isEmpty()) {
            logSum("Benchmark name: " + name);
        } else {
            logSum("Benchmark name: n/a");
        }

        String specificationDescription = inputSettings.getSpecificationDescription();
        if(specificationDescription != null && !specificationDescription.isEmpty()) {
            logSum("Specification summary: " + specificationDescription);
        } else {
            logSum("Specification summary: n/a");
        }


        String scenario = inputSettings.getScenario();
        if (scenario != null && !scenario.isEmpty()) {
            logSum("Scenario: " + scenario);
        } else {
            logSum("Scenario: n/a");
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

    @Override
    public AutomatonStateLabelingStrategyBuilder getStrategy() {
        return null;
    }
}
