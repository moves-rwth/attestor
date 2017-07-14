package de.rwth.i2.attestor.main;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.main.settings.CommandLineReader;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.main.settings.SettingsFileReader;
import de.rwth.i2.attestor.util.DebugMode;

import java.io.FileNotFoundException;

/**
 * The main class to run Attestor.
 * To start a program analysis it suffices to call run(args), where args are the command line arguments
 * passed, for example, to a main method.
 *
 * @author Christoph
 */
public class Attestor {

	/**
	 * Individual log level to show the progress of the analysis even if errors are suppressed.
	 */
	private final static Level PROGRESS = Level.forName("PROGRESS", 150);

	/**
	 * The top-level logger.
	 */
	private static final Logger logger = LogManager.getLogger( "Attestor" );

	/**
	 * The global settings for Attestor.
	 */
	private final Settings settings = Settings.getInstance();

	/**
	 * A parser for command line arguments.
	 */
	private final CommandLineReader commandLineReader = new CommandLineReader();

	public Attestor() {
		settings.options().setRemoveDeadVariables(true);
		commandLineReader.setupCLI();
	}

	/**
	 * Runs attestor to perform a program analysis.
	 *
	 * @param args The command line arguments determining settings and input files.
	 */
	public void run(String[] args) {
		
		if( commandLineReader.loadSettings(args) ) {
			
			if( commandLineReader.hasSettingsFile() ){
				SettingsFileReader settingsReader = 
						new SettingsFileReader(  commandLineReader.getPathToSettingsFile() );
				settingsReader.getInputSettings( settings );
				settingsReader.getOptionSettings( settings );
				settingsReader.getOutputSettings( settings );
			}
			commandLineReader.getInputSettings(  settings );
			commandLineReader.getOptionSettings( settings );
			commandLineReader.getOutputSettings( settings );
			commandLineReader.getMCSettings( settings );
			
			logger.log(PROGRESS, "Analyzing '"
                    + settings.input().getClasspath()
                    + "/"
                    + settings.input().getClassName()
                    + "."
                    + settings.input().getMethodName()
                    + "'..."
            );

            setupGrammar();
			
			executeTask();
		}
	}

	/**
	 * Load the grammar used in the program analysis.
	 */
    private void setupGrammar() {

        if( DebugMode.ENABLED){
            logger.log(PROGRESS, "Parsing grammar..." );
        }

        settings.grammar().loadGrammar( settings.input().getGrammarLocation() );
    }

    /**
     * Construct and run the analysis task specified through the command line arguments.
     */
	private void executeTask() {

		AnalysisTaskBuilder taskBuilder = settings.factory().createAnalysisTaskBuilder();

		if( DebugMode.ENABLED){
			logger.log(PROGRESS, "Parsing class files..." );
		}
		
		taskBuilder.loadProgram(
		        settings.input().getClasspath(),
                settings.input().getClassName(),
                settings.input().getMethodName()
        );


        if( DebugMode.ENABLED){
			logger.log(PROGRESS, "Parsing input..." );
		}

		try {
			taskBuilder.loadInput( settings.input().getInputLocation() );
		} catch (FileNotFoundException e) {
			logger.fatal("File '" + settings.input().getInputLocation() + "' specifying input location not found.");
			return;
		}

		if( DebugMode.ENABLED ) {
			logger.log(PROGRESS, "Starting state space generation..." );
		}

		AnalysisTask task = taskBuilder.build();

		task.execute();
		
		if( DebugMode.ENABLED ){
			logger.log(PROGRESS, "Finished state space generation.");
		}
		
		if(Settings.getInstance().output().isExportStateSpace() ) {
			
			if( DebugMode.ENABLED ){
				logger.log(PROGRESS, "Exporting to '" + Settings.getInstance().output().getLocationForStateSpace() );
			}
			
			task.exportAllStates();
		}
		
		if( Settings.getInstance().output().isExportTerminalStates() ){
			task.exportTerminalStates();
		}
		
		logger.log(PROGRESS, "done.");
        logger.log(PROGRESS, "#states (excluding procedure calls): " + task.getStateSpace().getStates().size() );
        logger.log(PROGRESS, "#states (including procedure calls): "
                + Settings.getInstance().factory().getTotalNumberOfStates());
		logger.log(PROGRESS, "#terminalstates: " + task.getStateSpace().getFinalStates().size() );

	}

}
