package de.rwth.i2.attestor.main.phases.impl;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.phases.transformers.InputTransformer;
import de.rwth.i2.attestor.main.phases.transformers.StateLabelingStrategyBuilderTransformer;
import de.rwth.i2.attestor.refinement.AutomatonStateLabelingStrategy;
import de.rwth.i2.attestor.refinement.AutomatonStateLabelingStrategyBuilder;
import de.rwth.i2.attestor.refinement.HeapAutomaton;
import de.rwth.i2.attestor.refinement.balanced.BalancednessStateRefinementStrategy;
import de.rwth.i2.attestor.refinement.grammarRefinement.GrammarRefinement;
import de.rwth.i2.attestor.refinement.grammarRefinement.InitialHeapConfigurationRefinement;
import de.rwth.i2.attestor.refinement.reachability.ReachabilityHeapAutomaton;
import de.rwth.i2.attestor.refinement.variableRelation.VariableRelationsAutomaton;
import de.rwth.i2.attestor.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class GrammarRefinementPhase extends AbstractPhase
        implements InputTransformer, StateLabelingStrategyBuilderTransformer {

    private static final Pattern reachablePattern = Pattern.compile("^isReachable\\(\\p{Alnum}+,\\p{Alnum}+\\)$");
    private static final Pattern equalityPattern = Pattern.compile("^\\p{Alnum}+ \\=\\= \\p{Alnum}+$");
    private static final Pattern inequalityPattern = Pattern.compile("^\\p{Alnum}+ \\!\\= \\p{Alnum}+$");

    private List<HeapConfiguration> inputs;
    private AutomatonStateLabelingStrategyBuilder stateLabelingStrategyBuilder;

    @Override
    public String getName() {

        return "Grammar Refinement";
    }

    @Override
    protected void executePhase() {

        inputs = getPhase(InputTransformer.class).getInputs();

        stateLabelingStrategyBuilder = getPhase(StateLabelingStrategyBuilderTransformer.class).getStrategy();
        if(stateLabelingStrategyBuilder == null) {
            stateLabelingStrategyBuilder = AutomatonStateLabelingStrategy.builder();
        }


        updateHeapAutomata();

        if(settings.options().isIndexedMode()) {
            logger.info("Grammar refinement for indexed grammars is not supported yet.");
            settings.stateSpaceGeneration().setStateRefinementStrategy(new BalancednessStateRefinementStrategy());
            return;
        }

        HeapAutomaton automaton = stateLabelingStrategyBuilder.getProductAutomaton();
        Grammar grammar =  settings.grammar().getGrammar();

        if(automaton != null && grammar != null) {
            settings.options().setGrammarRefinementEnabled(true);
            grammar = refineGrammar(automaton, grammar);
            refineInputs(automaton, grammar);
            settings.grammar().setGrammar(grammar);
        }
    }

    private void updateHeapAutomata() {

        boolean hasReachabilityAutomaton = false;

        Set<Pair<String, String>> trackedVariableRelations = new HashSet<>();

        Set<String> requiredAPs = settings.modelChecking().getRequiredAtomicPropositions();
        for(String ap : requiredAPs) {

            if(!hasReachabilityAutomaton && reachablePattern.matcher(ap).matches() ) {
                stateLabelingStrategyBuilder.add(new ReachabilityHeapAutomaton());
                hasReachabilityAutomaton = true;
                logger.info("Enable heap automaton to track reachable variables");
            } else if(equalityPattern.matcher(ap).matches()) {
                String[] split = ap.split("\\=\\=");
                String lhs = split[0].trim();
                String rhs = split[1].trim();
                Pair<String, String> trackedPair = new Pair<>(lhs, rhs);
                if(trackedVariableRelations.add(trackedPair)) {
                   stateLabelingStrategyBuilder.add(new VariableRelationsAutomaton(lhs, rhs));
                    logger.info("Enable heap automaton to track relationships between variables '"
                            + lhs + "' and '" + rhs + "'");
                }
            } else if (inequalityPattern.matcher(ap).matches()) {
                String[] split = ap.split("\\!\\=");
                String lhs = split[0].trim();
                String rhs = split[1].trim();
                Pair<String, String> trackedPair = new Pair<>(lhs, rhs);
                if(trackedVariableRelations.add(trackedPair)) {
                    stateLabelingStrategyBuilder.add(new VariableRelationsAutomaton(lhs, rhs));
                    logger.info("Enable heap automaton to track relationships between variables '"
                            + lhs + "' and '" + rhs + "'");
                }
            }

            // TODO points-to
        }
    }

    private Grammar refineGrammar(HeapAutomaton automaton, Grammar grammar) {

        logger.info("Refining graph grammar...");
        GrammarRefinement grammarRefinement = new GrammarRefinement(
                grammar,
                automaton
        );
        Grammar refinedGrammar = grammarRefinement.getRefinedGrammar();
        logger.info("done. Number of refined nonterminals: "
                + refinedGrammar.getAllLeftHandSides().size());
        return refinedGrammar;

    }

    private void refineInputs(HeapAutomaton automaton, Grammar grammar) {

        logger.info("Refining input heap configurations...");
        List<HeapConfiguration> newInputs = new ArrayList<>();
        for(HeapConfiguration input : inputs) {
            InitialHeapConfigurationRefinement inputRefinement = new InitialHeapConfigurationRefinement(
                    input,
                    grammar,
                    automaton
            );
            newInputs.addAll(inputRefinement.getRefinements());
        }
        inputs = newInputs;
        if(inputs.isEmpty())	{
            logger.fatal("No refined initial state exists.");
            throw new IllegalStateException();
        }
        logger.info("done. Number of refined heap configurations: " + inputs.size());
    }

    @Override
    public void logSummary() {
        // nothing to report
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
