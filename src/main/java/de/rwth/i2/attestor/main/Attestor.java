package de.rwth.i2.attestor.main;

import de.rwth.i2.attestor.automata.HeapAutomaton;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.io.JsonToDefaultHC;
import de.rwth.i2.attestor.io.JsonToIndexedHC;
import de.rwth.i2.attestor.main.settings.CommandLineReader;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.main.settings.SettingsFileReader;
import de.rwth.i2.attestor.util.FileReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

/**
 * The main class to run Attestor.
 * To start a program analysis it suffices to call run(args), where args are the command line arguments
 * passed, for example, to a main method.
 *
 * @author Christoph
 */
public class Attestor {

    private static final String ANSI_GREEN = "\u001B[32m";
	private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";

    private final Properties properties = new Properties();

    /**
	 * The top-level logger.
	 */
	private static final Logger logger = LogManager.getLogger( "Attestor" );

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

	    printVersion();

		abortOnFail(validationPhase(args), "Validation");
        leavePhase("Validation");

		abortOnFail(parsingPhase(), "Parsing");
        leavePhase("Parsing");

		abortOnFail(preprocessingPhase(), "Preprocessing");
        leavePhase("Preprocessing");

        printAnalyzedMethod();
		abortOnFail(stateSpaceGenerationPhase(), "State space generation");
        leavePhase("State space generation");

		abortOnFail(modelCheckingPhase(), "Model checking");
        leavePhase("Model-checking");

		abortOnFail(reportPhase(), "Report generation");
        leavePhase("Report generation");

		printSummary();
	}

	private void printVersion() {

        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("attestor.properties"));
            logger.info(properties.getProperty("artifactId") + " - version " + properties.getProperty("version"));
        } catch (IOException e) {
            logger.fatal("Project version could not be found. Aborting.");
            System.exit(1);
        }
    }

	private void printAnalyzedMethod() {

        logger.info("Analyzing '"
                + settings.input().getClasspath()
                + "/"
                + settings.input().getClassName()
                + "."
                + settings.input().getMethodName()
                + "'..."
        );

    }

    private void printSummary() {

	    logger.info("+-----------+----------------------+-----------------------+--------+");
        logger.info("|           |  w/ procedure calls  |  w/o procedure calls  | final  |");
        logger.info("+-----------+----------------------+-----------------------+--------+");
        logger.info(String.format("| #states   |  %19d |  %19d  |  %5d |",
                            Settings.getInstance().factory().getTotalNumberOfStates(),
                            task.getStateSpace().getStates().size(),
                            task.getStateSpace().getFinalStates().size()
                          ));
        logger.info("+-----------+----------------------+-----------------------+--------+");
    }

	private void abortOnFail(boolean executionSuccessful, String message) {

	    if(!executionSuccessful) {
			logger.fatal("+------------------------------------------------------------------+");
			logger.fatal(String.format("|  " + ANSI_RED + "Phase execution failed:"
					+ ANSI_RESET + " %-39s |", message));
			logger.fatal("+------------------------------------------------------------------+");
			System.exit(1);
		}
    }

	private boolean validationPhase(String[] args) {

		return commandLineReader.loadSettings(args);
	}

	private void leavePhase(String message) {
        logger.info("+-------------------------------------------------------------------+");
        logger.info(String.format("|  " + ANSI_GREEN + "Phase executed successfully:"
                + ANSI_RESET + " %-35s |", message));
        logger.info("+-------------------------------------------------------------------+");
    }

	private boolean parsingPhase() {

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
		if( commandLineReader.hasRootPath() ){
			settings.setRootPath( commandLineReader.getRootPath() );
		}

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

	private boolean preprocessingPhase() {

        HeapAutomaton stateLabelingAutomaton = settings.options().getStateLabelingAutomaton();
        if(stateLabelingAutomaton == null) {
            taskBuilder.setInput(inputHeapConfiguration);
            taskBuilder.setStateLabelingStrategy(state -> {});
            logger.info("Skipped refinement, because no atomic propositions are required.");
        } else {

            logger.info("Refining grammar...");
			settings.grammar().setGrammar(
					stateLabelingAutomaton.refine( settings.grammar().getGrammar() )
			);
			logger.info("done. Number of refined nonterminals: "
                    + settings.grammar().getGrammar().getAllLeftHandSides().size());
			logger.info("Refined nonterminals are: " + settings.grammar().getGrammar().getAllLeftHandSides());

			logger.info("Refining input heap configuration...");
			List<HeapConfiguration> refinedInputs = stateLabelingAutomaton
                    .refineHeapConfiguration(inputHeapConfiguration, settings.grammar().getGrammar(),  new HashSet<>());

		    if(refinedInputs.isEmpty())	{
		        logger.fatal("No refined initial state exists.");
		        return false;
            }

            logger.info("done. Number of refined heap configurations: "
                    + refinedInputs.size());

			taskBuilder.setInputs(refinedInputs);
		    taskBuilder.setStateLabelingStrategy(
                    programState -> {
                        for(String ap : stateLabelingAutomaton
                                .move(programState.getHeap())
                                .getAtomicPropositions()) {
                            programState.addAP(ap);
                        }
                    }
            );
		}

		HeapAutomaton stateRefinementAutomaton = settings.options().getStateRefinementAutomaton();
        if(stateRefinementAutomaton != null) {

            taskBuilder.setStateRefinementStrategy(
                    state -> {
                        stateRefinementAutomaton.move(state.getHeap());
                        return state;
                    }
            );
            logger.info("Initialized state refinement.");
        } else {
            logger.info("No state refinement is used.");
        }


		try {
			task = taskBuilder.build();
		} catch(Exception e) {
			logger.fatal(e.getMessage());
			return false;
		}


		return true;
	}

	private boolean stateSpaceGenerationPhase() {

		try {
			task.execute();
		} catch(Exception e) {
			logger.fatal(e.getMessage());
			return false;
		}

		return true;
	}

	private boolean modelCheckingPhase() {

		return true;
	}

	private boolean reportPhase() {

		if(Settings.getInstance().output().isExportStateSpace() ) {
            logger.info("State space exported to '"
                    + Settings.getInstance().output().getLocationForStateSpace()
					+ "'"
			);
            task.exportAllStates();
        }

        if( Settings.getInstance().output().isExportTerminalStates() ){
            task.exportTerminalStates();
        }

		return true;
	}
}
