package de.rwth.i2.attestor.main.settings;

import de.rwth.i2.attestor.util.DebugMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

/**
 * Populates {@link Settings} from a settings file.
 *
 * @author Hannah Arndt, Christoph
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
		if( grammarSettings.has( "path" )){
			input.setPathToGrammar( grammarSettings.getString( "path" )  );
		}else if( !jsonInput.has( "defaultPath" )){
			logger.error("You must define a default path or a path for grammar");
		}
		input.setGrammarName( grammarSettings.getString( "file" ) );
		
		JSONObject initialSettings = jsonInput.getJSONObject( "initialState" );
		if( initialSettings.has( "path" ) ){
			input.setPathToInput( initialSettings.getString( "path" ) );
		}else if( !jsonInput.has( "defaultPath" )){
			logger.error("You must define a default path or a path for the initial state");
		}
		input.setInputName( initialSettings.getString( "file" ) );

		if(jsonInput.has("stateLabeling")) {
            JSONObject stateLabelingSettings = jsonInput.getJSONObject("stateLabeling");
            if (stateLabelingSettings.has("path")) {
                input.setPathToStateLabeling(stateLabelingSettings.getString("path"));
            } else if (!jsonInput.has("defaultPath")) {
                logger.error("You must define a default path or a path for the initial state");
            }
            input.setStateLabelingName(stateLabelingSettings.getString("file"));
        }

        if(jsonInput.has("refinement")) {
            JSONObject stateLabelingSettings = jsonInput.getJSONObject("refinement");
            if (stateLabelingSettings.has("path")) {
                input.setPathToStateLabeling(stateLabelingSettings.getString("path"));
            } else if (!jsonInput.has("defaultPath")) {
                logger.error("You must define a default path or a path for the initial state");
            }
            input.setStateLabelingName(stateLabelingSettings.getString("file"));
        }

		return input;
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
	
}
