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


	protected FactorySettings(Scene scene) {
		super(scene);
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

	public StateSpaceGenerator createStateSpaceGenerator(Program program,
			List<HeapConfiguration> inputs, int scopeDepth) {
		List<ProgramState> inputStates = new ArrayList<>(inputs.size());
		inputs.forEach(hc -> inputStates.add(scene().createProgramState(hc)));

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
                .builder(this)
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
                		scene()::addNumberOfGeneratedStates
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
}
