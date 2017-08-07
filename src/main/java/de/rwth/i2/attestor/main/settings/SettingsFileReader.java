package de.rwth.i2.attestor.main.settings;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.automata.JsonToHeapAutomatonParser;
import de.rwth.i2.attestor.util.DebugMode;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

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
     * @param file A JSON file that contains settings.
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
			Settings settings = Settings.getInstance();
			
			//this.input = settings.getJSONObject("input");
			//initializeOptionsFromJson( settings.getJSONObject("options") );
			//this.options = settings.getJSONObject("options");
			//this.output = settings.getJSONObject("output");
			
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
	public InputSettings getInputSettings( Settings settings ){
		JSONObject jsonInput = jsonSettings.getJSONObject( "input" );
		InputSettings input = settings.input();
		
		if( jsonInput.has( "defaultPath" ) ){
			input.setDefaultPath( jsonInput.getString( "defaultPath" ) );
		}
		
		JSONObject programSettings = jsonInput.getJSONObject( "program" );
		if( programSettings.has( "classpath" )){
			input.setClasspath( programSettings.getString( "classpath" )  );
		}else if( !jsonInput.has( "defaultPath" )){
			logger.error("You must define a default path or a classpath");
		}
		input.setClassName( programSettings.getString( "class" ) );
		input.setMethodName( programSettings.getString( "method" ) );
		
		JSONObject grammarSettings = jsonInput.getJSONObject( "grammar" );
		if(grammarSettings.has("file")) {
			if (grammarSettings.has("path")) {
				input.setPathToGrammar(grammarSettings.getString("path"));
			} else if (!jsonInput.has("defaultPath")) {
				logger.error("You must define a default path or a path for grammar");
			}
			input.setGrammarName(grammarSettings.getString("file"));
		}

		// Add requested predefined grammars
		JSONArray predefinedGrammarSettings = jsonInput.getJSONArray( "predefinedGrammars" );
		for(int i = 0; i < predefinedGrammarSettings.length(); i++){
			JSONObject predefinedGrammarSetting = predefinedGrammarSettings.getJSONObject(i);
			String grammarType = predefinedGrammarSetting.getString("type");

			// Check if corresponding grammar exists
			if(SettingsFileReader.class.getClassLoader().getResource("predefinedGrammars/" + grammarType + ".json") != null){

					HashMap<String, String> rename = extractMapping(predefinedGrammarSetting);
					input.addPredefinedGrammar(predefinedGrammarSetting.getString("type"), rename);
					logger.debug("Adding predefined grammar " + grammarType);
			} else {
				logger.warn("No predefined grammar of type " + predefinedGrammarSetting.getString("type") + " available. Skipping it.");
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

		return input;
	}

	private HashMap<String,String> extractMapping(JSONObject predefinedGrammarSetting) {

		HashMap<String, String> rename = null;
		// Read in the type and field name mapping
		try {
			BufferedReader br = new BufferedReader(new FileReader(predefinedGrammarSetting.getString("definition")));
			String definitionsLine = null;
			rename = new HashMap<String, String>();
			try {
				definitionsLine = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			while(definitionsLine != null){
				if(definitionsLine.startsWith("@Rename")){
					String[] map = definitionsLine.replace("@Rename", "").split("->");
					assert map.length == 2;

					rename.put(map[0].trim(), map[1].trim());
				}

				try {
					definitionsLine = br.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		} catch (FileNotFoundException e) {
			logger.warn("File " + predefinedGrammarSetting.getString("definition") + " not found. Skipping predefined grammar "
					+ predefinedGrammarSetting.getString("type") + ".");
		}

		return rename;
	}

	/**
     * Populates all option settings from the parsed settings file.
     * @param settings All settings.
     * @return The populated option settings.
     */
	public OptionSettings getOptionSettings( Settings settings ){
		JSONObject jsonOptions = jsonSettings.getJSONObject( "options" );
		OptionSettings options = settings.options();
		
		if(jsonOptions.has( "mode" )) {
			options.setIndexedMode( jsonOptions.get( "mode" ).equals( "indexed" ) );
		}

		DebugMode.ENABLED = jsonOptions.has("logging");

		if( jsonOptions.has( "depth" )) {
			options.setMinDereferenceDepth( jsonOptions.getInt( "depth" ) );
		}

		if( jsonOptions.has( "maximalStateSpace") ) {
			options.setMaxStateSpaceSize( jsonOptions.getInt( "maximalStateSpace" ) );
		}

		if( jsonOptions.has(  "maximalHeap" ) ) {
			options.setMaxStateSize( jsonOptions.getInt( "maximalHeap" ) );
		}

		if( jsonOptions.has( "aggressiveThreshold" )) {
			options.setAggressiveAbstractionThreshold( jsonOptions.getInt( "aggressiveThreshold" ));
		}

		if( jsonOptions.has( "aggressiveReturn" ) ){
			options.setAggressiveReturnAbstraction( jsonOptions.getBoolean( "aggressiveReturn" ) );
		}

		if( jsonOptions.has( "removeDeadVariables" ) ){
			options.setRemoveDeadVariables( jsonOptions.getBoolean( "removeDeadVariables" ) );
		}

		if( jsonOptions.has("stateLabeling") ) {
            JSONArray stateLabelingSettings = jsonOptions.getJSONArray("stateLabeling");
            JsonToHeapAutomatonParser parser = new JsonToHeapAutomatonParser(stateLabelingSettings);
            options.setStateLabelingAutomaton( parser.getHeapAutomaton() );
        }

        if( jsonOptions.has("stateRefinement") ) {
            JSONArray stateRefinementSettings = jsonOptions.getJSONArray("stateRefinement");
            JsonToHeapAutomatonParser parser = new JsonToHeapAutomatonParser(stateRefinementSettings);
            options.setStateRefinementAutomaton( parser.getHeapAutomaton() );
        }

		return options;
	}

    /**
     * Populates all output settings from the parsed settings file.
     * @param settings All settings.
     * @return The populated output settings.
     */
	public OutputSettings getOutputSettings( Settings settings ){
		JSONObject jsonOutput = jsonSettings.getJSONObject( "output" );
		OutputSettings output = settings.output();
		
		if( jsonOutput.has( "defaultPath" )){
			output.setDefaultPath( jsonOutput.getString( "defaultPath" ) );
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
		
		return output;
	}

	/**
	 * Populates the model checking settings with the input from the parsed settings file.
	 * @param settings all settings
	 * @return the populated model checking settings
	 */
	public ModelCheckingSettings getMCSettings(Settings settings){
		JSONObject jsonMC = jsonSettings.getJSONObject( "modelChecking" );
		ModelCheckingSettings mc = settings.modelChecking();

		if( jsonMC.has( "enabled" )){
			mc.setModelCheckingEnabled(jsonMC.getBoolean("enabled"));
		}
		if( jsonMC.has("formulae")){
			String formulaeString = jsonMC.getString("formulae");
			for(String formula : formulaeString.split(",")){
				try {
					mc.addFormula(new LTLFormula(formula));
				} catch (Exception e) {
					logger.log(Level.WARN, "The input " + formula + " is not a valid LTL formula. Skipping it.");

				}
			}
		}

		return mc;
	}
	
}
