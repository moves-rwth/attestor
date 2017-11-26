package de.rwth.i2.attestor.main.settings;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import org.apache.logging.log4j.*;
import org.json.JSONArray;
import org.json.JSONObject;

import de.rwth.i2.attestor.LTLFormula;

/**
 * Populates {@link Settings} from a settings file.
 *
 * @author Hannah Arndt, Christoph, Christina
 */
public class SettingsFileReader {

    /**
     * The logger of this class.
     */
    private static final Logger logger = LogManager.getLogger( "SettingsFileReader" );

    /**
     * The JSONObject obtained from parsing the provided JSON file.
     */
	private JSONObject jsonSettings;

    /**
     * Creates a SettingsFileReader to parse a JSON file.
     * @param file A JSON file that containsSubsumingState settings.
     */
	public SettingsFileReader( String file ) {
		Scanner scan;
		try {

			scan = new Scanner(new FileReader(file));
			StringBuilder str = new StringBuilder();
			while (scan.hasNext())
				str.append(scan.nextLine()).append("\n");
			scan.close();

			jsonSettings = new JSONObject(str.toString());
			
		} catch (FileNotFoundException e) {
		    logger.fatal(
		           "The settings file '" + file + "' could not be found. Execution is aborted.\n"
                    + "\tThe home directory of this tool is '" + System.getProperty("user.dir") + "'"
            );
			logger.fatal("Settings file '" + file + "' could not be found. Aborting.");
			System.exit(1);
		}
	}

    /**
     * Populates all input settings from the parsed settings file.
     * @param settings All settings.
     * @return The populated input settings.
     */
	@SuppressWarnings("UnusedReturnValue")
	public InputSettings getInputSettings(Settings settings ){
		JSONObject jsonInput = jsonSettings.getJSONObject( "input" );
		InputSettings input = settings.input();

		// path to class and json files to be analyzed
		if( jsonInput.has( "defaultPath" ) ){
			input.setDefaultPath( jsonInput.getString( "defaultPath" ) );
		}

		if(jsonSettings.has("scenario")) {
			input.setScenario(jsonSettings.getString("scenario"));
		}

		JSONObject programSettings = jsonInput.getJSONObject( "program" );
		if( programSettings.has( "classpath" )){
			input.setClasspath( programSettings.getString( "classpath" )  );
		}else if( !jsonInput.has( "defaultPath" )){
			logger.error("You must define a default path or a classpath");
		}
		if(programSettings.has("class")) {
			input.setClassName(programSettings.getString("class"));
		} else {
			logger.error("Please provide a class to be analysed.");
		}
		if(programSettings.has("method")) {
			input.setMethodName(programSettings.getString("method"));
		} else {
			logger.error("Please provide a method to be analysed.");
		}
		
		JSONObject grammarSettings = jsonInput.getJSONObject( "userDefinedGrammar" );
		if(grammarSettings.has("file")) {
			if (grammarSettings.has("path")) {
				input.setPathToGrammar(grammarSettings.getString("path"));
			} else if (!jsonInput.has("defaultPath")) {
				logger.error("You must define a default path or a path for the grammar");
			}
			input.setUserDefinedGrammarName(grammarSettings.getString("file"));
		}

		// Add requested predefined grammars
		JSONArray predefinedGrammarSettings = jsonInput.getJSONArray( "predefinedGrammars" );
		for(int i = 0; i < predefinedGrammarSettings.length(); i++){
			JSONObject predefinedGrammarSetting = predefinedGrammarSettings.getJSONObject(i);
			final String grammarType = predefinedGrammarSetting.getString("type");

			// Check if corresponding grammar exists
			if(SettingsFileReader.class.getClassLoader().getResource("predefinedGrammars/" + grammarType + ".json") != null){

					String renameFileLocation = predefinedGrammarSetting.getString("definition");
					input.addPredefinedGrammar( grammarType, renameFileLocation );
					logger.debug( "Adding predefined grammar " + grammarType );
			} else {
				logger.warn("No predefined grammar of type " + grammarType
						+ " available. Skipping it.");
			}


		}

		JSONObject initialSettings = jsonInput.getJSONObject("initialState");
		if( initialSettings.has( "path" ) ){
			input.setPathToInput( initialSettings.getString( "path" ) );
		}else if( (!jsonInput.has( "defaultPath" )) && initialSettings.has("file")){
			logger.error("You must define a default path or a path for the initial state");
		}
		if(initialSettings.has("file")) {
			input.setInputName(initialSettings.getString("file"));
		} else if(input.getInputName() == null) {
			if (SettingsFileReader.class.getClassLoader().getResource("initialStates") == null) {
				throw new IllegalStateException("Default initial states location not found.");
			} else {
				input.setInitialStatesURL(SettingsFileReader.class.getClassLoader().getResource("initialStates/emptyInput.json"));
			}
		}
		
		if(jsonInput.has("contracts")) {
			JSONObject contractSettings = jsonInput.getJSONObject("contracts");
			if( contractSettings.has( "path" ) ){
				input.setPathToContracts( contractSettings.getString( "path" ) );
			}else if( (!jsonInput.has( "defaultPath" )) && contractSettings.has("file")){
				logger.error("You must define a default path or a path for the contracts");
			}
			JSONArray listOfFiles = contractSettings.getJSONArray("files");
			for(int i = 0; i < listOfFiles.length(); i++ ){
				input.addContractFile( listOfFiles.getString(i) );
			}
		}

		return input;
	}

	/**
     * Populates all option settings from the parsed settings file.
     * @param settings All settings.
     * @return The populated option settings.
     */
	@SuppressWarnings("UnusedReturnValue")
	public OptionSettings getOptionSettings(Settings settings ){
		JSONObject jsonOptions = jsonSettings.getJSONObject( "options" );

		OptionSettings options = settings.options();

		for(String key : jsonOptions.keySet()) {

			switch(key) {
				case "mode":
					options.setIndexedMode( jsonOptions.get(key).equals( "indexed" ) );
					break;
				case "abstractionDistance":
					options.setAbstractionDistance( jsonOptions.getInt(key) );
					break;
				case "maximalStateSpace":
					options.setMaxStateSpaceSize( jsonOptions.getInt(key) );
					break;
				case "maximalHeap":
					options.setMaxStateSize( jsonOptions.getInt(key) );
					break;
				case "removeDeadVariables":
					options.setRemoveDeadVariables( jsonOptions.getBoolean(key) );
					break;
				case "aggressiveNullAbstraction":
					options.setAggressiveNullAbstraction( jsonOptions.getBoolean(key) );
					break;
				case "garbageCollection":
					options.setGarbageCollectionEnabled(jsonOptions.getBoolean(key));
					break;
				case "stateSpacePostProcessing":
					options.setPostProcessingEnabled(jsonOptions.getBoolean(key));
					break;
				default:
					logger.error("Ignoring unknown option: " + key);
					break;
			}
		}

		return options;
	}

    /**
     * Populates all output settings from the parsed settings file.
     * @param settings All settings.
     * @return The populated output settings.
     */
	@SuppressWarnings("UnusedReturnValue")
	public OutputSettings getOutputSettings(Settings settings ){
		JSONObject jsonOutput = jsonSettings.getJSONObject( "output" );
		OutputSettings output = settings.output();
		
		if( jsonOutput.has( "defaultPath" )){
			output.setDefaultPath( jsonOutput.getString( "defaultPath" ) );
		}

		if( jsonOutput.has("enabled") ) {
			settings.options().setNoExport(!jsonOutput.getBoolean("enabled"));
		}
		
		if( jsonOutput.has( "stateSpace" ) ){
			output.setExportStateSpace( true );
			JSONObject jsonStateSpace = jsonOutput.getJSONObject( "stateSpace" );
			if( jsonStateSpace.has( "path" ) ){
				output.setPathForStateSpace( jsonStateSpace.getString( "path" ) );
			}
			if( jsonStateSpace.has( "folder" ) ){
				output.setFolderForStateSpace( jsonStateSpace.getString( "folder" ) );
			}
		}
		
		if( jsonOutput.has( "terminalStates" ) ){
			output.setExportTerminalStates( true );
			JSONObject jsonTerminalStates = jsonOutput.getJSONObject( "terminalStates" );
			if( jsonTerminalStates.has( "path" ) ){
				output.setPathForTerminalStates( jsonTerminalStates.getString( "path" ) );
			}
			if( jsonTerminalStates.has( "folder" ) ){
				output.setPathForTerminalStates( jsonTerminalStates.getString( "folder" ) );
			}
		}
		
		if( jsonOutput.has( "grammar" ) ){
			output.setExportGrammar( true );
			JSONObject jsonGrammar = jsonOutput.getJSONObject( "grammar" );
			if( jsonGrammar.has( "path" ) ){
				output.setPathForGrammar( jsonGrammar.getString( "path" ) );
			}
			if( jsonGrammar.has( "folder" ) ){
				output.setFolderForGrammar( jsonGrammar.getString( "folder" ) );
			}
		}

		if( jsonOutput.has( "customHCs" ) ){
			output.setExportCustomHcs( true );
			JSONObject jsonGrammar = jsonOutput.getJSONObject( "customHCs" );
			if( jsonGrammar.has( "path" ) ){
				output.setPathForCustomHcs( jsonGrammar.getString( "path" ) );
			}
			if( jsonGrammar.has( "folder" ) ){
				output.setFolderForCustomHcs( jsonGrammar.getString( "folder" ) );
			}
		}
		
		if( jsonOutput.has( "bigStates" ) ){
			output.setExportBigStates( true );
			JSONObject jsonBS = jsonOutput.getJSONObject( "bigStates" );
			output.exportBigStatesThreshold( jsonBS.getInt( "threshold" ) );
			if( jsonBS.has( "path" ) ){
				output.setPathForBigStates( jsonBS.getString( "path" ) );
			}
			if( jsonBS.has( "folder" ) ){
				output.setFolderForBigStates( jsonBS.getString( "folder" ) );
			}
		}
		
		if( jsonOutput.has( "contracts" ) ){
			output.setExportContracts( true );
			JSONObject jsonC = jsonOutput.getJSONObject( "contracts" );
			if( jsonC.has( "path" ) ){
				output.setDirectoryForContracts( jsonC.getString( "path" ) );
			}
			JSONArray requestArray = jsonC.getJSONArray("requestedContracts");
			for( int i = 0; i < requestArray.length(); i++ ) {
				JSONObject request = requestArray.getJSONObject(i);
				String signature = request.getString("signature");
				String filename = request.getString("filename");
				settings.output().addRequiredContract(signature, filename);
			}
		}
		
		return output;
	}

	/**
	 * Populates the model checking settings with the input from the parsed settings file.
	 * @param settings all settings
	 * @return the populated model checking settings
	 */
	@SuppressWarnings("UnusedReturnValue")
	public ModelCheckingSettings getMCSettings(Settings settings){
		JSONObject jsonMC = jsonSettings.getJSONObject( "modelChecking" );
		ModelCheckingSettings mc = settings.modelChecking();

		if( jsonMC.has( "enabled" )){
			mc.setModelCheckingEnabled(jsonMC.getBoolean("enabled"));
		}
		if( jsonMC.has("formulae")){
			String formulaeString = jsonMC.getString("formulae");
			for(String formula : formulaeString.split(";")){
				try {
					LTLFormula ltlFormula = new LTLFormula(formula);
					// Transform to PNF
					ltlFormula.toPNF();
					mc.addFormula(ltlFormula);
				} catch (Exception e) {
					logger.log(Level.WARN, "The input " + formula + " is not a valid LTL formula. Skipping it.");

				}
			}
		}

		return mc;
	}
	
}
