package de.rwth.i2.attestor.main.settings;

import de.rwth.i2.attestor.grammar.GrammarExporter;
    import de.rwth.i2.attestor.graph.Nonterminal;
               import de.rwth.i2.attestor.graph.SelectorLabel;
               import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
    import de.rwth.i2.attestor.graph.heap.HeapConfigurationExporter;
    import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
               import de.rwth.i2.attestor.indexedGrammars.AnnotatedSelectorLabel;
import de.rwth.i2.attestor.indexedGrammars.IndexedNonterminalImpl;
import de.rwth.i2.attestor.io.htmlExport.GrammarHtmlExporter;
    import de.rwth.i2.attestor.io.htmlExport.HeapConfigurationHtmlExporter;
    import de.rwth.i2.attestor.io.htmlExport.StateSpaceHtmlExporter;
    import de.rwth.i2.attestor.main.AnalysisTaskBuilder;
    import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceExporter;
    import de.rwth.i2.attestor.tasks.defaultTask.DefaultAnalysisTask;
               import de.rwth.i2.attestor.tasks.GeneralNonterminal;
               import de.rwth.i2.attestor.tasks.GeneralSelectorLabel;
               import de.rwth.i2.attestor.tasks.GeneralType;
import de.rwth.i2.attestor.tasks.indexedTask.IndexedAnalysisTask;
               import de.rwth.i2.attestor.types.Type;

               import java.util.ArrayList;
               import java.util.HashMap;
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
     * @return A builder to create a new AnalysisTask dependent on the current settings.
     */
    public AnalysisTaskBuilder createAnalysisTaskBuilder() {
        if(requiresIndexedSymbols()) {
            return IndexedAnalysisTask.builder();
        } else {
            return DefaultAnalysisTask.builder();
        }
    }

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

        if(requiresIndexedSymbols()) {
            return new IndexedNonterminalImpl(label, new ArrayList<>());
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

        if(requiresIndexedSymbols()) {
            return new IndexedNonterminalImpl(label, rank, isReductionTentacle, new ArrayList<>());
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
     * Yields a GrammarExporter that writes into the specified directory.
     * @param directory The directory into which the GrammarExporter should write its result.
     * @return The GrammarExporter.
     */
    public GrammarExporter getGrammarExporter(String directory) {
        if( grammarExporters.containsKey( directory ) ) {
            return grammarExporters.get( directory );
        } else {
            GrammarExporter newExporter = new GrammarHtmlExporter(directory);
            grammarExporters.put( directory, newExporter );
            return newExporter;
        }
    }

    /**
     * Yields a HeapConfigurationExporter that writes into the specified directory.
     * @param directory The directory into which the HeapConfigurationExporter should write its result.
     * @return The HeapConfigurationExporter.
     */
    public HeapConfigurationExporter getHeapConfigurationExporter(String directory) {
        if( heapConfigurationExporters.containsKey( directory ) ) {
            return heapConfigurationExporters.get( directory );
        } else {
            HeapConfigurationExporter newExporter = new HeapConfigurationHtmlExporter( directory);
            heapConfigurationExporters.put( directory, newExporter );
            return newExporter;
        }
    }

    /**
     * Creates an object to export state spaces.
     * @param directory The directory in which the exported state spaces should be stored.
     * @return The created StateSpaceExporter.
     */
    public StateSpaceExporter getStateSpaceExporter(String directory) {
        if( stateSpaceExporters.containsKey( directory ) ) {
            return stateSpaceExporters.get( directory );
        } else {
            StateSpaceExporter newExporter = new StateSpaceHtmlExporter(directory);
            stateSpaceExporters.put( directory, newExporter );
            return newExporter;
        }
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
}
