package de.rwth.i2.attestor.main.settings;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.generated.lexer.LexerException;
import de.rwth.i2.attestor.generated.parser.ParserException;
import java.io.File;

import org.apache.commons.cli.*;

import de.rwth.i2.attestor.util.DebugMode;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

/**
 * Parses the provided command line options in order to populate
 * {@link Settings}.
 *
 * @author Hannah Arndt, Christoph, Christina
 */
public class CommandLineReader {

	/**
	 * The logger of this class.
	 */
	private static final Logger logger = LogManager.getLogger( "CommandLineReader" );

    /**
     * A specification of the available command line options.
     */
    private Options cliOptions;

    /**
     * The underlying command line parser.
     */
	private CommandLine cmd;


    /**
     * Initializes the specification of the command line interface.
     * This method should always be called first.
     */
	public void setupCLI() {

		cliOptions = new Options();
		
		cliOptions.addOption(
				Option.builder("rp")
				.longOpt("root-path")
				.hasArg()
				.argName("path")
				.desc( "(optional) defines a root path for the input. If specified, all other "
						+ "paths are evaluated relative to this path." )
				.build()
				);
		
		cliOptions.addOption( 
				Option.builder("sf")
				.longOpt( "settings-file" )
				.hasArg()
				.argName( "path" )
				.desc( "file that contains the settings to be executed." 
						+ "Can be overwritten by additional command line settings" )
				.build()
				);
		
		cliOptions.addOption( 
				Option.builder("p")
				.longOpt("defaultpath")
				.hasArg()
				.argName("path")
				.desc("path to class and json files to be analyzed")
				.build()
				);
		
		cliOptions.addOption( 
				Option.builder("c")
				.longOpt("class")
				.hasArg()
				.argName("name")
				.desc("name of a class containing the main method")
				.build()
				);

		cliOptions.addOption(
				Option.builder("cp")
				.longOpt("classpath")
				.hasArg()
				.argName("path")
				.desc("classpath if not identical with defaultpath")
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
				Option.builder("i")
				.longOpt("initial")
				.hasArg()
				.argName("file")
				.desc("(optional) name of file containing initial state in json-format")
				.build()
				);

		cliOptions.addOption(
				Option.builder("ip")
				.longOpt("initial-path")
				.hasArg()
				.argName("path")
				.desc("path to initial state if not identical with defaultpath")
				.build()
				);

		cliOptions.addOption(
				Option.builder("g")
				.longOpt("grammar")
				.hasArg()
				.argName("file")
				.desc("name of file containing grammar in json-format")
				.build()
				);

		cliOptions.addOption(
				Option.builder("gp")
				.longOpt("grammar-path")
				.hasArg()
				.argName("path")
				.desc("path to grammar if not identical with defaultpath")
				.build()
				);


		cliOptions.addOption(
				Option.builder("kd")
				.longOpt("keep-dead-variables")
				.desc("(optional) keep non-live variables in the heap")
				.build()
				);

		cliOptions.addOption(
				Option.builder("x")
				.longOpt("indexed")
				.desc("(otional) use indexed grammars for analysis")
				.build()
				);

		cliOptions.addOption(
				Option.builder("v")
				.longOpt("verbose")
				.desc("(optional) enable more verbose output")
				.build()
				);

		cliOptions.addOption(
				Option.builder("d")
				.longOpt("depth")
				.desc("(optional) sets the dereference depth for canonicalization (default is " 
						+ Settings.getInstance().options().getMinDereferenceDepth() + ")")
				.hasArg()
				.argName("int")
				.build()
				);

		cliOptions.addOption(
				Option.builder("msp")
				.longOpt("maxStateSpace")
				.desc("(optional) stops the analysis if the generated state space is larger than specified (default is " 
						+ Settings.getInstance().options().getMaxStateSpaceSize() + ")")
				.hasArg()
				.argName("int")
				.build()
				);

		cliOptions.addOption(
				Option.builder("mh")
				.longOpt("maxHeap")
				.desc("(optional) stops the analysis if a graph larger than specified is encountered (default is "
						+ Settings.getInstance().options().getMaxStateSize() + ")")
				.hasArg()
				.argName("int")
				.build()
				);

		cliOptions.addOption(
				Option.builder("aggr")
				.longOpt("aggressive-canonization-threshold")
				.hasArg()
				.optionalArg(true)
				.argName("int")
				.desc("(optional) after this threshold canonization will ignore the depth argument (default "
						+ Settings.getInstance().options().getAggressiveAbstractionThreshold() + ")"
						+"(only applicable to indexed analysis)")
				.build()
				);

		cliOptions.addOption(
				Option.builder("ar")
				.longOpt("aggressive-abstraction-at-return")
				.hasArg()
				.argName("boolean")
				.desc("(optional) if enabled, canonization will ignore depth argument on return Statements "
						+ "(enabled by default).")
				.build()
				);

		cliOptions.addOption( 
				Option.builder("o")
				.longOpt( "output-path" )
				.hasArg()
				.argName( "path" )
				.desc( "(optional) set the default path to where the output (e.g. stateSpace, grammar) will be placed (default inputpath)" )
				.build()
				);

		cliOptions.addOption(
				Option.builder("html")
				.longOpt("export-to-html")
				.desc("(optional) exports generated statespace to explorable HTML files (default is false)")
				.build()
				);

		cliOptions.addOption(
				Option.builder("ghtml")
				.longOpt("grammar-to-html")
				.desc("(optional) exports parsed grammar to explorable HTML files (default is false)")
				.build()
				);

		cliOptions.addOption(
				Option.builder("bs")
				.longOpt("export-big-states")
				.hasArg()
				.optionalArg(true)
				.argName("threshold")
				.desc("(optional) if enabled, states with more than 30 states will be exported to folder debug."
						+ " Only possible for indexed analysis.")
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
     * Attempts to parse the provided command line arguments and checks whether they specify the specifcation.
     * @param args The command line arguments.
     * @return true if and only if the provided command line arguments specify the specification.
     */
	public boolean loadSettings(String[] args) {

		CommandLineParser parser = new DefaultParser();

		try {
			cmd = parser.parse( cliOptions, args);
			
			if( ! commandLineIsValid(cmd) ){
				HelpFormatter helpFormatter = new HelpFormatter();
				helpFormatter.printHelp( "java Attestor", cliOptions );
				return false;
			}

		} catch(ParseException | NumberFormatException e) {

			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "Attestor", cliOptions );
			return false;

		}

		return true;
	}

    /**
     * @return true if and only if a settings file has been provided in the command line arguments.
     */
	public boolean hasSettingsFile(){
		return cmd.hasOption( "sf" );
	}
	
    /**
     * @return The path to the settings file provided in the command line arguments.
     */
	public String getPathToSettingsFile(){
		if( hasRootPath() ){
			return getRootPath() + File.separator + cmd.getOptionValue( "sf" );
		}
		return cmd.getOptionValue( "sf" );
	}
	
	/**
	 * @return true if and only if a root path has been provided
	 */
	public boolean hasRootPath(){
		return cmd.hasOption("rp");
	}
	
	/**
	 * @return The specified root path
	 */
	public String getRootPath(){
		return cmd.getOptionValue("rp");
	}

    /**
     * Populates all settings that customize how state spaces are exported
     * with data extracted from the command line arguments.
     * @param settings All settings.
     * @return The populated output settings.
     */
	public OutputSettings getOutputSettings( Settings settings ) {
		OutputSettings outputSettings = settings.output();
		if( cmd.hasOption( "o" )){
			outputSettings.setDefaultPath( cmd.getOptionValue( "o" ) );
		}

		if(cmd.hasOption("html")) {
			outputSettings.setExportStateSpace( true );
		}

		if( cmd.hasOption("ghtml")){
			outputSettings.setExportGrammar( true );
		}

		if( cmd.hasOption("bigStates")){
			outputSettings.setExportBigStates( true );
		}
		
		return outputSettings;
	}

    /**
     * Populates all settings that customize how the analysis is performed
     * with data extracted from the command line arguments.
     * @param settings All settings.
     * @return The populated option settings.
     */
	public OptionSettings getOptionSettings( Settings settings ) {
		
		OptionSettings optionSettings = settings.options();
		if(cmd.hasOption("x")) {
			optionSettings.setIndexedMode(true);
		}

		DebugMode.ENABLED = cmd.hasOption("v");

		if(cmd.hasOption("d")) {
			optionSettings.setMinDereferenceDepth( Integer.valueOf(cmd.getOptionValue("d")) );
		}

		if(cmd.hasOption("msp")) {
			optionSettings.setMaxStateSpaceSize( Integer.valueOf(cmd.getOptionValue("msp")) );
		}

		if(cmd.hasOption("mh")) {
			optionSettings.setMaxStateSize( Integer.valueOf(cmd.getOptionValue("mh")) );
		}

		if(cmd.hasOption("aggr")) {
			optionSettings.setAggressiveAbstractionThreshold( Integer.valueOf(cmd.getOptionValue("aggr")) );
		}

		if( cmd.hasOption("ar") ){
			optionSettings.setAggressiveReturnAbstraction( Boolean.getBoolean(cmd.getOptionValue("ar")) );
		}

		if(cmd.hasOption("kd")){
			optionSettings.setRemoveDeadVariables(false);
		}else{
			optionSettings.setRemoveDeadVariables(true);
		}
		
		return optionSettings;
	}

    /**
     * Populates all settings that customize which input files are loaded
     * with data extracted from the command line arguments.
     * @param settings All settings.
     * @return The populated input settings.
     */
	public InputSettings getInputSettings( Settings settings ) {
		InputSettings inputSettings = settings.input();
		if( cmd.hasOption("p")){
			inputSettings.setDefaultPath( cmd.getOptionValue("p"));
		}
		if( cmd.hasOption("c")){
			inputSettings.setClassName( cmd.getOptionValue("c"));
		}
		if( cmd.hasOption("m")){
			inputSettings.setMethodName(cmd.getOptionValue("m"));
		}
		if( cmd.hasOption("g")){
			inputSettings.setGrammarName(cmd.getOptionValue("g"));
		} 
		if( cmd.hasOption("i")){
			inputSettings.setInputName(cmd.getOptionValue("i"));
		} else if(inputSettings.getInputName() == null){
			if (CommandLineReader.class.getClassLoader().getResource("initialStates") == null) {
				logger.entry("Default initial states location not found!");
			} else {
				inputSettings.setInitialStatesURL(SettingsFileReader.class.getClassLoader().getResource("initialStates/emptyInput.json"));
			}
		}
		
		return inputSettings;
	}

	/**
	 * Populates all settings that customize if and how model checking is performed.
	 * @param settings All settings.
	 * @return The populated model checking settings.
	 */
	public ModelCheckingSettings getMCSettings( Settings settings ) {
		ModelCheckingSettings mcSettings = settings.modelChecking();
		if( cmd.hasOption("mc")){
			mcSettings.setModelCheckingEnabled( true );

			String formulaString = cmd.getOptionValue("mc");
			for(String formula : formulaString.split(",")){
				LTLFormula ltlFormula = null;
				try {
					ltlFormula = new LTLFormula(formula);
					mcSettings.addFormula(ltlFormula);
				} catch (Exception e) {
					logger.log(Level.WARN, "The input " + formula + " is not a valid LTL formula. Skipping it.");
				}
			}
		}

		return mcSettings;
	}

    /**
     * Checks whether the provided command line arguments are valid in the sense
     * that either a settings file or sufficient individual command line arguments
     * have been provided.
     * @param cmd The parsed command line arguments.
     * @return true if and only if sufficient command line arguments have been provided.
     */
	private boolean commandLineIsValid(CommandLine cmd){
		
			return  cmd.hasOption( "sf" )
					|| 
					( cmd.hasOption("p") && cmd.hasOption("c") && cmd.hasOption("m")
					&& cmd.hasOption("g")
							// Initial HC no longer necessary, default: empty HC
							//&& cmd.hasOption("i")
					);
		
	}

}
