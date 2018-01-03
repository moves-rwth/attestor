package de.rwth.i2.attestor.main.phases.impl;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategyBuilder;
import de.rwth.i2.attestor.grammar.materialization.MaterializationStrategy;
import de.rwth.i2.attestor.grammar.materialization.MaterializationStrategyBuilder;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.phases.communication.ModelCheckingSettings;
import de.rwth.i2.attestor.main.phases.transformers.GrammarTransformer;
import de.rwth.i2.attestor.main.phases.transformers.InputTransformer;
import de.rwth.i2.attestor.main.phases.transformers.MCSettingsTransformer;
import de.rwth.i2.attestor.main.phases.transformers.StateLabelingStrategyBuilderTransformer;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.markingGeneration.AbstractMarkingGenerator;
import de.rwth.i2.attestor.markingGeneration.visited.VisitedMarkingGenerator;
import de.rwth.i2.attestor.markings.MarkedHcGenerator;
import de.rwth.i2.attestor.markings.Marking;
import de.rwth.i2.attestor.refinement.AutomatonStateLabelingStrategy;
import de.rwth.i2.attestor.refinement.AutomatonStateLabelingStrategyBuilder;
import de.rwth.i2.attestor.refinement.identicalNeighbourhood.NeighbourhoodAutomaton;
import de.rwth.i2.attestor.refinement.visited.StatelessVisitedAutomaton;
import de.rwth.i2.attestor.refinement.visited.StatelessVisitedByAutomaton;
import de.rwth.i2.attestor.stateSpaceGeneration.AbortStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.StateSpaceBoundedAbortStrategy;

import java.util.*;
import java.util.regex.Pattern;

public class AlternativeMarkingGenerationPhase extends AbstractPhase
        implements InputTransformer, StateLabelingStrategyBuilderTransformer {


    private static final Pattern visitedByPattern = Pattern.compile("^visited\\(\\p{Alnum}+\\)$");
    private static final Pattern visitedPattern = Pattern.compile("^visited$");
    private static final Pattern identicNeighboursPattern = Pattern.compile("^identicNeighbours$");

    private static final String MARKING_NAME = "%marking";

    private static final String VISITED = "visited";
    private static final String VISITED_BY = "visitedBy";
    private static final String IDENTIC_NEIGHBOURS = "identicNeighbours";

    private List<HeapConfiguration> inputs;

    private AutomatonStateLabelingStrategyBuilder stateLabelingStrategyBuilder;

    public AlternativeMarkingGenerationPhase(Scene scene) {

        super(scene);
    }

    @Override
    public String getName() {

        return "Marking generation";
    }

    @Override
    protected void executePhase() {

        inputs = getPhase(InputTransformer.class).getInputs();

        stateLabelingStrategyBuilder = getPhase(StateLabelingStrategyBuilderTransformer.class).getStrategy();
        if (stateLabelingStrategyBuilder == null) {
            stateLabelingStrategyBuilder = AutomatonStateLabelingStrategy.builder();
        }

        Collection<String> requiredMarkings = determineMarkingsFromAPs();
        for(String marking : requiredMarkings) {
            addMarking(marking);
            addStateLabeling(marking);
        }
    }

    private Collection<String> determineMarkingsFromAPs() {

        ModelCheckingSettings mcSettings = getPhase(MCSettingsTransformer.class).getMcSettings();
        Set<String> result = new LinkedHashSet<>();
        for(String ap : mcSettings.getRequiredAtomicPropositions()) {
            if(visitedPattern.matcher(ap).matches()) {
                result.add(VISITED);
            } else if(visitedByPattern.matcher(ap).matches()) {
                String varName = ap.split("[\\(\\)]")[1];
                scene().options().addKeptVariable(varName);
                result.add(VISITED_BY);
            } else if(identicNeighboursPattern.matcher(ap).matches()) {
                result.add(IDENTIC_NEIGHBOURS);
            }
        }
        return result;
    }

    private void addMarking(String marking) {

        Collection<String> availableSelectorNames = scene().options().getUsedSelectorLabels();

        final Grammar grammar = getPhase(GrammarTransformer.class).getGrammar();
        final boolean indexedMode = scene().options().isIndexedMode();
        final int abstractionDistance = scene().options().getAbstractionDistance();
        final boolean aggressiveNullAbstraction = scene().options().getAggressiveNullAbstraction();
        final int stateSpaceBound = scene().options().getMaxStateSpaceSize();
        final int stateBound = scene().options().getMaxStateSize();

        MaterializationStrategy materializationStrategy = new MaterializationStrategyBuilder()
                .setIndexedMode(indexedMode)
                .setGrammar(grammar)
                .build();

        CanonicalizationStrategy canonicalizationStrategy = new CanonicalizationStrategyBuilder()
                .setGrammar(grammar)
                .setMinAbstractionDistance(abstractionDistance)
                .setAggressiveNullAbstraction(aggressiveNullAbstraction)
                .build();

        AbortStrategy abortStrategy = new StateSpaceBoundedAbortStrategy(stateSpaceBound, stateBound);

        AbstractMarkingGenerator generator = null;

        switch (marking) {
            case VISITED:
            case VISITED_BY:
                generator = new VisitedMarkingGenerator(MARKING_NAME, availableSelectorNames,
                        abortStrategy, materializationStrategy, canonicalizationStrategy);
                break;
            case IDENTIC_NEIGHBOURS:
                // TODO
                break;
            default:
                logger.error("Unknown marking.");
        }

        generateMarkedInputs(generator);
    }

    private void generateMarkedInputs(AbstractMarkingGenerator generator) {

        if(generator == null) {
            return;
        }

        List<HeapConfiguration> markedInputs = new LinkedList<>();
        for(HeapConfiguration in : inputs) {
            ProgramState initialState = scene().createProgramState(in);
            Collection<ProgramState> marked = generator.marked(initialState);
            marked.forEach(programState -> markedInputs.add(programState.getHeap()));
        }
        inputs = markedInputs;
    }

    private void addStateLabeling(String markingIdentifier) {

        switch (markingIdentifier) {
            case VISITED:
                stateLabelingStrategyBuilder.add(new StatelessVisitedAutomaton(new Marking("visited")));
                break;
            case VISITED_BY:
                stateLabelingStrategyBuilder.add(new StatelessVisitedByAutomaton(new Marking("visited")));
                break;
            case IDENTIC_NEIGHBOURS:
                Marking marking = new Marking("neighbourhood", true);
                markInputs(marking);
                stateLabelingStrategyBuilder.add(new NeighbourhoodAutomaton(this, marking));
                break;
            default:
                logger.error("Unknown marking.");
        }

    }

    private void markInputs(Marking marking) {

        Grammar grammar = getPhase(GrammarTransformer.class).getGrammar();
        List<HeapConfiguration> newInputs = new ArrayList<>();
        for (HeapConfiguration input : inputs) {
            MarkedHcGenerator generator = new MarkedHcGenerator(this, input, grammar, marking);
            newInputs.addAll(generator.getMarkedHcs());
        }
        if (newInputs.isEmpty()) {
            throw new IllegalStateException("No marked heap configurations could be computed.");
        }
        inputs = newInputs;
        logger.info("done. Generated " + inputs.size() + " marked heap configurations.");
    }

    @Override
    public void logSummary() {
        // nothing to report
    }

    @Override
    public boolean isVerificationPhase() {

        return false;
    }

    @Override
    public List<HeapConfiguration> getInputs() {

        return inputs;
    }

    @Override
    public AutomatonStateLabelingStrategyBuilder getStrategy() {

        return stateLabelingStrategyBuilder;
    }

}
