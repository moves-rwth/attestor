package de.rwth.i2.attestor.main.phases.impl;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.phases.transformers.InputTransformer;
import de.rwth.i2.attestor.markings.MarkedHcGenerator;
import de.rwth.i2.attestor.markings.Marking;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class MarkingGenerationPhase extends AbstractPhase implements InputTransformer {

    private List<HeapConfiguration> inputs;

    @Override
    public String getName() {

        return "Marking generation";
    }

    @Override
    protected void executePhase() {

        inputs = getPhase(InputTransformer.class).getInputs();
        Set<String> requiredAPs = settings.modelChecking().getRequiredAtomicPropositions();

        boolean requiresVisitedMarking = false;
        boolean requiresNeighbourhoodMarking = false;

        Pattern visitedPattern = Pattern.compile("^visited$|^visited\\(\\p{Alnum}+\\)$");

        for(String s : requiredAPs) {

            if(requiresNeighbourhoodMarking && requiresVisitedMarking) {
                break;
            }

            if(!requiresVisitedMarking && visitedPattern.matcher(s).matches()) {
                requiresVisitedMarking = true;
            }

            if(!requiresNeighbourhoodMarking && s.equals("identicNeighbours")) {
                requiresNeighbourhoodMarking = true;
            }
        }

        if(requiresVisitedMarking) {
            logger.info("Computing marked inputs to track visited identities...");
            markInputs(new Marking("visited"));
            // TODO add visited automaton...
        }

        if(requiresNeighbourhoodMarking) {
            logger.info("Computing marked inputs to track neighbourhood identities...");
            markInputs(new Marking("neighbourhood", true));
        }

    }

    private void markInputs(Marking marking) {

        Grammar grammar = settings.grammar().getGrammar();
        List<HeapConfiguration> newInputs = new ArrayList<>();
        for(HeapConfiguration input : inputs) {
            MarkedHcGenerator generator = new MarkedHcGenerator(input, grammar, marking);
            newInputs.addAll(generator.getMarkedHcs());
        }
        if(newInputs.isEmpty()) {
            throw new IllegalStateException("No marked heap configurations could be computed.");
        }
        inputs = newInputs;
        logger.info("Generated " + inputs.size() + " marked heap configurations.");
    }

    @Override
    public void logSummary() {
        // nothing to report
    }

    @Override
    public List<HeapConfiguration> getInputs() {

        return inputs;
    }
}
