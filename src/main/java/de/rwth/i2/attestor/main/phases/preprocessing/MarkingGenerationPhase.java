package de.rwth.i2.attestor.main.phases.preprocessing;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.phases.communication.ModelCheckingSettings;
import de.rwth.i2.attestor.main.phases.transformers.GrammarTransformer;
import de.rwth.i2.attestor.main.phases.transformers.InputTransformer;
import de.rwth.i2.attestor.main.phases.transformers.MCSettingsTransformer;
import de.rwth.i2.attestor.main.phases.transformers.StateLabelingStrategyBuilderTransformer;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.markings.MarkedHcGenerator;
import de.rwth.i2.attestor.markings.Marking;
import de.rwth.i2.attestor.refinement.AutomatonStateLabelingStrategy;
import de.rwth.i2.attestor.refinement.AutomatonStateLabelingStrategyBuilder;
import de.rwth.i2.attestor.refinement.identicalNeighbourhood.NeighbourhoodAutomaton;
import de.rwth.i2.attestor.refinement.visited.StatelessVisitedAutomaton;
import de.rwth.i2.attestor.refinement.visited.StatelessVisitedByAutomaton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class MarkingGenerationPhase extends AbstractPhase
        implements InputTransformer, StateLabelingStrategyBuilderTransformer {


    private static final Pattern visitedByPattern = Pattern.compile("^visited\\(\\p{Alnum}+\\)$");

    private List<HeapConfiguration> inputs;

    private AutomatonStateLabelingStrategyBuilder stateLabelingStrategyBuilder;

    public MarkingGenerationPhase(Scene scene) {

        super(scene);
    }

    @Override
    public String getName() {

        return "Marking generation";
    }

    @Override
    protected void executePhase() {

        inputs = getPhase(InputTransformer.class).getInputs();
        ModelCheckingSettings mcSettings = getPhase(MCSettingsTransformer.class).getMcSettings();
        Set<String> requiredAPs = mcSettings.getRequiredAtomicPropositions();

        StateLabelingStrategyBuilderTransformer prev = getPhase(StateLabelingStrategyBuilderTransformer.class);
        if (prev == null) {
            stateLabelingStrategyBuilder = AutomatonStateLabelingStrategy.builder();
        } else {
            stateLabelingStrategyBuilder = prev.getStrategy();
            if (stateLabelingStrategyBuilder == null) {
                stateLabelingStrategyBuilder = AutomatonStateLabelingStrategy.builder();
            }
        }

        boolean requiresVisitedMarking = false;
        boolean requiresVisitedByMarking = false;
        boolean requiresNeighbourhoodMarking = false;


        for (String s : requiredAPs) {

            if (requiresNeighbourhoodMarking && requiresVisitedMarking) {
                break;
            }

            if (!requiresVisitedMarking && s.equals("visited")) {
                requiresVisitedMarking = true;
            }

            if (visitedByPattern.matcher(s).matches()) {
                String varName = s.split("[\\(\\)]")[1];
                scene().options().addKeptVariable(varName);
                requiresVisitedByMarking = true;
            }

            if (!requiresNeighbourhoodMarking && s.equals("identicNeighbours")) {
                requiresNeighbourhoodMarking = true;
            }
        }

        Marking marking = null;
        if (requiresVisitedMarking || requiresVisitedByMarking) {
            logger.info("Computing marked inputs to track visited identities...");
            marking = new Marking("visited");
            markInputs(marking);
        }

        if (requiresVisitedMarking) {
            stateLabelingStrategyBuilder.add(new StatelessVisitedAutomaton(marking));
        }

        if (requiresVisitedByMarking) {
            stateLabelingStrategyBuilder.add(new StatelessVisitedByAutomaton(marking));
        }

        if (requiresNeighbourhoodMarking) {
            logger.info("Computing marked inputs to track neighbourhood identities...");
            marking = new Marking("neighbourhood", true);
            markInputs(marking);
            stateLabelingStrategyBuilder.add(new NeighbourhoodAutomaton(this, marking));
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