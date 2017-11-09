package de.rwth.i2.attestor.main.settings;

import de.rwth.i2.attestor.graph.BasicNonterminal;
import de.rwth.i2.attestor.graph.BasicSelectorLabel;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.io.jsonExport.JsonStateSpaceExporter;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.strategies.defaultGrammarStrategies.DefaultProgramState;
import de.rwth.i2.attestor.strategies.defaultGrammarStrategies.RefinedDefaultNonterminal;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.AnnotatedSelectorLabel;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminalImpl;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedState;
import de.rwth.i2.attestor.types.GeneralType;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.FileUtils;
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
public class FactorySettings {
	
	/**
	 * The logger of this class.
	 */
	private static final Logger logger = LogManager.getLogger( "FactorySettings");

    /**
     * The total number of states that has been generated since running the tool.
     */
    private long totalNumberOfStates = 0;

    /**
     * @return A HeapConfiguration that contains neither nodes nor edges.
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
        return Settings.getInstance().options().isIndexedMode();
    }

    /**
     * @return True if and only if heap automata are required.
     */
    private boolean requiresRefinedSymbols() {

        return Settings.getInstance().options().isGrammarRefinementEnabled();
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
     * Creates a SelectorLabel with the provided label.
     * @param label A String describing this SelectorLabel.
     * @return The SelectorLabel.
     */
    public SelectorLabel getSelectorLabel(String label)  {

        if(requiresIndexedSymbols()) {
            return new AnnotatedSelectorLabel(label, "");
        } else {
            return BasicSelectorLabel.getSelectorLabel(label);
        }
    }

    /**
     * Creates a Type with the provided name.
     * @param name The name of the Type.
     * @return The Type.
     */
    public Type getType(String name) {
                                   return GeneralType.getType(name);
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


    public StateSpaceGenerator createStateSpaceGenerator(Program program, HeapConfiguration input, int scopeDepth) {

        return getStateSpaceGeneratorBuilder()
                .setProgram(program)
                .addInitialState(
                        createProgramState(input, scopeDepth)
                )
                .build();
    }

    public StateSpaceGenerator createStateSpaceGenerator(Program program,
                                                         List<HeapConfiguration> inputs, int scopeDepth) {
        List<ProgramState> inputStates = new ArrayList<>(inputs.size());
        inputs.forEach(hc -> inputStates.add(createProgramState(hc, scopeDepth)));

        return getStateSpaceGeneratorBuilder()
                .setProgram(program)
                .addInitialStates(
                        inputStates
                )
                .build();
    }

    private SSGBuilder getStateSpaceGeneratorBuilder() {

        StateSpaceGenerationSettings settings = Settings.getInstance().stateSpaceGeneration();
        return StateSpaceGenerator
                .builder()
                .setStateLabelingStrategy(
                        settings.getStateLabelingStrategy()
                )
                .setMaterializationStrategy(
                        settings.getMaterializationStrategy()
                )
                .setCanonizationStrategy(
                        settings.getCanonicalizationStrategy()
                )
                .setAbortStrategy(
                        settings.getAbortStrategy()
                )
                .setStateRefinementStrategy(
                        settings.getStateRefinementStrategy()
                )
                .setStateCounter(
                        this::addGeneratedStates
                )
                .setDeadVariableElimination(
                        Settings.getInstance().options().isRemoveDeadVariables()
                )
                .setBreadthFirstSearchEnabled(false)
                .setExplorationStrategy((s,sp) -> true)
                .setStateSpaceSupplier(() -> new InternalStateSpace(Settings.getInstance().options().getMaxStateSpaceSize()))
                .setSemanticsOptionsSupplier(DefaultSemanticsObserver::new);

    }

    public ProgramState createProgramState(HeapConfiguration heapConfiguration, int scopeDepth) {

        ProgramState result;

        if(scopeDepth > 0)  {
            if(requiresIndexedSymbols()) {
                result = new IndexedState(heapConfiguration, scopeDepth);
            } else {
                result = new DefaultProgramState(heapConfiguration, scopeDepth);
            }
        } else {
            if(requiresIndexedSymbols()) {
                result = new IndexedState(heapConfiguration);
            } else {
                result = new DefaultProgramState(heapConfiguration);
            }
        }

        result.setProgramCounter(0);
        result.prepareHeap();
        return result;
    }
}
