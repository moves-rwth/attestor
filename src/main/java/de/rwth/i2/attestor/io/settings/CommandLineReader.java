package de.rwth.i2.attestor.io.settings;

import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.phases.communication.OutputSettings;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * @author Hannah Arndt, Christoph, Christina
 */
public class CommandLineReader extends SceneObject {

    /**
     * The logger of this class.
     */
    private static final Logger logger = LogManager.getLogger("CommandLineReader");

    /**
     * A specification of the available command line options.
     */
    private org.apache.commons.cli.Options cliOptions;

    /**
     * The underlying command line parser.
     */
    private CommandLine cmd;

    private String parsingError;

    public CommandLineReader(SceneObject sceneObject) {

        super(sceneObject);
    }

    /**
     * Initializes the specification of the command line interface.
     * This method should always be called first.
     */
    public void setupCLI() {

        cliOptions = new org.apache.commons.cli.Options();

        cliOptions.addOption(
                Option.builder("ne")
                        .longOpt("no-export")
                        .desc("This option overrides all exports defined in a settings file such that nothing is exported.")
                        .build()
        );

        cliOptions.addOption(
                Option.builder("rp")
                        .longOpt("root-path")
                        .hasArg()
                        .argName("path")
                        .desc("defines a root path for the input. If specified, all other "
                                + "paths are evaluated relative to this path.")
                        .build()
        );

        cliOptions.addOption(
                Option.builder("sf")
                        .longOpt("settings-file")
                        .hasArg()
                        .argName("file")
                        .desc("Loads a settings file that specifies the analysis to be executed.")
                        .build()
        );
    }


    /**
     * Attempts to parse the provided command line arguments and checks whether they specify the specification.
     *
     * @param args The command line arguments.
     * @return true if and only if the provided command line arguments specify the specification.
     */
    public boolean loadSettings(String[] args) {

        CommandLineParser parser = new DefaultParser();

        try {
            cmd = parser.parse(cliOptions, args);

            if (!commandLineIsValid(cmd)) {
                return false;
            }

        } catch (ParseException | NumberFormatException e) {
            parsingError = e.getMessage();
            return false;
        }

        return true;
    }

    public String getParsingError() {

        return parsingError;
    }

    public void printHelp() {

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java Attestor", cliOptions);
    }

    /**
     * @return The path to the communication file provided in the command line arguments.
     */
    public String getPathToSettingsFile() {

        if (hasRootPath()) {
            return getRootPath() + File.separator + cmd.getOptionValue("sf");
        }
        return cmd.getOptionValue("sf");
    }

    /**
     * @return true if and only if a root path has been provided
     */
    public boolean hasRootPath() {

        return cmd.hasOption("rp");
    }

    /**
     * @return The specified root path
     */
    public String getRootPath() {

        return cmd.getOptionValue("rp");
    }

    /**
     * Populates all communication that customize how state spaces are exported
     * with data extracted from the command line arguments.
     *
     * @param outputSettings All output communication.
     */
    public void getOutputSettings(OutputSettings outputSettings) {

        if (cmd.hasOption("ne") || System.getProperty("attestor.ne") != null) {
            outputSettings.setNoExport(true);
        }
    }

    /**
     * Checks whether the provided command line arguments are valid in the sense
     * that a communication file has been provided.
     *
     * @param cmd The parsed command line arguments.
     * @return true if and only if a communication file has been provided.
     */
    private boolean commandLineIsValid(CommandLine cmd) {

        if (!cmd.hasOption("sf")) {
            parsingError = "The mandatory option -sf <path to communication file> is missing.";
            return false;
        }

        if (!cmd.hasOption("rp")) {
            parsingError = "The mandatory option -rp <root path> is missing. " +
                    "You might want to try '-rp .'";
            return false;
        }
        return true;
    }
}
