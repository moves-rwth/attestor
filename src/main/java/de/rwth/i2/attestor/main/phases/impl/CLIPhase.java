package de.rwth.i2.attestor.main.phases.impl;

import de.rwth.i2.attestor.main.environment.Scene;
import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.settings.CommandLineReader;
import de.rwth.i2.attestor.main.settings.SettingsFileReader;

public class CLIPhase extends AbstractPhase {

    private final String[] args;

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
        if(!commandLineReader.loadSettings(args)) {
            commandLineReader.printHelp();
            throw new IllegalArgumentException(commandLineReader.getParsingError());
        }
        if( commandLineReader.hasSettingsFile() ){
            SettingsFileReader settingsReader =
                    new SettingsFileReader(  commandLineReader.getPathToSettingsFile() );
            settingsReader.getInputSettings( settings );
            settingsReader.getOptionSettings( scene().options() );
            settingsReader.getOutputSettings( settings );
            settingsReader.getMCSettings( settings );
        }
        commandLineReader.getInputSettings(  settings );
        commandLineReader.updateOptions( scene().options() );
        commandLineReader.getOutputSettings( settings );
        commandLineReader.getMCSettings( settings );

        if( commandLineReader.hasRootPath() ){
            settings.setRootPath( commandLineReader.getRootPath() );
        }
    }

    @Override
    public void logSummary() {

        logSum("Analysis summary:");
        logSum("+----------------------------------+--------------------------------+");
        logSum("| Method: "
                + settings.input().getClasspath()
                + "/"
                + settings.input().getClassName()
                + "."
                + settings.input().getMethodName()
        );

        String scenario = settings.input().getScenario();
        if(scenario != null && !scenario.isEmpty()) {
            logSum("| Scenario: " + scenario);
        }
        logSum("+----------------------------------+--------------------------------+");

    }

    @Override
    public boolean isVerificationPhase() {

        return false;
    }
}
