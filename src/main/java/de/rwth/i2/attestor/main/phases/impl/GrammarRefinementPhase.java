package de.rwth.i2.attestor.main.phases.impl;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.BasicNonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.environment.Scene;
import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.phases.transformers.InputTransformer;
import de.rwth.i2.attestor.main.phases.transformers.StateLabelingStrategyBuilderTransformer;
import de.rwth.i2.attestor.refinement.AutomatonStateLabelingStrategy;
import de.rwth.i2.attestor.refinement.AutomatonStateLabelingStrategyBuilder;
import de.rwth.i2.attestor.refinement.HeapAutomaton;
import de.rwth.i2.attestor.refinement.balanced.BalancednessAutomaton;
import de.rwth.i2.attestor.refinement.balanced.BalancednessStateRefinementStrategy;
import de.rwth.i2.attestor.refinement.balanced.ListLengthAutomaton;
import de.rwth.i2.attestor.refinement.grammarRefinement.GrammarRefinement;
import de.rwth.i2.attestor.refinement.grammarRefinement.InitialHeapConfigurationRefinement;
import de.rwth.i2.attestor.refinement.languageInclusion.LanguageInclusionAutomaton;
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

    private static final Pattern btree = Pattern.compile("^btree$");
    private static final Pattern bimap = Pattern.compile("^bimap$");
    private static final Pattern languageInclusion = Pattern.compile("^L\\(\\p{Space}*\\p{Alnum}+\\p{Space}*\\)$");
    private static final Pattern reachablePattern = Pattern.compile("^isReachable\\(\\p{Space}*\\p{Alnum}+,\\p{Space}*\\p{Alnum}+\\)$");
    private static final Pattern reachableBySelPattern
            = Pattern.compile("^isReachable\\(\\p{Alnum}+,\\p{Space}*\\p{Alnum}+,\\p{Space}*\\[(\\p{Alnum}+,\\p{Space})*\\p{Alnum}+\\]+\\)$");
    private static final Pattern equalityPattern = Pattern.compile("^@?\\p{Alnum}+(.\\p{Alnum}+)? == \\p{Alnum}+$");
    private static final Pattern inequalityPattern = Pattern.compile("^@?\\p{Alnum}+(.\\p{Alnum}+)? != \\p{Alnum}+$");

    private List<HeapConfiguration> inputs;
    private AutomatonStateLabelingStrategyBuilder stateLabelingStrategyBuilder;

    public GrammarRefinementPhase(Scene scene) {
        super(scene);
    }

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

        if(scene().options().isIndexedMode()) {

            // in the long run, we should make this an option in the settings
            if(BasicNonterminal.hasNonterminal("BT"))  {
                settings.stateSpaceGeneration().setStateRefinementStrategy(new BalancednessStateRefinementStrategy(this));
            }
        }

        HeapAutomaton automaton = stateLabelingStrategyBuilder.getProductAutomaton();
        Grammar grammar =  settings.grammar().getGrammar();

        if(automaton != null && grammar != null && !scene().options().isIndexedMode()) {
            scene().options().setGrammarRefinementEnabled(true);
            grammar = refineGrammar(automaton, grammar);
            refineInputs(automaton, grammar);
            settings.grammar().setGrammar(grammar);
        }
    }

    private void updateHeapAutomata() {

        boolean isIndexedMode = scene().options().isIndexedMode();
        boolean hasReachabilityAutomaton = false;
        boolean hasLanguageInclusionAutomaton = false;
        boolean hasBtreeAutomaton = false;
        boolean hasBimapAutomaton = false;

        Set<Set<String>> reachabilityAutomataBySelList = new HashSet<>();

        Set<Pair<String, String>> trackedVariableRelations = new HashSet<>();

        Set<String> requiredAPs = settings.modelChecking().getRequiredAtomicPropositions();

        for(String ap : requiredAPs) {

            if(!hasBimapAutomaton && bimap.matcher(ap).matches()) {
                Grammar grammar = settings.grammar().getGrammar();
                stateLabelingStrategyBuilder.add(new ListLengthAutomaton(this, grammar));
                hasBimapAutomaton = true;
                logger.debug("Enable checking for lists of equal length.");
            } else if(!hasBtreeAutomaton && btree.matcher(ap).matches()) {
                Grammar grammar = settings.grammar().getGrammar();
                stateLabelingStrategyBuilder.add(new BalancednessAutomaton(this, grammar));
                hasBtreeAutomaton = true;
                logger.debug("Enable checking for balanced trees.");
            } else if(!hasLanguageInclusionAutomaton && languageInclusion.matcher(ap).matches()) {
                Grammar grammar = settings.grammar().getGrammar();
                stateLabelingStrategyBuilder.add(new LanguageInclusionAutomaton(this, grammar));
                hasLanguageInclusionAutomaton = true;
                logger.debug("Enable language inclusion checks to determine heap shapes.");
            } else if(reachableBySelPattern.matcher(ap).matches()) {

                if(isIndexedMode) {
                    logger.info("Advanced grammar refinement for indexed grammars is not supported yet.");
                    continue;
                }

                String[] parameters = ap.split("[\\(\\)]")[1].split("\\[");
                String[] variables = parameters[0].split(",");
                settings.stateSpaceGeneration().addKeptVariable(variables[0].trim());
                settings.stateSpaceGeneration().addKeptVariable(variables[1].trim());
                String[] selectors = parameters[1].split("\\]")[0].split(",");
                Set<String> allowedSelectors = new HashSet<>(selectors.length);
                for(String sel : selectors) {
                    allowedSelectors.add(sel.trim());
                }

                if(reachabilityAutomataBySelList.add(allowedSelectors)) {
                    stateLabelingStrategyBuilder.add(new ReachabilityHeapAutomaton(this,allowedSelectors));
                    logger.debug("Enable heap automaton to track reachable variables according to selectors "
                            + allowedSelectors);
                }

            } else if(!hasReachabilityAutomaton && reachablePattern.matcher(ap).matches() ) {


                if(isIndexedMode) {
                    logger.info("Advanced grammar refinement for indexed grammars is not supported yet.");
                    continue;
                }

                stateLabelingStrategyBuilder.add(new ReachabilityHeapAutomaton(this));
                String[] variables = ap.split("[\\(\\)]")[1].split(",");
                settings.stateSpaceGeneration().addKeptVariable(variables[0].trim());
                settings.stateSpaceGeneration().addKeptVariable(variables[1].trim());
                hasReachabilityAutomaton = true;
                logger.debug("Enable heap automaton to track reachable variables");
            } else if(equalityPattern.matcher(ap).matches()) {
                String[] split = ap.split("\\=\\=");
                enableVariableRelationTracking(trackedVariableRelations, split);
            } else if (inequalityPattern.matcher(ap).matches()) {
                String[] split = ap.split("\\!\\=");
                enableVariableRelationTracking(trackedVariableRelations, split);
            }
        }
    }

    private void enableVariableRelationTracking(Set<Pair<String, String>> trackedVariableRelations, String[] split) {

        String lhs = split[0].trim();
        String rhs = split[1].trim();
        Pair<String, String> trackedPair = new Pair<>(lhs, rhs);
        if(trackedVariableRelations.add(trackedPair)) {
           stateLabelingStrategyBuilder.add(new VariableRelationsAutomaton(lhs, rhs));
           settings.stateSpaceGeneration().addKeptVariable(lhs);
           settings.stateSpaceGeneration().addKeptVariable(rhs);
           logger.info("Enable heap automaton to track relationships between '"
                    + lhs + "' and '" + rhs + "'");
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
    public boolean isVerificationPhase() {

        return true;
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
