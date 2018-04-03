package de.rwth.i2.attestor.phases.commandLineInterface;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CommandLineReader {

    private static Logger logger = LogManager.getLogger("CommandLineReader");
    private Options commandLineOptions = new Options();


    public CommandLineReader() {

        setupGeneralOptions();
        setupInputOptions();
        setupAbstractionOptions();
        setupAnalysisOptions();
        setupExportOptions();
        setupLoggerOptions();
    }

    public CommandLine read(String[] args) {

        List<String> allArguments;
        try {
            allArguments = loadSettings(args);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read settings file (" + e.getMessage() + ").");
        }

        CommandLineParser parser = new DefaultParser();
        try {
            String[] allArgs = allArguments.toArray(new String[allArguments.size()]);
            return parser.parse(commandLineOptions, allArgs);
        } catch (ParseException | NumberFormatException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private List<String> loadSettings(String[] args) throws IOException {

        String rootPath = "";
        String settingsFile = "";

        List<String> argumentList = new ArrayList<>();

        // We only search for --root-path and --read.
        // Since both options require an argument, we only
        // move up to args.length-1.
        for(int i=0; i < args.length; i++) {

            String argument = args[i];
            switch(argument) {
                case "-l":
                case "--read":
                    if(!settingsFile.isEmpty()) {
                        logger.warn("Overwriting previously set settings file.");
                    }
                    settingsFile = args[i + 1];
                    ++i;
                    break;
                case "-rp":
                case "--root-path":
                    if(!rootPath.isEmpty()) {
                        logger.warn("Overwriting previously set root path.");
                    }
                    argumentList.add(argument);
                    rootPath = args[i + 1];
                    argumentList.add(rootPath);
                    ++i;
                    break;
                default:
                    argumentList.add(argument);
                    break;
            }
        }

        if(settingsFile.isEmpty()) {
            return argumentList;
        } else {
            Path path;
            if(rootPath.isEmpty()) {
                path = Paths.get(settingsFile);
            } else {
                path = Paths.get(rootPath + File.separator + settingsFile);
            }
            logger.info("loading settings file: " + path);
            List<String> lines = Files.readAllLines(path);
            SettingsLexer lexer = new SettingsLexer(lines);
            List<String> result = lexer.getLexemes();
            result.addAll(argumentList);
            return result;
        }
    }

    public void printHelp() {

        CommandLineReader options = new CommandLineReader();
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "java -jar Attestor", options.commandLineOptions);
    }


    private void setupGeneralOptions() {

        commandLineOptions.addOption(
                Option.builder("d")
                        .longOpt("description")
                        .hasArg()
                        .argName("text")
                        .desc("Optionally provides a brief textual " +
                                "description of the specified analysis.")
                        .build()
        );

        commandLineOptions.addOption(
                Option.builder("l")
                        .longOpt("read")
                        .hasArg()
                        .argName("file")
                        .desc("Additionally loads all command line options that are contained in the " +
                                "supplied settings file. A settings file may contain all command line options " +
                                "presented on this page except for --read. In case of conflicting options, " +
                                "explicitly provided command line options have precedence over options " +
                                "in the settings file. If --root-path is set then the common root path" +
                                " is added as a prefix to the classpath. " +
                                "Notice that loaded files may not contain the --read option again.")
                        .build()
        );

        commandLineOptions.addOption(
                Option.builder("rp")
                        .longOpt("root-path")
                        .hasArg()
                        .argName("path")
                        .desc("Determines the provided path as a common prefix for all other paths provided " +
                                "in command line options. More precisely, affected options whose arguments are " +
                                "concatenated with prefix <path> are: \n" +
                                "* --read\n" +
                                "* --classpath\n" +
                                "* --grammar\n" +
                                "* --initial\n" +
                                "* --contract\n" +
                                "If option --root-path is not explicitly, the root path is set to the empty string.")
                        .build()
        );

    }

    private void setupInputOptions() {

        commandLineOptions.addOption(
                Option.builder("c")
                        .longOpt("class")
                        .required()
                        .hasArg()
                        .argName("classname")
                        .desc("Determines the class containing the top-level method should be analyzed. " +
                                "The actually analyzed method is set via --method")
                        .build()
        );

        commandLineOptions.addOption(
                Option.builder("cp")
                        .longOpt("classpath")
                        .required()
                        .hasArg()
                        .argName("classpath")
                        .desc("Determines the path to the classes that should be analyzed. " +
                                "If --root-path is set then the common root path is added " +
                                "as a prefix to the classpath.")
                        .build()
        );

        commandLineOptions.addOption(
                Option.builder()
                        .longOpt("contract")
                        .hasArg()
                        .argName("file")
                        .desc("Loads a user-supplied contract from the provided file that can be directly applied " +
                                "if the corresponding method is encountered. " +
                                "Please confer syntax for contract files for further details on manually" +
                                " writing contract files. " +
                                "If --root-path is set then the common root path is added " +
                                "as a prefix to the contract file.")
                        .build()
        );

        commandLineOptions.addOption(
                Option.builder("g")
                        .longOpt("grammar")
                        .hasArg()
                        .argName("file")
                        .desc("Loads a user-supplied graph grammar from the provided <file>." +
                                "Please confer syntax for graph grammars for further details on " +
                                "writing custom graph grammars. " +
                                "If --root-path is set then the common root path is added " +
                                "as a prefix to the grammar file.")
                        .build()
        );

        commandLineOptions.addOption(
                Option.builder("i")
                        .longOpt("initial")
                        .hasArg()
                        .argName("file")
                        .desc("Determines the heap of the initial state as the heap configuration provided in <file>." +
                                " If no initial heap is provided, the analysis assumes an initially empty heap. " +
                                "Please confer syntax for heap configurations for further details on manually " +
                                "specifying heap configurations. " +
                                "If --root-path is set then the common root path is added" +
                                " as a prefix to the initial heap file.")
                        .build()
        );

        commandLineOptions.addOption(
                Option.builder("m")
                        .longOpt("method")
                        .hasArg()
                        .argName("name")
                        .desc("Sets the name of the top-level method in the class determined by --class " +
                                "that should be analyzed. " +
                                "Notice that the method must be uniquely identifiable by its name " +
                                "without parameters into account. " +
                                "If the supplied method has parameters, it is recommended to " +
                                "also supply a suitable initial state via --initial.")
                        .build()
        );

        commandLineOptions.addOption(
                Option.builder("pg")
                        .longOpt("predefined-grammar")
                        .hasArg()
                        .argName("name")
                        .desc("Adds a predefined graph grammar with the provided name to the grammars " +
                                "used in the analysis. " +
                                "The fixed node type and selector names can be renamed using --rename " +
                                "Please confer the list of predefined data structures for further details " +
                                "on available predefined graph grammars.")
                        .build()
        );

        commandLineOptions.addOption(
                Option.builder("r")
                        .longOpt("rename")
                        .hasArgs()
                        .numberOfArgs(Option.UNLIMITED_VALUES)
                        .argName("...")
                        .desc("Renames the provided class, i.e. node type, together with the selectors " +
                                "of the specified class. The exact arguments are of the form " +
                                "oldClassname=newClassname oldSelector1=newSelector1 ...")
                        .build()
        );
    }

    private void setupAbstractionOptions() {

        commandLineOptions.addOption(
                Option.builder("a")
                        .longOpt("admissible-abstraction")
                        .desc("Discards certain abstractions to establish a weak version of admissibility: " +
                                "If a node, say u, has an attached variable then u may not belong to the nodes of " +
                                "an embedding used for abstraction. The same holds for base markings, which are " +
                                "treated like variables. Constants and composed markings are not considered unless " +
                                "the options --admissible-constants and --admissible-markings are set, respectively.")
                        .build()
        );

        commandLineOptions.addOption(
                Option.builder()
                        .longOpt("admissible-constants")
                        .desc("If --admissible-abstraction is set then this option treats constants " +
                                "the same as variables when checking whether an abstraction violates admissibility. " +
                                "Otherwise, this option has no effect.")
                        .build()
        );

        commandLineOptions.addOption(
                Option.builder()
                        .longOpt("admissible-markings")
                        .desc("If --admissible-abstraction is set then this option treats composed markings the" +
                                " same as variables when checking whether an abstraction violates admissibility. " +
                                "Otherwise, this option has no effect.")
                        .build()
        );

        commandLineOptions.addOption(
                Option.builder()
                        .longOpt("admissible-full")
                        .desc("Enforces that all program states are admissible before they are added to " +
                                "the state space. This is achieved by performing additional materialization steps. " +
                                "Notice that this option does not influence how abstraction is performed.")
                        .build()
        );

        commandLineOptions.addOption(
                Option.builder()
                        .longOpt("no-chain-abstraction")
                        .desc("By default, program states in a chain, i.e. a sequence of program states with exactly " +
                                "one predecessor and exactly one successor that do not require immediate abstraction " +
                                "(e.g. return, procedure calls, etc.) are not abstracted individually, " +
                                "but just inserted into the state space as is. This is an optimization to avoid " +
                                "unnecessary expensive computations of abstractions. " +
                                "If this option is enabled, however, all program states will be abstracted before " +
                                "they are added to the state space.")
                        .build()
        );

        commandLineOptions.addOption(
                Option.builder()
                        .longOpt("no-rule-collapsing")
                        .desc("By default, an embedding computed during abstraction may map multiple external nodes " +
                                "of a rule to the same node in a given heap configuration. " +
                                "This option enforces that external nodes always refer to different nodes.")
                        .build()
        );

        commandLineOptions.addOption(
                Option.builder()
                        .longOpt("indexed")
                        .desc("If one or more of the supplied graph grammars is indexed then this option enables " +
                                "the use of this index for abstraction and materialization.")
                        .build()
        );

        commandLineOptions.addOption(
                Option.builder()
                        .longOpt("post-processing")
                        .desc("If --admissible-abstraction is set then this option applies a more aggressive " +
                                "abstraction that may violate admissibility to all final states encountered during " +
                                "state space generation in order to reduce the total number of final states. " +
                                "Otherwise, this option has no effect.")
                        .build()
        );

        commandLineOptions.addOption(
                Option.builder()
                        .longOpt("canonical")
                        .desc("Enforces that every program states in generated state spaces is a canonical state," +
                                " i.e. its heap language is disjoint from the heap language of every other program " +
                                "state in the state space. " +
                                "This option disables various optimizations and will thus typically result in a " +
                                "larger state space generation time. Option --canonical has to be switched on, " +
                                "however, if detected counterexamples should be checked for spuriousity. " +
                                "If --admissible-abstraction is set then --canonical is a shortcut for\n" +
                                "* --admissible-full,\n" +
                                "* --admissible-constants,\n" +
                                "* --admissible-markings,\n" +
                                "* --no-chain-abstraction, and\n" +
                                "it is incompatible with --post-processing.\n" +
                                "Otherwise, option --canonical has the same effect as --no-chain-abstraction.")
                        .build()
        );

    }

    private void setupAnalysisOptions() {

        commandLineOptions.addOption(
                Option.builder("mc")
                        .longOpt("model-checking")
                        .hasArg()
                        .argName("formula")
                        .desc("Adds a specification in linear temporal logic that Attestor attempts to " +
                                "verify for the generated state space. If the specification does not hold, " +
                                "a counterexample is provided. " +
                                "Further details regarding the syntax of specifications and supported atomic " +
                                "propositions are found in the LTL specifications. " +
                                "Please note that the atomic propositions used within the supplied specification " +
                                "influences state space generation due to additional time required to " +
                                "compute labels and necessary grammar refinement.")
                        .build()
        );

        commandLineOptions.addOption(
                Option.builder("ngc")
                        .longOpt("no-garbage-collector")
                        .desc("Disables the counterpart to Java's garbage collector in the " +
                                "concrete semantics and abstract semantics.")
                        .build()
        );

        commandLineOptions.addOption(
                Option.builder("ms")
                        .longOpt("max-state-space")
                        .hasArg()
                        .argName("integer")
                        .desc("Determines the maximal number of program states to be encountered within a single" +
                                " state space generation until the analysis is aborted. " +
                                "By default, the maximal number of program states is set to 5000.")
                        .build()
        );


        commandLineOptions.addOption(
                Option.builder("mh")
                        .longOpt("max-heap")
                        .hasArg()
                        .argName("integer")
                        .type(Integer.class)
                        .desc("Determines the maximal number of nodes encountered within the heap configuration " +
                                "of any program state until the analysis is aborted. " +
                                "By default, the maximal number of nodes is set to 50.")
                        .build()
        );

    }

    private void setupExportOptions() {

        commandLineOptions.addOption(
                Option.builder("e")
                        .longOpt("export")
                        .hasArg()
                        .argName("path")
                        .desc("Exports a report that allows to graphically explore the generated state space. " +
                                "The exported report is written to a directory ROOT_PATH/<path>, where ROOT_PATH is " +
                                "the path determined by --root-path.")
                        .build()
        );

        commandLineOptions.addOption(
                Option.builder()
                        .longOpt("export-grammar")
                        .hasArg()
                        .argName("path")
                        .desc("Exports the graph grammars used within the analysis. " +
                                "The exported grammar is written to a directory ROOT_PATH/<path>, where ROOT_PATH is" +
                                " the path determined by --root-path.")
                        .build()
        );

        commandLineOptions.addOption(
                Option.builder()
                        .longOpt("export-large-states")
                        .hasArg()
                        .argName("path")
                        .desc("Exports only the program states that exceed the threshold determined by --max-heap. " +
                                "The exported states is written to a directory ROOT_PATH/<path>, " +
                                "where ROOT_PATH is the path determined by --root-path.")
                        .build()
        );

        commandLineOptions.addOption(
                Option.builder()
                        .longOpt("export-contracts")
                        .hasArg()
                        .argName("path")
                        .desc("Exports the contracts generated for the provided method for graphical inspection. " +
                                "The exported contracts are written to a directory ROOT_PATH/<path>, where ROOT_PATH is " +
                                "the path determined by --root-path. " +
                                "If the generated contracts should be reused for another analysis, " +
                                "i.e. they should be supplied using --contract (link), use the option " +
                                "--save-contracts instead.")
                        .build()
        );

        commandLineOptions.addOption(
                Option.builder()
                        .longOpt("save-contracts")
                        .hasArg()
                        .argName("path")
                        .desc("Exports all generated contracts for graphical inspection. " +
                                "The exported contracts are written to a directory ROOT_PATH/<path>, " +
                                "where ROOT_PATH is the path determined by --root-path. " +
                                "If the generated contracts should be reused for another analysis, " +
                                "i.e. they should be supplied using --contract (link), use the option " +
                                "--save-contracts instead.")
                        .build()
        );
    }

    private void setupLoggerOptions() {

        OptionGroup debugOptions = new OptionGroup();

        debugOptions.addOption(
                Option.builder("q")
                        .longOpt("quiet")
                        .build()
        );

        debugOptions.addOption(
                Option.builder("v")
                        .longOpt("verbose")
                        .build()
        );

        debugOptions.addOption(
                Option.builder()
                        .longOpt("debug")
                        .build()
        );

        debugOptions.addOption(
                Option.builder()
                        .longOpt("christoph")
                        .build()
        );

        commandLineOptions.addOptionGroup(debugOptions);
    }
}
