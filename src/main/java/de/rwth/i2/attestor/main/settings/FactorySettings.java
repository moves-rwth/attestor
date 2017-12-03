package de.rwth.i2.attestor.main.settings;

import de.rwth.i2.attestor.graph.BasicNonterminal;
import de.rwth.i2.attestor.graph.BasicSelectorLabel;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.io.FileUtils;
import de.rwth.i2.attestor.io.jsonExport.cytoscapeFormat.JsonStateSpaceExporter;
import de.rwth.i2.attestor.main.environment.Scene;
import de.rwth.i2.attestor.main.environment.SceneObject;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.programState.defaultState.RefinedDefaultNonterminal;
import de.rwth.i2.attestor.programState.indexedState.AnnotatedSelectorLabel;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminalImpl;
import de.rwth.i2.attestor.programState.indexedState.IndexedState;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.AggressivePostProcessingStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.FinalStateSubsumptionPostProcessingStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.NoPostProcessingStrategy;
import de.rwth.i2.attestor.types.GeneralType;
import de.rwth.i2.attestor.types.Type;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * A factory()() class to create all commonly used objects that depend on previously defined settings.
 *
 * @author Christoph
 */
public class FactorySettings extends SceneObject {

	/**
	 * The logger of this class.
	 */
	private static final Logger logger = LogManager.getLogger( "FactorySettings");

	/**
	 * The total number of states that has been generated since running the tool.
	 */
	private long totalNumberOfStates = 0;

	protected FactorySettings(Scene scene) {
		super(scene);
	}

	/**
	 * @return A HeapConfiguration that containsSubsumingState neither nodes nor edges.
	 */
	public HeapConfiguration createEmptyHeapConfiguration() {
		return new InternalHeapConfiguration();
	}

	/**
	 * Creates a Nonterminal symbol with the provided label.
	 * @param label The label of the Nonterminal.
	 * @return The Nonterminal.
	 */
	public Nonterminal getNonterminal(String label) {
		if(requiresIndexedSymbols() && requiresRefinedSymbols()) {
			logger.warn("Refinement of indexed grammars is not supported yet.");
			return new IndexedNonterminalImpl(label, new ArrayList<>());
			//throw new IllegalArgumentException("Refinement of indexed grammars is not supported yet.");
		} else if(requiresIndexedSymbols()) {
			return new IndexedNonterminalImpl(label, new ArrayList<>());
		} else if(requiresRefinedSymbols()) {
			return new RefinedDefaultNonterminal(BasicNonterminal.getNonterminal(label), null);
		} else {
			return BasicNonterminal.getNonterminal(label);
		}
	}

	/**
	 * @return true if and only if an indexed analysis is performed.
	 */
	private boolean requiresIndexedSymbols() {
		return scene().options().isIndexedMode();
	}

	/**
	 * @return True if and only if heap automata are required.
	 */
	private boolean requiresRefinedSymbols() {

		return scene().options().isGrammarRefinementEnabled();
	}

	/**
	 * Creates a Nonterminal symbol with the provided parameters.
	 * @param label The label of the Nonterminal symbol.
	 * @param rank The number of nodes that have to be attached to an edge labeled with this Nonterminal.
	 * @param isReductionTentacle An array of length rank that determines for each tentacle whether it is a reduction
	 *                            tentacle or not. The i-th tentacle is a reduction tentacle if replacing a hyperedge
	 *                            labeled with this Nonterminal never creates an outgoing edge at the i-th external
	 *                            node.
	 * @return The Nonterminal.
	 */
	public Nonterminal createNonterminal(String label, int rank, boolean[] isReductionTentacle) {

		if(requiresIndexedSymbols() && requiresRefinedSymbols()) {
			logger.warn("Refinement of indexed grammars is not supported yet.");
			return new IndexedNonterminalImpl(label, rank, isReductionTentacle, new ArrayList<>());
			// throw new IllegalArgumentException("Refinement of indexed grammars is not supported yet.");
		} else if(requiresIndexedSymbols()) {
			return new IndexedNonterminalImpl(label, rank, isReductionTentacle, new ArrayList<>());
		} else if(requiresRefinedSymbols()) {
			return new RefinedDefaultNonterminal(
					BasicNonterminal.getNonterminal(label, rank, isReductionTentacle),
					null
					);
		} else {
			return BasicNonterminal.getNonterminal(label, rank, isReductionTentacle);
		}
	}

	/**
	 * Creates an object to export state spaces.
	 * @param directory the directory to which the exportet files are written
	 * @param filename The name of the file the state space should be exported to.
	 * @param stateSpace The state space to export.
	 * @param program the program that was analyzed
	 * @throws IOException if the writing to the directory failes
	 */
	public void export(String directory, String filename, StateSpace stateSpace, Program program) throws IOException {

		FileUtils.createDirectories(directory);
		Writer writer = new BufferedWriter(
				new OutputStreamWriter( new FileOutputStream(directory + File.separator + filename) )
				);
		StateSpaceExporter exporter = new JsonStateSpaceExporter(writer);
		exporter.export(stateSpace, program);
		writer.close();
	}

	/**
	 * Adds a number of previously generated states to the global state counter.
	 * @param states The number of freshly generated states.
	 */
	private void addGeneratedStates(int states) {

		assert(states >= 0);

		totalNumberOfStates += states;
	}

	/**
	 * Resets the global state counter.
	 */
	public void resetTotalNumberOfStates() {
		totalNumberOfStates = 0;
	}

	/**
	 * @return The total number of states generated since running the tool.
	 */
	public long getTotalNumberOfStates() {
		return totalNumberOfStates;
	}


	public StateSpaceGenerator createStateSpaceGenerator(Program program, HeapConfiguration input ) {

		return getStateSpaceGeneratorBuilder()
				.setProgram(program)
				.addInitialState(
						createProgramState(input)
						)
				.build();
	}

	public StateSpaceGenerator createStateSpaceGenerator(Program program,
			List<HeapConfiguration> inputs, int scopeDepth) {
		List<ProgramState> inputStates = new ArrayList<>(inputs.size());
		inputs.forEach(hc -> inputStates.add(createProgramState(hc)));

        return getStateSpaceGeneratorBuilder()
                .setProgram(program)
                .addInitialStates(
                        inputStates
                )
                .build();
    }

	private SSGBuilder getStateSpaceGeneratorBuilder() {

        StateSpaceGenerationSettings stateSpaceGenerationSettings = Settings.getInstance().stateSpaceGeneration();
        return StateSpaceGenerator
                .builder()
                .setStateLabelingStrategy(
                        stateSpaceGenerationSettings.getStateLabelingStrategy()
                )
                .setMaterializationStrategy(
                        stateSpaceGenerationSettings.getMaterializationStrategy()
                )
                .setCanonizationStrategy(
                        stateSpaceGenerationSettings.getCanonicalizationStrategy()
                )
                .setAbortStrategy(
                        stateSpaceGenerationSettings.getAbortStrategy()
                )
                .setStateRefinementStrategy(
                        stateSpaceGenerationSettings.getStateRefinementStrategy()
                )
                .setStateCounter(
                        this::addGeneratedStates
                )
                .setDeadVariableElimination(
                        scene().options().isRemoveDeadVariables()
                )
                .setBreadthFirstSearchEnabled(false)
                .setExplorationStrategy((s,sp) -> true)
                .setStateSpaceSupplier(() -> new InternalStateSpace(scene().options().getMaxStateSpaceSize()))
                .setSemanticsOptionsSupplier(DefaultSymbolicExecutionObserver::new)
                .setPostProcessingStrategy(getPostProcessingStrategy())
                ;

    }

    private PostProcessingStrategy getPostProcessingStrategy() {

        OptionSettings optionSettings = scene().options();
        CanonicalizationStrategy aggressiveStrategy = Settings
                .getInstance()
                .stateSpaceGeneration()
                .getAggressiveCanonicalizationStrategy();

        if(!optionSettings.isPostprocessingEnabled() || optionSettings.getAbstractionDistance() == 0) {
            return new NoPostProcessingStrategy();
        }

        if(optionSettings.isIndexedMode()) {
            return new AggressivePostProcessingStrategy(aggressiveStrategy, optionSettings.getAbstractionDistance());
        }

        return new FinalStateSubsumptionPostProcessingStrategy(aggressiveStrategy, optionSettings.getAbstractionDistance());
    }

	public ProgramState createProgramState(HeapConfiguration heapConfiguration ) {

		ProgramState result;

		if(requiresIndexedSymbols()) {
			result = new IndexedState(heapConfiguration);
		} else {
			result = new DefaultProgramState(heapConfiguration);
		}


		result.setProgramCounter(0);
		result.prepareHeap();
		return result;
	}
}
