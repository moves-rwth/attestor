package de.rwth.i2.attestor.main;

import java.io.IOException;
import java.util.Properties;

import de.rwth.i2.attestor.main.environment.DefaultScene;
import org.apache.logging.log4j.*;

import de.rwth.i2.attestor.main.phases.PhaseRegistry;
import de.rwth.i2.attestor.main.phases.impl.*;
import de.rwth.i2.attestor.main.phases.transformers.StateSpaceTransformer;
import de.rwth.i2.attestor.main.settings.Settings;


/**
 * The main class to run Attestor.
 *
 *  To start a program analysis it suffices to call {Attestor#run(args)}, where args are the command line arguments
 * passed, for example, to a main method.
 * In particular, these arguments have to include the path to a settings file customizing the analysis.
 * <br>
 * The execution of Attestor consists of phases. Any fatal failure of a phase (that is an exception caught
 * by the method starting the phase) aborts further execution.
 * The main phases are executed in the following order:
 * <ol>
 *     <li>Setup phase: Validates the provided command line options and populates the global Settings.</li>
 *     <li>Parsing phase: Parses all supplied input files, such as the program to be analyzed,
 *                        the grammar, input state, etc.</li>
 *     <li>Preprocessing phase: Applies all pre-computation steps that should be applied to programs, grammars, etc.
 *                              For example, grammar refinement is performed in this phase.</li>
 *     <li>State space generation phase: Applies the abstract semantics defined by the provided graph grammar and
 *                                       the input program until a fixed point is reached.</li>
 *     <li>Model-checking phase: If temporal logic formulas have been provided, this phase checks whether they
 *                               are satisfied by the state space generated in the previous phase.</li>
 *     <li>Report phase: Exports the previously computed results. </li>
 * </ol>
 *
 * @author Christoph
 */
public class Attestor {

    private final Properties properties = new Properties();

	private static final Logger logger = LogManager.getLogger( "Attestor" );

	private final Settings settings = Settings.getInstance();

	private PhaseRegistry registry;

	private DefaultScene scene = new DefaultScene();

	public Attestor() {

		settings.setScene(scene);
	}

	/**
	 * Runs attestor to perform a program analysis.
	 *
	 * @param args The command line arguments determining settings and analysis customizations.
     *
     * @see <a href="https://github.com/moves-rwth/attestor/wiki/Command-Line-Options">
     *          Explanation of all command line options
     *      </a>
	 */
	public void run(String[] args) {


		printVersion();

		registry = new PhaseRegistry(settings);

		registry
				.addPhase( new CLIPhase(scene, args) )
				.addPhase( new ParseProgramPhase(scene) )
				.addPhase( new ParseGrammarPhase(scene) )
				.addPhase( new ParseInputPhase(scene) )
				.addPhase( new ParseContractsPhase(scene) )
				.addPhase( new MarkingGenerationPhase(scene) )
				.addPhase( new GrammarRefinementPhase(scene) )
				.addPhase( new AbstractionPreprocessingPhase(scene) )
				.addPhase( new StateSpaceGenerationPhase(scene) )
				.addPhase( new ModelCheckingPhase(scene) )
				.addPhase( new CounterexampleGenerationPhase(scene) )
				.addPhase( new ReportGenerationPhase(scene) )
				.execute();

		registry.logExecutionSummary();
		registry.logExecutionTimes();
	}

	public long getTotalNumberOfStates() {
		return scene.getNumberOfGeneratedStates();
	}

	public int getNumberOfStatesWithoutProcedureCalls() {
		return registry.getMostRecentPhase(StateSpaceTransformer.class)
				.getStateSpace()
				.getStates()
				.size();
	}

	public int getNumberOfFinalStates() {

		return registry.getMostRecentPhase(StateSpaceTransformer.class)
				.getStateSpace()
				.getFinalStates()
				.size();
	}

	private void printVersion() {

        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("attestor.properties"));
            logger.log(Level.getLevel("VERSION"), properties.getProperty("artifactId")
					+ " - version " + properties.getProperty("version"));
        } catch (IOException e) {
            logger.fatal("Project version could not be found. Aborting.");
            System.exit(1);
        }
    }
}
