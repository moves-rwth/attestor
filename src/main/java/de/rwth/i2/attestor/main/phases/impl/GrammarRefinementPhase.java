package de.rwth.i2.attestor.main.phases.impl;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.phases.transformers.InputTransformer;
import de.rwth.i2.attestor.refinement.HeapAutomaton;
import de.rwth.i2.attestor.refinement.RefinementParser;
import de.rwth.i2.attestor.refinement.balanced.BalancednessStateRefinementStrategy;
import de.rwth.i2.attestor.refinement.grammarRefinement.GrammarRefinement;
import de.rwth.i2.attestor.refinement.grammarRefinement.InitialHeapConfigurationRefinement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GrammarRefinementPhase extends AbstractPhase implements InputTransformer {

    private List<HeapConfiguration> inputs;

    @Override
    public String getName() {

        return "Grammar Refinement";
    }

    @Override
    protected void executePhase() {

        inputs = getPhase(InputTransformer.class).getInputs();

        determineRequiredRefinements();

        if(!settings.options().isIndexedMode()) {
            setupStateLabeling();
        } else {
            settings.stateSpaceGeneration().setStateLabelingStrategy(state -> {});
            settings.stateSpaceGeneration().setStateRefinementStrategy(new BalancednessStateRefinementStrategy());
            logger.warn("Refinement of indexed grammars is not supported yet and thus ignored.");
        }

    }

    private void determineRequiredRefinements() {

        Set<String> requiredAPs = settings.modelChecking().getRequiredAtomicPropositions();
        logger.info("Setup refinements for " + requiredAPs.size() + " atomic proposition(s).");

        RefinementParser refinementParser = new RefinementParser(requiredAPs);
        settings.options().setRefinementAutomaton(refinementParser.getRefinementAutomaton());
        settings.stateSpaceGeneration().setStateRefinementStrategy(refinementParser.getStateRefinementStrategy());
        settings.stateSpaceGeneration().setStateLabelingStrategy(refinementParser.getStateLabelingStrategy());
    }

    private void setupStateLabeling() {

        HeapAutomaton refinementAutomaton = settings.options().getRefinementAutomaton();
        if(refinementAutomaton == null) {
            logger.info("No grammar refinement is required.");
            settings.stateSpaceGeneration().setStateLabelingStrategy(state -> {});
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

        logger.info("Refining input heap configuration...");

        List<HeapConfiguration> newInputs = new ArrayList<>();



        for(HeapConfiguration input : inputs) {
            InitialHeapConfigurationRefinement inputRefinement = new InitialHeapConfigurationRefinement(
                    input,
                    settings.grammar().getGrammar(),
                    refinementAutomaton
            );

            newInputs.addAll(inputRefinement.getRefinements());
        }
        inputs = newInputs;

        if(inputs.isEmpty())	{
            logger.fatal("No refined initial state exists.");
            throw new IllegalStateException();
        }

        logger.info("done. Number of refined heap configurations: "
                + inputs.size());
    }

    @Override
    public void logSummary() {

    }

    @Override
    public List<HeapConfiguration> getInputs() {

        return inputs;
    }
}
