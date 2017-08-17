package de.rwth.i2.attestor.main.settings;

import de.rwth.i2.attestor.grammar.GrammarExporter;
import de.rwth.i2.attestor.graph.GeneralNonterminal;
import de.rwth.i2.attestor.graph.GeneralSelectorLabel;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationExporter;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.io.jsonExport.JsonHeapConfigurationExporter;
import de.rwth.i2.attestor.io.jsonExport.JsonStateSpaceExporter;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.strategies.defaultGrammarStrategies.DefaultState;
import de.rwth.i2.attestor.strategies.defaultGrammarStrategies.RefinedDefaultNonterminal;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.AnnotatedSelectorLabel;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminalImpl;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedState;
import de.rwth.i2.attestor.types.GeneralType;
import de.rwth.i2.attestor.types.Type;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * A factory class to create all commonly used objects that depend on previously defined settings.
 *
 * @author Christoph
 */
public class FactorySettings {

    /**
     * Stores for each directory the grammar exporters created so far.
     */
    private final Map<String, GrammarExporter> grammarExporters = new HashMap<>();

    /**
     * Stores for each directory the heap configuration exporters created so far.
     */
    private final Map<String, HeapConfigurationExporter> heapConfigurationExporters = new HashMap<>();

    /**
     * Stores for each directory the state space exporters created so far.
     */
    private final Map<String, StateSpaceExporter> stateSpaceExporters = new HashMap<>();

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
            throw new IllegalArgumentException("Refinement of indexed grammars is not supported yet.");
        } else if(requiresIndexedSymbols()) {
            return new IndexedNonterminalImpl(label, new ArrayList<>());
        } else if(requiresRefinedSymbols()) {
            return new RefinedDefaultNonterminal(GeneralNonterminal.getNonterminal(label), null);
        } else {
            return GeneralNonterminal.getNonterminal(label);
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

        return Settings.getInstance().options().getStateLabelingAutomaton() != null
                || Settings.getInstance().options().getStateRefinementAutomaton() != null;
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
            throw new IllegalArgumentException("Refinement of indexed grammars is not supported yet.");
        } else if(requiresIndexedSymbols()) {
            return new IndexedNonterminalImpl(label, rank, isReductionTentacle, new ArrayList<>());
        } else if(requiresRefinedSymbols()) {
            return new RefinedDefaultNonterminal(
                    GeneralNonterminal.getNonterminal(label, rank, isReductionTentacle),
                    null
            );
        } else {
            return GeneralNonterminal.getNonterminal(label, rank, isReductionTentacle);
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
            return GeneralSelectorLabel.getSelectorLabel(label);
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
     * Yields a HeapConfigurationExporter that writes into the specified directory.
     * @param filename The name of the file the heap configuration should be exported to.
     * @return The HeapConfigurationExporter.
     */
    public void export(String directory, String filename, HeapConfiguration hc) throws IOException {

        prepareFilename(directory);

        FileWriter writer = new FileWriter(directory + File.separator + filename);
        HeapConfigurationExporter exporter = new JsonHeapConfigurationExporter(writer);
        exporter.export(hc);
        writer.close();
        /*if( heapConfigurationExporters.containsKey( directory ) ) {
            return heapConfigurationExporters.get( directory );
        } else {
            HeapConfigurationExporter newExporter = new HeapConfigurationHtmlExporter( directory);
            heapConfigurationExporters.put( directory, newExporter );
            return newExporter;
        }*/
    }

    private void prepareFilename(String directory) throws IOException {
        File file = new File(directory);
        if( !file.exists() || !file.isDirectory() ) {
            boolean success = ( new File( directory ) ).mkdirs();
            if( !success ) {
                throw new IOException( "Unable to generate directory: " + directory );
            }
        }
    }

    /**
     * Creates an object to export state spaces.
     * @param filename The name of the file the state space should be exported to.
     * @param stateSpace The state space to export.
     * @return The created StateSpaceExporter.
     */
    public void export(String directory, String filename, StateSpace stateSpace, Program program) throws IOException {

        prepareFilename(directory);

        Writer writer = new BufferedWriter(
                new OutputStreamWriter( new FileOutputStream(directory + File.separator + filename) )
        );
        StateSpaceExporter exporter = new JsonStateSpaceExporter(writer);
        exporter.export(stateSpace, program);
        writer.close();
        /*
        if( stateSpaceExporters.containsKey( directory ) ) {
            return stateSpaceExporters.get( directory );
        } else {
            StateSpaceExporter newExporter = new StateSpaceHtmlExporter(directory);
            stateSpaceExporters.put( directory, newExporter );
            return newExporter;
        }
        */
    }

    /**
     * Adds a number of previously generated states to the global state counter.
     * @param states The number of freshly generated states.
     */
    public void addGeneratedStates(int states) {

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

    public ProgramState createProgramState(int programCounter, HeapConfiguration heapConfiguration, int scopeDepth) {

        ProgramState result;

        if(scopeDepth > 0)  {
            if(requiresIndexedSymbols()) {
                result = new IndexedState(heapConfiguration, scopeDepth);
            } else {
                result = new DefaultState(heapConfiguration, scopeDepth);
            }
        } else {
            if(requiresIndexedSymbols()) {
                result = new IndexedState(heapConfiguration);
            } else {
                result = new DefaultState(heapConfiguration);
            }
        }

        result.setProgramCounter(programCounter);
        result.prepareHeap();
        return result;
    }

    public StateSpaceGenerator createStateSpaceGenerator(Program program, HeapConfiguration input, int scopeDepth) {

        return getStateSpaceGeneratorBuilder()
                .setProgram(program)
                .addInitialState(
                        createProgramState(0, input, scopeDepth)
                )
                .build();
    }

    public StateSpaceGenerator createStateSpaceGenerator(Program program,
                                                         List<HeapConfiguration> inputs, int scopeDepth) {
        List<ProgramState> inputStates = new ArrayList<>(inputs.size());
        inputs.forEach(hc -> inputStates.add(createProgramState(0, hc, scopeDepth)));

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
                .setInclusionStrategy(
                        settings.getInclusionStrategy()
                )
                .setCanonizationStrategy(
                        settings.getCanonicalizationStrategy()
                )
                .setAbortStrategy(
                        settings.getAbortStrategy()
                )
                .setStateRefinementStrategy(
                        settings.getStateRefinementStrategy()
                );
    }
}
