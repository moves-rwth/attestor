package de.rwth.i2.attestor.main;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.io.JsonToDefaultHC;
import de.rwth.i2.attestor.io.JsonToIndexedHC;
import de.rwth.i2.attestor.main.settings.CommandLineReader;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.main.settings.SettingsFileReader;
import de.rwth.i2.attestor.util.FileReader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.FileNotFoundException;

/**
 * The main class to run Attestor.
 * To start a program analysis it suffices to call run(args), where args are the command line arguments
 * passed, for example, to a main method.
 *
 * @author Christoph
 */
public class Attestor {

    static final String ANSI_RESET = "\u001B[0m";
    static final String ANSI_BLACK = "\u001B[30m";
    static final String ANSI_RED = "\u001B[31m";
    static final String ANSI_GREEN = "\u001B[32m";
    static final String ANSI_BLUE = "\u001B[34m";


	/**
	 * Individual log level to show the progress of the analysis even if errors are suppressed.
	 */
    private final static Level PHASE = Level.forName( "NEXT PHASE", 250);
	private final static Level PROGRESS = Level.forName( ANSI_BLUE + "PROGRESS" + ANSI_RESET, 50);


    /**
	 * The top-level logger.
	 */
	private static final Logger logger = LogManager.getLogger( "" );

	/**
	 * The global settings for Attestor.
	 */
	private final Settings settings = Settings.getInstance();

    /**
     * The input heap configuration of the main method to analyze.
     */
    private HeapConfiguration inputHeapConfiguration;

    /**
     * The task builder used to construct the first analysis task.
     */
    private AnalysisTaskBuilder taskBuilder;

    /**
     * The task builder used to construct the first analysis task.
     */
    private AnalysisTask task;

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

		if(!validationPhase(args)) {
			logger.fatal( ANSI_RED + "Validation phase failed." + ANSI_RESET);
			System.exit(1);
		}

		if(!loadPhase()) {
			logger.fatal( ANSI_RED + "Load phase failed." + ANSI_RESET);
            System.exit(1);
		}

		logger.log(PROGRESS, ANSI_BLUE + "Analyzing '"
				+ settings.input().getClasspath()
				+ "/"
				+ settings.input().getClassName()
				+ "."
				+ settings.input().getMethodName()
				+ "'..." + ANSI_RESET
		);

		if(!setupPhase()) {
			logger.fatal( ANSI_RED + "Setup phase failed." + ANSI_RESET);
            System.exit(1);
		}

		if(!stateSpaceGenerationPhase()) {
			logger.fatal( ANSI_RED + "State space generation phase failed." + ANSI_RESET);
            System.exit(1);
		}

		if(!modelCheckingPhase()) {
			logger.fatal(ANSI_RED + "Model checking phase failed." + ANSI_RESET);
            System.exit(1);
		}

		if(!reportPhase()) {
			logger.fatal(ANSI_RED + "Report phase failed." + ANSI_RESET);
            System.exit(1);
		}

        logger.log(PHASE, "\n" + ANSI_GREEN
                + "+-------------------------------------------------------------------+\n"
                + "|            analysis finished                                      |\n"
                + "+-------------------------------------------------------------------+"
                + ANSI_RESET
        );
        logger.log(PROGRESS, ANSI_BLUE + "Analysis summary:" + ANSI_RESET + "\n"
                + "+-----------+----------------------+-----------------------+--------+\n"
                + "|           |  w/ procedure calls  |  w/o procedure calls  | final  |\n"
                + "+-----------+----------------------+-----------------------+--------+\n"
                + "|  #states  "
                + String.format("|  %18d  |  %19d  |  %5d |%n",
                    Settings.getInstance().factory().getTotalNumberOfStates(),
                    task.getStateSpace().getStates().size(),
                    task.getStateSpace().getFinalStates().size()
                  )
                + "+-----------+----------------------+-----------------------+--------+"
        );
	}

	private boolean validationPhase(String[] args) {

		logger.log(PHASE, ANSI_GREEN + "\n"
                + "+-------------------------------------------------------------------+\n"
                + "|            validation                                             |\n"
                + "+-------------------------------------------------------------------+"
                + ANSI_RESET);

		return commandLineReader.loadSettings(args);
	}

	private boolean loadPhase() {

        logger.log(PHASE, ANSI_GREEN + "\n"
                + "+-------------------------------------------------------------------+\n"
                + "|            load                                                   |\n"
                + "+-------------------------------------------------------------------+"
                + ANSI_RESET);

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

		settings.grammar().loadGrammar( settings.input().getGrammarLocation() );

        try {
            loadInput();
        } catch (FileNotFoundException e) {
            logger.fatal("Input file '" + settings.input().getInputLocation() + "' could not be found.");
            return false;
        }

        taskBuilder = settings.factory().createAnalysisTaskBuilder();

		taskBuilder.loadProgram(
                settings.input().getClasspath(),
                settings.input().getClassName(),
                settings.input().getMethodName()
        );

		return true;
	}

	private void loadInput() throws FileNotFoundException {
		String str = FileReader.read(settings.input().getInputLocation());
		JSONObject jsonObj = new JSONObject(str);

		if(settings.options().isIndexedMode()) {
			inputHeapConfiguration = JsonToIndexedHC.jsonToHC( jsonObj );
		} else {
			inputHeapConfiguration = JsonToDefaultHC.jsonToHC( jsonObj );
		}
	}

	private boolean setupPhase() {

        logger.log(PHASE, ANSI_GREEN + "\n"
                + "+-------------------------------------------------------------------+\n"
                + "|            setup                                                  |\n"
                + "+-------------------------------------------------------------------+"
                + ANSI_RESET);


		// refine grammar now.

        // refine input
        taskBuilder.setInput(inputHeapConfiguration);

		return true;
	}

	private boolean stateSpaceGenerationPhase() {

        logger.log(PHASE, ANSI_GREEN + "\n"
                + "+-------------------------------------------------------------------+\n"
                + "|            state space generation                                 |\n"
                + "+-------------------------------------------------------------------+"
                + ANSI_RESET);

        task = taskBuilder.build();
        task.execute();

		return true;
	}

	private boolean modelCheckingPhase() {

        logger.log(PHASE, ANSI_GREEN + "\n"
                + "+-------------------------------------------------------------------+\n"
                + "|            model checking                                         |\n"
                + "+-------------------------------------------------------------------+"
                + ANSI_RESET);

		return true;
	}

	private boolean reportPhase() {

        logger.log(PHASE, ANSI_GREEN + "\n"
                + "+-------------------------------------------------------------------+\n"
                + "|            report                                                 |\n"
                + "+-------------------------------------------------------------------+"
                + ANSI_RESET);

		if(Settings.getInstance().output().isExportStateSpace() ) {
            logger.log(PROGRESS, ANSI_BLUE + "State space exported to '"
                    + Settings.getInstance().output().getLocationForStateSpace() + ANSI_RESET);
            task.exportAllStates();
        }

        if( Settings.getInstance().output().isExportTerminalStates() ){
            task.exportTerminalStates();
        }

		return true;
	}
}
