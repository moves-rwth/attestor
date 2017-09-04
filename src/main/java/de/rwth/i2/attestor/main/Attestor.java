package de.rwth.i2.attestor.main;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.GrammarExporter;
import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationHelper;
import de.rwth.i2.attestor.grammar.canonicalization.EmbeddingCheckerProvider;
import de.rwth.i2.attestor.grammar.canonicalization.GeneralCanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.canonicalization.defaultGrammar.DefaultCanonicalizationHelper;
import de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar.EmbeddingIndexChecker;
import de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar.IndexedCanonicalizationHelper;
import de.rwth.i2.attestor.grammar.materialization.*;
import de.rwth.i2.attestor.grammar.materialization.communication.DefaultGrammarResponseApplier;
import de.rwth.i2.attestor.grammar.materialization.defaultGrammar.DefaultMaterializationRuleManager;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexMaterializationStrategy;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedGrammarResponseApplier;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedMaterializationRuleManager;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationExporter;
import de.rwth.i2.attestor.io.CustomHcListExporter;
import de.rwth.i2.attestor.io.JsonToDefaultHC;
import de.rwth.i2.attestor.io.JsonToIndexedHC;
import de.rwth.i2.attestor.io.jsonExport.JsonCustomHcListExporter;
import de.rwth.i2.attestor.io.jsonExport.JsonGrammarExporter;
import de.rwth.i2.attestor.io.jsonExport.JsonHeapConfigurationExporter;
import de.rwth.i2.attestor.io.jsonExport.JsonStateSpaceExporter;
import de.rwth.i2.attestor.main.settings.CommandLineReader;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.main.settings.SettingsFileReader;
import de.rwth.i2.attestor.modelChecking.ProofStructure;
import de.rwth.i2.attestor.refinement.grammarRefinement.GrammarRefinement;
import de.rwth.i2.attestor.refinement.grammarRefinement.InitialHeapConfigurationRefinement;
import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleParser;
import de.rwth.i2.attestor.semantics.jimpleSemantics.translation.StandardAbstractSemantics;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.strategies.StateSpaceBoundedAbortStrategy;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.AVLIndexCanonizationStrategy;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.DefaultIndexMaterialization;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.IndexCanonizationStrategy;
import de.rwth.i2.attestor.util.FileReader;
import de.rwth.i2.attestor.util.FileUtils;
import de.rwth.i2.attestor.util.ZipUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.*;
import java.util.*;



/**
 * The main class to run Attestor.
 *
 *  To start a program analysis it suffices to call {Attestor#run(args)}, where args are the command line arguments
 * passed, for example, to a main method.
 * In particular, these arguments have to include the path to a settings file customizing the analysis.
 * <br>
 * The execution of Attestor consists of six phases. Any fatal failure of a phase (that is an exception caught
 * by the method starting the phase) aborts further execution.
 * The six phases are executed in the following order:
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

    /**
     * Green color for log messages.
     */
    private static final String ANSI_GREEN = "\u001B[32m";

    /**
     * Red color for log messages
     */
    private static final String ANSI_RED = "\u001B[31m";

    /**
     * Resets previously set colors in log messages.
     */
    private static final String ANSI_RESET = "\u001B[0m";

    /**
     * Project properties, such as the current version number.
     */
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
     *
     * @see <a href="https://github.com/moves-rwth/attestor/wiki/Command-Line-Options">
     *          Explanation of all command line options
     *      </a>
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

	
		loadUserDefinedGrammar();
		loadPredefinedGrammars();

		loadInput();
		loadProgram();
	}

	private void loadPredefinedGrammars() {
		
		if(settings.input().getUsedPredefinedGrammars() != null) {
			for ( String predefinedGrammar : settings.input().getUsedPredefinedGrammars() ) {
				logger.debug("Loading predefined grammar " + predefinedGrammar);
				//HashMap<String, String> renamingMap = settings.input().getRenaming( predefinedGrammar );
				String locationOfRenamingMap = settings.input().getRenamingLocation( predefinedGrammar );
				try{
				HashMap<String,String> renamingMap = parseRenamingMap( locationOfRenamingMap );
				settings.grammar().loadGrammarFromURL(Attestor.class.getClassLoader()
                        .getResource("predefinedGrammars/" + predefinedGrammar + ".json"), renamingMap);
				}catch( FileNotFoundException e ){
					logger.warn( "Skipping predefined grammar "
								+ predefinedGrammar + ".");
				}
			}
		}
	}

	private HashMap<String, String> parseRenamingMap(String locationOfRenamingMap) throws FileNotFoundException {
		HashMap<String, String> rename = new HashMap<String, String>();
		// Read in the type and field name mapping
		try {
			BufferedReader br = new BufferedReader(new java.io.FileReader( locationOfRenamingMap ));
			String definitionsLine = null;
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
			br.close();
		} catch (FileNotFoundException e) {
			logger.warn("File " + locationOfRenamingMap + " not found. ");
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return rename;
	}

	private void loadUserDefinedGrammar() {
		if(settings.input().getUserDefinedGrammarName() != null) {
			settings.grammar().loadGrammarFromFile(settings.input().getGrammarLocation(), null);
		}
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

        if(!settings.options().isIndexedMode()) {
            setupStateLabeling();
        } else {
            inputs.add(originalInput);
            settings.stateSpaceGeneration().setStateLabelingStrategy(state -> {});
            logger.warn("Refinement of indexed grammars is not supported yet and thus ignored.");
        }
	}

	private void setupStateLabeling() {

		de.rwth.i2.attestor.refinement.HeapAutomaton refinementAutomaton = settings.options().getRefinementAutomaton();
		if(refinementAutomaton == null) {
			logger.info("No grammar refinement is required.");
			inputs.add(originalInput);
			return;
		}

		logger.info("Refining grammar...");
		GrammarRefinement grammarRefinement = new GrammarRefinement(
				settings.grammar().getGrammar(),
				refinementAutomaton
		);
		settings.grammar().setGrammar(grammarRefinement.getRefinedGrammar());

        logger.info("done. Number of refined nonterminals: "
                + settings.grammar().getGrammar().getAllLeftHandSides().size());
        logger.info("Refined nonterminals are: " + settings.grammar().getGrammar().getAllLeftHandSides());

        logger.info("Refining input heap configuration...");

        InitialHeapConfigurationRefinement inputRefinement = new InitialHeapConfigurationRefinement(
        		originalInput,
				settings.grammar().getGrammar(),
				refinementAutomaton
		);

        inputs = inputRefinement.getRefinements();

        if(inputs.isEmpty())	{
            logger.fatal("No refined initial state exists.");
            throw new IllegalStateException();
        }

        logger.info("done. Number of refined heap configurations: "
                + inputs.size());
    }

	private void stateSpaceGenerationPhase() {

	    settings.factory().resetTotalNumberOfStates();

	    setupMaterialization();
	    setupCanonicalization();
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
        EmbeddingCheckerProvider checkerProvider = getEmbeddingCheckerProvider();
        CanonicalizationHelper canonicalizationHelper;
         
        if(settings.options().isIndexedMode()) {
        	
        	canonicalizationHelper = getIndexedCanonicalizationHelper(checkerProvider);
            logger.info("Setup canonicalization using indexed grammar.");
            
        } else {
        	canonicalizationHelper = new DefaultCanonicalizationHelper( checkerProvider );
            logger.info("Setup canonicalization using standard hyperedge replacement grammar.");
        }
        CanonicalizationStrategy strategy = new GeneralCanonicalizationStrategy(grammar, canonicalizationHelper); 
        settings.stateSpaceGeneration().setCanonicalizationStrategy(strategy);
    }

	private CanonicalizationHelper getIndexedCanonicalizationHelper(EmbeddingCheckerProvider checkerProvider) {
		CanonicalizationHelper canonicalizationHelper;
		IndexCanonizationStrategy indexStrategy = new AVLIndexCanonizationStrategy();
		
		
		
		IndexMaterializationStrategy materializer = new IndexMaterializationStrategy();
		DefaultIndexMaterialization indexGrammar = new DefaultIndexMaterialization();
		IndexMatcher indexMatcher = new IndexMatcher( indexGrammar);
		EmbeddingIndexChecker indexChecker = 
				new EmbeddingIndexChecker( indexMatcher, 
											materializer );
		
		canonicalizationHelper = new IndexedCanonicalizationHelper( indexStrategy, checkerProvider, indexChecker);
		return canonicalizationHelper;
	}

	private EmbeddingCheckerProvider getEmbeddingCheckerProvider() {
		final int abstractionDifference = settings.options().getAbstractionDistance();
		final int aggressiveAbstractionThreshold = settings.options().getAggressiveAbstractionThreshold();
		final boolean aggressiveReturnAbstraction = settings.options().isAggressiveReturnAbstraction();
		EmbeddingCheckerProvider checkerProvider = new EmbeddingCheckerProvider(abstractionDifference ,
																				aggressiveAbstractionThreshold, 
																				aggressiveReturnAbstraction);
		return checkerProvider;
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

	    if(settings.output().isExportGrammar() ){
	        String location = settings.output().getLocationForGrammar();

            // Copy necessary libraries
            InputStream zis = getClass().getClassLoader().getResourceAsStream("grammarViewer" +
                    ".zip");

            File targetDirectory = new File(location + File.separator);
            ZipUtils.unzip(zis, targetDirectory);

            // Generate JSON files
            GrammarExporter exporter = new JsonGrammarExporter();
            exporter.export(location + File.separator + "grammarData", settings.grammar().getGrammar());

            logger.info("Grammar exported to '"
                    + location
            );


        }

		if(settings.output().isExportStateSpace() ) {

		    String location = settings.output().getLocationForStateSpace();

		    exportStateSpace(
                    location + File.separator + "data",
                    "statespace.json",
                    stateSpace,
                    program
            );

            Set<ProgramState> states = stateSpace.getStates();
            int i=0;
            for(ProgramState state : states) {
                exportHeapConfiguration(
                        location + File.separator + "data",
                        "hc_" + i + ".json",
                        state.getHeap()
                );
                ++i;
            }

            InputStream zis = getClass().getClassLoader().getResourceAsStream("viewer.zip");

            File targetDirectory = new File(location + File.separator);
            ZipUtils.unzip(zis, targetDirectory);

            logger.info("State space exported to '"
                    + location
                    + "'"
            );
        }

        if(settings.output().isExportCustomHcs()){
	        String location = settings.output().getLocationForCustomHcs();

            // Copy necessary libraries
            InputStream zis = getClass().getClassLoader().getResourceAsStream("customHcViewer" +
                    ".zip");

            File targetDirectory = new File(location + File.separator);
            ZipUtils.unzip(zis, targetDirectory);

            // Generate JSON files for prebooked HCs and their summary
            CustomHcListExporter exporter = new JsonCustomHcListExporter();
            exporter.export(location + File.separator + "customHcsData", settings.output().getCustomHcSet());

            logger.info("Custom HCs exported to '"
                    + location
            );

        }

	}

    private void exportHeapConfiguration(String directory, String filename, HeapConfiguration hc)
            throws IOException {

        FileUtils.createDirectories(directory);
        FileWriter writer = new FileWriter(directory + File.separator + filename);
        HeapConfigurationExporter exporter = new JsonHeapConfigurationExporter(writer);
        exporter.export(hc);
        writer.close();
    }

    private void exportStateSpace(String directory, String filename, StateSpace stateSpace, Program program)
            throws IOException {

        FileUtils.createDirectories(directory);
        Writer writer = new BufferedWriter(
                new OutputStreamWriter( new FileOutputStream(directory + File.separator + filename) )
        );
        StateSpaceExporter exporter = new JsonStateSpaceExporter(writer);
        exporter.export(stateSpace, program);
        writer.close();
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
