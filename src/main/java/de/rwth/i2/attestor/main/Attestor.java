package de.rwth.i2.attestor.main;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.automata.HeapAutomaton;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.materialization.*;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.io.JsonToDefaultHC;
import de.rwth.i2.attestor.io.JsonToIndexedHC;
import de.rwth.i2.attestor.main.settings.CommandLineReader;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.main.settings.SettingsFileReader;
import de.rwth.i2.attestor.modelChecking.ProofStructure;
import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleParser;
import de.rwth.i2.attestor.semantics.jimpleSemantics.translation.StandardAbstractSemantics;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.strategies.GeneralInclusionStrategy;
import de.rwth.i2.attestor.strategies.StateSpaceBoundedAbortStrategy;
import de.rwth.i2.attestor.strategies.defaultGrammarStrategies.DefaultCanonicalizationStrategy;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedCanonicalizationStrategy;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.DefaultIndexMaterialization;
import de.rwth.i2.attestor.util.FileReader;
import de.rwth.i2.attestor.util.ZipUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


/**
 * The main class to run Attestor.
 *
 *  To start a program analysis it suffices to call {Attestor#run(args)}, where args are the command line arguments
 * passed, for example, to a main method.
 * In particular, these arguments have to include the path to a settings file customizing the analysis.
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
     * The originally parsed input heap configuration.
     */
	private HeapConfiguration originalInput;

    /**
     * The input heap configurations that will be used to analyze the program.
     */
    private List<HeapConfiguration> inputs = new ArrayList<>();

	/**
	 * The program that should be analyzed.
	 */
	private Program program;

	/**
	 * The state space obtained from analyzing the initial method of the provided program.
	 */
	private StateSpace stateSpace;

    /**
	 * A parser for command line arguments.
	 */
	private final CommandLineReader commandLineReader = new CommandLineReader();

	/**
	 * Runs attestor to perform a program analysis.
	 *
	 * @param args The command line arguments determining settings and analysis customizations.
	 */
	public void run(String[] args) {

	    printVersion();

	    try {
			setupPhase(args);
		} catch(Exception e) {
	    	failPhase(e, "Setup");
		}
        leavePhase("Setup");

	    try {
			parsingPhase();
		} catch(Exception e) {
	    	failPhase(e,"Parsing");
		}
        leavePhase("Parsing");

	    try {
			preprocessingPhase();
		} catch(Exception e) {
	    	failPhase(e,"Preprocessing");
		}
        leavePhase("Preprocessing");

        try {
			stateSpaceGenerationPhase();
		} catch(Exception e) {
        	failPhase(e,"State space generation");
		}
        leavePhase("State space generation");

        try {
        	modelCheckingPhase();
		} catch (Exception e) {
        	failPhase(e,"Model-checking");
		}
        leavePhase("Model-checking");

        try {
        	reportPhase();
		} catch(Exception e) {
        	failPhase(e,"Report generation");
		}
        leavePhase("Report generation");

		printSummary();
	}

    /**
     * Prints the currently running version of attestor.
     */
	private void printVersion() {

        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("attestor.properties"));
            logger.info(properties.getProperty("artifactId") + " - version " + properties.getProperty("version"));
        } catch (IOException e) {
            logger.fatal("Project version could not be found. Aborting.");
            System.exit(1);
        }
    }

    /**
     * This phase initializes the command line interfaces and populates the global settings.
     * @param args The command line arguments passed to attestor.
     */
	private void setupPhase(String[] args) {

        commandLineReader.setupCLI();
		commandLineReader.loadSettings(args);
        if( commandLineReader.hasSettingsFile() ){
            SettingsFileReader settingsReader =
                    new SettingsFileReader(  commandLineReader.getPathToSettingsFile() );
            settingsReader.getInputSettings( settings );
            settingsReader.getOptionSettings( settings );
            settingsReader.getOutputSettings( settings );
            settingsReader.getMCSettings( settings );
        }
        commandLineReader.getInputSettings(  settings );
        commandLineReader.getOptionSettings( settings );
        commandLineReader.getOutputSettings( settings );
        commandLineReader.getMCSettings( settings );

        if( commandLineReader.hasRootPath() ){
            settings.setRootPath( commandLineReader.getRootPath() );
        }
	}

    private void failPhase(Exception e, String message) {
        e.printStackTrace();
        logger.fatal(e.getMessage());
        logger.fatal("+------------------------------------------------------------------+");
        logger.fatal(String.format("|  " + ANSI_RED + "Phase execution failed:"
                + ANSI_RESET + " %-39s |", message));
        logger.fatal("+------------------------------------------------------------------+");
        System.exit(1);
    }

	private void leavePhase(String message) {
        logger.info("+-------------------------------------------------------------------+");
        logger.info(String.format("|  " + ANSI_GREEN + "Phase executed successfully:"
                + ANSI_RESET + " %-35s |", message));
        logger.info("+-------------------------------------------------------------------+");
    }

	private void parsingPhase() throws IOException {

		// Load the user-defined grammar
		if(settings.input().getGrammarName() != null) {
			settings.grammar().loadGrammarFromFile(settings.input().getGrammarLocation(), null);
		}

		// Load the requested predefined grammars
		if(settings.input().getUsedPredefinedGrammars() != null) {
			for (String predefinedGrammar : settings.input().getUsedPredefinedGrammars()) {
				logger.debug("Loading predefined grammar " + predefinedGrammar);
				HashMap<String, String> renamingMap = settings.input().getRenaming(predefinedGrammar);
				settings.grammar().loadGrammarFromURL(Attestor.class.getClassLoader()
                        .getResource("predefinedGrammars/" + predefinedGrammar + ".json"), renamingMap);
			}
		}

		loadInput();
		loadProgram();
	}

	private void loadInput() throws IOException {

		String str;
		if(settings.input().getInputName() != null){
			logger.debug("Reading user-defined initial state.");
			str = FileReader.read(settings.input().getInputLocation());
		} else {
			logger.debug("Reading predefined empty initial state.");
			str = FileReader.read(settings.input().getInitialStatesURL().openStream());
		}

		JSONObject jsonObj = new JSONObject(str);

		if(settings.options().isIndexedMode()) {
		    originalInput = JsonToIndexedHC.jsonToHC( jsonObj );
		} else {
			originalInput = JsonToDefaultHC.jsonToHC( jsonObj );
		}
	}

	private void loadProgram() {

		JimpleParser programParser = new JimpleParser(new StandardAbstractSemantics());
		program = programParser.parse(
				settings.input().getClasspath(),
				settings.input().getClassName(),
				settings.input().getMethodName()
		);
	}

	private void preprocessingPhase() {

        if(!settings.options().isIndexedMode()
                && settings.options().getStateLabelingAutomaton() != null) {
            setupStateLabeling();
        } else {
            inputs.add(originalInput);
            settings.stateSpaceGeneration().setStateLabelingStrategy(state -> {});
            if(settings.options().getStateLabelingAutomaton() == null) {
                logger.info("Skipped refinement, because no atomic propositions are required.");
            } else if(settings.options().isIndexedMode()) {
                logger.warn("Refinement of indexed grammars is not supported yet and thus ignored.");
            }
        }

        setupStateRefinement();
	}

	private void setupStateLabeling() {

        logger.info("Refining grammar...");
        HeapAutomaton stateLabelingAutomaton = settings.options().getStateLabelingAutomaton();
        settings.grammar().setGrammar(
                stateLabelingAutomaton.refine( settings.grammar().getGrammar() )
        );
        logger.info("done. Number of refined nonterminals: "
                + settings.grammar().getGrammar().getAllLeftHandSides().size());
        logger.info("Refined nonterminals are: " + settings.grammar().getGrammar().getAllLeftHandSides());

        logger.info("Refining input heap configuration...");
        inputs = stateLabelingAutomaton.refineHeapConfiguration(
                originalInput,
                settings.grammar().getGrammar(),
                new HashSet<>()
        );

        if(inputs.isEmpty())	{
            logger.fatal("No refined initial state exists.");
            throw new IllegalStateException();
        }

        logger.info("done. Number of refined heap configurations: "
                + inputs.size());

        settings.stateSpaceGeneration()
                .setStateLabelingStrategy(
                        programState -> {
                            for(String ap : stateLabelingAutomaton
                                    .move(programState.getHeap()).getAtomicPropositions()) {
                                programState.addAP(ap);
                            }
                        }
                );
    }

    private void setupStateRefinement() {

        HeapAutomaton stateRefinementAutomaton = settings.options().getStateRefinementAutomaton();
        if(stateRefinementAutomaton != null) {
            settings.stateSpaceGeneration()
                    .setStateRefinementStrategy(
                            state -> {
                                stateRefinementAutomaton.move(state.getHeap());
                                return state;
                            }
                    );
            logger.info("Initialized state refinement.");
        } else {
            settings.stateSpaceGeneration()
                    .setStateRefinementStrategy(
                            state -> state
                    );
            logger.info("No additional state refinement is used.");
        }
    }

	private void stateSpaceGenerationPhase() {

	    settings.factory().resetTotalNumberOfStates();

	    setupMaterialization();
	    setupCanonicalization();
	    setupInclusionTest();
	    setupAbortTest();

	    assert(!inputs.isEmpty());

	    StateSpaceGenerator stateSpaceGenerator = settings
                .factory()
                .createStateSpaceGenerator(
	                program,
                    inputs,
                    0
                );

	    printAnalyzedMethod();

	    stateSpace = stateSpaceGenerator.generate();
	    logger.info("State space generation finished. #states: "
                + settings.factory().getTotalNumberOfStates());
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

	private void setupMaterialization() {

        Grammar grammar = settings.grammar().getGrammar();
        MaterializationStrategy strategy;

        if(settings.options().isIndexedMode()) {
            ViolationPointResolver vioResolver = new ViolationPointResolver( grammar );
            IndexMatcher indexMatcher = new IndexMatcher( new DefaultIndexMaterialization() );
            MaterializationRuleManager grammarManager =
                    new IndexedMaterializationRuleManager(vioResolver, indexMatcher);

            GrammarResponseApplier ruleApplier =
                    new IndexedGrammarResponseApplier( new IndexMaterializationStrategy(),
                            new GraphMaterializer() );

            strategy = new GeneralMaterializationStrategy( grammarManager, ruleApplier );
            logger.info("Setup materialization using indexed grammars.");
        } else {
            ViolationPointResolver vioResolver = new ViolationPointResolver( grammar );
            MaterializationRuleManager grammarManager =
                    new DefaultMaterializationRuleManager(vioResolver);
            GrammarResponseApplier ruleApplier =
                    new DefaultGrammarResponseApplier( new GraphMaterializer() );
            strategy = new GeneralMaterializationStrategy( grammarManager, ruleApplier );
            logger.info("Setup materialization using standard hyperedge replacement grammars.");
        }

        settings.stateSpaceGeneration().setMaterializationStrategy(strategy);
    }

    private void setupCanonicalization() {

        Grammar grammar = settings.grammar().getGrammar();
        CanonicalizationStrategy strategy;
        if(settings.options().isIndexedMode()) {
            strategy = new IndexedCanonicalizationStrategy(
                    grammar,
                    true,
                    settings.options().getAggressiveAbstractionThreshold(),
                    settings.options().isAggressiveReturnAbstraction(),
                    settings.options().getMinDereferenceDepth()
            );
            logger.info("Setup canonicalization using indexed grammar.");
        } else {
            strategy = new DefaultCanonicalizationStrategy(
                    grammar,
                    true,
                    settings.options().getAggressiveAbstractionThreshold(),
                    settings.options().isAggressiveReturnAbstraction(),
                    settings.options().getMinDereferenceDepth()
            );
            logger.info("Setup canonicalization using standard hyperedge replacement grammar.");
        }
        settings.stateSpaceGeneration().setCanonicalizationStrategy(strategy);
    }

    private void setupInclusionTest() {

	    settings.stateSpaceGeneration()
                .setInclusionStrategy(
                        new GeneralInclusionStrategy()
                );
	    logger.info("Setup state inclusion test: Isomorphism.");
    }

    private void setupAbortTest() {

        int stateSpaceBound = Settings.getInstance().options().getMaxStateSpaceSize();
        int stateBound = Settings.getInstance().options().getMaxStateSize();
        settings.stateSpaceGeneration()
                .setAbortStrategy(
                        new StateSpaceBoundedAbortStrategy(stateSpaceBound, stateBound)
                );
        logger.info("Setup abort criterion: #states > "
                + stateSpaceBound
                + " or one state is larger than "
                + stateBound
                + " nodes.");
    }

	private void modelCheckingPhase() {

	    Set<LTLFormula> formulas = settings.modelChecking().getFormulae();

	    if(formulas.isEmpty()) {
	        logger.info("No LTL formulas have been provided.");
        }

	    for(LTLFormula formula : settings.modelChecking().getFormulae()) {

	        logger.info("Checking formula: " + formula.toString() + "...");
            ProofStructure proofStructure = new ProofStructure();
            proofStructure.build(stateSpace, formula);
            if(proofStructure.isSuccessful()) {
                logger.info("Formula is satisfied.");
            } else {
                logger.warn("Formula is not satisfied.");
            }
        }
	}

	private void reportPhase() throws IOException {

		if(settings.output().isExportStateSpace() ) {

		    String location = settings.output().getLocationForStateSpace();

            settings.factory().export(
                    location + File.separator + "data",
                    "statespace.json",
                    stateSpace,
                    program
            );

            List<ProgramState> states = stateSpace.getStates();
            for(int i=0; i < states.size(); i++) {
                settings.factory().export(
                        location + File.separator + "data",
                        "hc_" + i + ".json",
                        states.get(i).getHeap()
                );
            }

            InputStream zis = getClass().getClassLoader().getResourceAsStream("viewer.zip");

            File targetDirectory = new File(location + File.separator);
            ZipUtils.unzip(zis, targetDirectory);

            logger.info("State space exported to '"
                    + location
                    + "'"
            );
        }
	}

	private void printSummary() {

        logger.info("+-----------+----------------------+-----------------------+--------+");
        logger.info("|           |  w/ procedure calls  |  w/o procedure calls  | final  |");
        logger.info("+-----------+----------------------+-----------------------+--------+");
        logger.info(String.format("| #states   |  %19d |  %19d  |  %5d |",
                Settings.getInstance().factory().getTotalNumberOfStates(),
                stateSpace.getStates().size(),
                stateSpace.getFinalStates().size()
        ));
        logger.info("+-----------+----------------------+-----------------------+--------+");
    }
}
