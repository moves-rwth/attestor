package de.rwth.i2.attestor.io.settings;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.main.scene.Options;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.phases.communication.InputSettings;
import de.rwth.i2.attestor.phases.communication.ModelCheckingSettings;
import de.rwth.i2.attestor.phases.communication.OutputSettings;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.Level;
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
                        .longOpt("communication-file")
                        .hasArg()
                        .argName("path")
                        .desc("file that containsSubsumingState the communication to be executed."
                                + "Can be overwritten by additional command line communication")
                        .build()
        );

        cliOptions.addOption(
                Option.builder("m")
                        .longOpt("method")
                        .hasArg()
                        .argName("method name")
                        .desc("name of entry method - e.g. 'main'")
                        .build()
        );


        cliOptions.addOption(
                Option.builder("v")
                        .longOpt("verbose")
                        .desc("(optional) enable more verbose output")
                        .build()
        );

        cliOptions.addOption(
                Option.builder("ad")
                        .longOpt("depth")
                        .desc("(optional) sets the abstraction distance (default is "
                                + scene().options().getAbstractionDistance() + ")")
                        .hasArg()
                        .argName("int")
                        .build()
        );

        cliOptions.addOption(
                Option.builder("msp")
                        .longOpt("maxStateSpace")
                        .desc("(optional) stops the analysis if the generated state space is larger than specified (default is "
                                + scene().options().getMaxStateSpaceSize() + ")")
                        .hasArg()
                        .argName("int")
                        .build()
        );

        cliOptions.addOption(
                Option.builder("mh")
                        .longOpt("maxHeap")
                        .desc("(optional) stops the analysis if a graph larger than specified is encountered (default is "
                                + scene().options().getMaxStateSize() + ")")
                        .hasArg()
                        .argName("int")
                        .build()
        );

        cliOptions.addOption(
                Option.builder("html")
                        .longOpt("export-to-html")
                        .desc("(optional) exports generated state space to explorable HTML files (default is false)")
                        .build()
        );

        cliOptions.addOption(
                Option.builder("ghtml")
                        .longOpt("grammar-to-html")
                        .desc("(optional) exports parsed grammar to explorable HTML files (default is false)")
                        .build()
        );

        cliOptions.addOption(
                Option.builder("mc")
                        .longOpt("model-checking")
                        .hasArg()
                        .argName("formulae")
                        .desc("(optional) if enabled, model checking will be performed for the provided formulae +"
                                + "(separated by ,)")
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
     * @return true if and only if a communication file has been provided in the command line arguments.
     */
    public boolean hasSettingsFile() {

        return cmd.hasOption("sf");
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

        if (cmd.hasOption("ne")) {
            outputSettings.setNoExport(true);
        }
    }

    /**
     * Populates all communication that customize how the analysis is performed
     * with data extracted from the command line arguments.
     *
     * @param options All options.
     */
    public void updateOptions(Options options) {

        if (cmd.hasOption("ad")) {
            options.setAbstractionDistance(Integer.valueOf(cmd.getOptionValue("ad")));
        }

        if (cmd.hasOption("msp")) {
            options.setMaxStateSpaceSize(Integer.valueOf(cmd.getOptionValue("msp")));
        }

        if (cmd.hasOption("mh")) {
            options.setMaxStateSize(Integer.valueOf(cmd.getOptionValue("mh")));
        }
    }

    /**
     * Populates all communication that customize which input files are loaded
     * with data extracted from the command line arguments.
     *
     * @param settings All input communication.
     * @return The populated input communication.
     */
    public void getInputSettings(InputSettings settings) {

        if (cmd.hasOption("m")) {
            settings.setMethodName(cmd.getOptionValue("m"));
        }
    }

    /**
     * Populates all communication that customize if and how model checking is performed.
     *
     * @param mcSettings All communication.
     */
    public void getMCSettings(ModelCheckingSettings mcSettings) {

        if (cmd.hasOption("mc")) {
            mcSettings.setModelCheckingEnabled(true);
            String formulaString = cmd.getOptionValue("mc");
            for (String formula : formulaString.split(",")) {
                LTLFormula ltlFormula;
                try {
                    ltlFormula = new LTLFormula(formula);
                    mcSettings.addFormula(ltlFormula);
                } catch (Exception e) {
                    logger.log(Level.WARN, "The input " + formula + " is not a valid LTL formula. Skipping it.");
                }
            }
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
