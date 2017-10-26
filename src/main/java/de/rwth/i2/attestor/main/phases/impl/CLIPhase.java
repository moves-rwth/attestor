package de.rwth.i2.attestor.main.phases.impl;

import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.settings.CommandLineReader;
import de.rwth.i2.attestor.main.settings.SettingsFileReader;

public class CLIPhase extends AbstractPhase {

    private String[] args;

    public CLIPhase(String[] args) {

        this.args = args;
    }

    @Override
    public String getName() {

        return "Command line interface";
    }

    @Override
    protected void executePhase() {

        CommandLineReader commandLineReader = new CommandLineReader();
        commandLineReader.setupCLI();
        if(!commandLineReader.loadSettings(args)) {
            commandLineReader.printHelp();
            throw new IllegalArgumentException(commandLineReader.getParsingError());
        }
        if( commandLineReader.hasSettingsFile() ){
            SettingsFileReader settingsReader =
                    new SettingsFileReader(  commandLineReader.getPathToSettingsFile() );
            settingsReader.getInputSettings( settings );
            settingsReader.getOptionSettings( settings );
            settingsReader.getOutputSettings( settings );
            settingsReader.getMCSettings( settings );
        }
        commandLineReader.getInputSettings(  settings );
        commandLineReader.getOptionSettings( settings );
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
