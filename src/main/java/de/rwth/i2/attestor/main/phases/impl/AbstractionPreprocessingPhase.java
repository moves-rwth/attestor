package de.rwth.i2.attestor.main.phases.impl;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationHelper;
import de.rwth.i2.attestor.grammar.canonicalization.EmbeddingCheckerProvider;
import de.rwth.i2.attestor.grammar.canonicalization.GeneralCanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.canonicalization.defaultGrammar.DefaultCanonicalizationHelper;
import de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar.EmbeddingIndexChecker;
import de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar.IndexedCanonicalizationHelper;
import de.rwth.i2.attestor.grammar.inclusion.MinDistanceInclusionStrategy;
import de.rwth.i2.attestor.grammar.materialization.*;
import de.rwth.i2.attestor.grammar.materialization.communication.DefaultGrammarResponseApplier;
import de.rwth.i2.attestor.grammar.materialization.defaultGrammar.DefaultMaterializationRuleManager;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexMaterializationStrategy;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedGrammarResponseApplier;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedMaterializationRuleManager;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.phases.transformers.StateLabelingStrategyBuilderTransformer;
import de.rwth.i2.attestor.main.settings.InputSettings;
import de.rwth.i2.attestor.main.settings.StateSpaceGenerationSettings;
import de.rwth.i2.attestor.refinement.BundledStateRefinementStrategy;
import de.rwth.i2.attestor.refinement.garbageCollection.GarbageCollector;
import de.rwth.i2.attestor.stateSpaceGeneration.CanonicalizationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.MaterializationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.StateLabelingStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.StateRefinementStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.NoStateRefinementStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.StateSpaceBoundedAbortStrategy;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminal;
import de.rwth.i2.attestor.programState.indexedState.index.DefaultIndexMaterialization;
import de.rwth.i2.attestor.programState.indexedState.index.IndexCanonizationStrategy;
import de.rwth.i2.attestor.programState.indexedState.index.IndexCanonizationStrategyImpl;
import de.rwth.i2.attestor.types.GeneralType;
import gnu.trove.iterator.TIntIterator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AbstractionPreprocessingPhase extends AbstractPhase {


    private Grammar grammar;

    @Override
    public String getName() {

        return "Abstraction preprocessing";
    }

    @Override
    protected void executePhase() {

        grammar = settings.grammar().getGrammar();

        checkSelectors();

        setupMaterialization();
        setupCanonicalization();
        setupInclusionCheck();
        setupAbortTest();
        setupStateLabeling();
        setupStateRefinement();
    }

    @Override
    public void logSummary() {
        // nothing to report
    }

    @Override
    public boolean isVerificationPhase() {

        return false;
    }

    private void checkSelectors() {

        InputSettings inputSettings = settings.input();
        Set<String> usedSelectors = new HashSet<>(inputSettings.getUsedSelectorLabels());
        usedSelectors.removeAll(inputSettings.getGrammarSelectorLabels());

        if(!usedSelectors.isEmpty()) {
            logger.warn("");
            logger.warn("");
            logger.warn("");
            logger.warn("+------------------------------------------------------------------+");
            logger.warn("| Some selector labels are not used within any grammar rule.       |");
            logger.warn("| These selector labels can never be abstracted!                   |");
            logger.warn("| The selectors in question are listed below:                      |");
            for(String badSel : usedSelectors) {
                logger.warn(String.format("| %-65s|", badSel));
            }
            logger.warn("+------------------------------------------------------------------+");
            logger.warn("");
            logger.warn("");
            logger.warn("");
        }

    }

    private void setupMaterialization() {

        MaterializationStrategy strategy;

        if(settings.options().isIndexedMode()) {
            ViolationPointResolver vioResolver = new ViolationPointResolver( grammar );

            IndexMatcher indexMatcher = new IndexMatcher( new DefaultIndexMaterialization() );
            MaterializationRuleManager grammarManager =
                    new IndexedMaterializationRuleManager(vioResolver, indexMatcher);

            GrammarResponseApplier ruleApplier =
                    new IndexedGrammarResponseApplier( new IndexMaterializationStrategy(),
                            new GraphMaterializer() );

            strategy = new GeneralMaterializationStrategy( grammarManager, ruleApplier );
            logger.debug("Setup materialization using indexed grammars.");
        } else {
            ViolationPointResolver vioResolver = new ViolationPointResolver( grammar );
            MaterializationRuleManager grammarManager =
                    new DefaultMaterializationRuleManager(vioResolver);
            GrammarResponseApplier ruleApplier =
                    new DefaultGrammarResponseApplier( new GraphMaterializer() );
            strategy = new GeneralMaterializationStrategy( grammarManager, ruleApplier );
            logger.debug("Setup materialization using standard hyperedge replacement grammars.");
        }

        settings.stateSpaceGeneration().setMaterializationStrategy(strategy);
    }

    private void setupCanonicalization() {

        final int abstractionDifference = settings.options().getAbstractionDistance();
        EmbeddingCheckerProvider checkerProvider = new EmbeddingCheckerProvider(abstractionDifference);
        EmbeddingCheckerProvider aggressiveCheckerProvider = new EmbeddingCheckerProvider(0);

        CanonicalizationHelper canonicalizationHelper;
        CanonicalizationHelper aggressiveCanonicalizationHelper;

        if(settings.options().isIndexedMode()) {
            canonicalizationHelper = getIndexedCanonicalizationHelper(checkerProvider);
            aggressiveCanonicalizationHelper = getIndexedCanonicalizationHelper(aggressiveCheckerProvider);
            logger.debug("Setup canonicalization using indexed grammar.");
        } else {
            canonicalizationHelper = new DefaultCanonicalizationHelper( checkerProvider );
            aggressiveCanonicalizationHelper = new DefaultCanonicalizationHelper( aggressiveCheckerProvider );
            logger.debug("Setup canonicalization using standard hyperedge replacement grammar.");
        }
        CanonicalizationStrategy strategy = new GeneralCanonicalizationStrategy(grammar, canonicalizationHelper);
        CanonicalizationStrategy aggressiveStrategy = new GeneralCanonicalizationStrategy(grammar, aggressiveCanonicalizationHelper);
        settings.stateSpaceGeneration().setCanonicalizationStrategy(strategy);
        settings.stateSpaceGeneration().setAggressiveCanonicalizationStrategy(aggressiveStrategy);
    }

    private void setupInclusionCheck() {

        final int abstractionDistance = settings.options().getAbstractionDistance();
        if(abstractionDistance > 0 && !settings.options().isIndexedMode()) {
            DefaultProgramState.setHeapInclusionStrategy(new MinDistanceInclusionStrategy(grammar));
            logger.debug("Setup inclusion strategy to isomorphism checking with materialization.");
        } else {
            logger.debug("Setup inclusion strategy to isomorphism checking.");
        }
    }

    private CanonicalizationHelper getIndexedCanonicalizationHelper(EmbeddingCheckerProvider checkerProvider) {
        CanonicalizationHelper canonicalizationHelper;
        IndexCanonizationStrategy indexStrategy = new IndexCanonizationStrategyImpl(determineNullPointerGuards());
        IndexMaterializationStrategy materializationStrategy = new IndexMaterializationStrategy();
        DefaultIndexMaterialization indexGrammar = new DefaultIndexMaterialization();
        IndexMatcher indexMatcher = new IndexMatcher( indexGrammar);
        EmbeddingIndexChecker indexChecker =
                new EmbeddingIndexChecker( indexMatcher,
                        materializationStrategy );

        canonicalizationHelper = new IndexedCanonicalizationHelper( indexStrategy, checkerProvider, indexChecker);
        return canonicalizationHelper;
    }

    private Set<String> determineNullPointerGuards() {

        Set<String> nullPointerGuards = new HashSet<>();

        Grammar grammar = settings.grammar().getGrammar();
        for(Nonterminal lhs : grammar.getAllLeftHandSides()) {
            if(lhs instanceof IndexedNonterminal) {
                IndexedNonterminal iLhs = (IndexedNonterminal) lhs;
                if(iLhs.getIndex().getLastIndexSymbol().isBottom()) {
                    for(HeapConfiguration rhs : grammar.getRightHandSidesFor(lhs)) {

                        TIntIterator iterator = rhs.nodes().iterator();
                        while(iterator.hasNext()) {
                            int node = iterator.next();
                            for(SelectorLabel sel : rhs.selectorLabelsOf(node)) {

                                int target = rhs.selectorTargetOf(node, sel);
                                if(rhs.nodeTypeOf(target) == GeneralType.getType("NULL")) {
                                    nullPointerGuards.add(sel.getLabel());
                                }
                            }
                        }
                    }
                }
            }
        }

        return nullPointerGuards;
    }

    private void setupAbortTest() {

        int stateSpaceBound = settings.options().getMaxStateSpaceSize();
        int stateBound = settings.options().getMaxStateSize();
        settings.stateSpaceGeneration()
                .setAbortStrategy(
                        new StateSpaceBoundedAbortStrategy(stateSpaceBound, stateBound)
                );
        logger.debug("Setup abort criterion: #states > "
                + stateSpaceBound
                + " or one state is larger than "
                + stateBound
                + " nodes.");
    }

    private void setupStateLabeling() {

        StateLabelingStrategy stateLabelingStrategy = getPhase(StateLabelingStrategyBuilderTransformer.class)
                .getStrategy()
                .build();
        if(stateLabelingStrategy == null) {
            stateLabelingStrategy = s -> {};
        }
        settings.stateSpaceGeneration().setStateLabelingStrategy(stateLabelingStrategy);
    }

    private void setupStateRefinement() {

        StateSpaceGenerationSettings stateSpaceGenerationSettings = settings.stateSpaceGeneration();
        StateRefinementStrategy stateRefinementStrategy = stateSpaceGenerationSettings.getStateRefinementStrategy();
        boolean isGarbageCollectionEnabled = settings.options().isGarbageCollectionEnabled();

        if(stateRefinementStrategy == null) {
            if(isGarbageCollectionEnabled) {
                stateSpaceGenerationSettings.setStateRefinementStrategy(new GarbageCollector());
            } else {
                stateSpaceGenerationSettings.setStateRefinementStrategy(new NoStateRefinementStrategy());
            }
        } else if(isGarbageCollectionEnabled){

            List<StateRefinementStrategy> strategies = new ArrayList<>(2);
            strategies.add(stateRefinementStrategy);
            strategies.add(new GarbageCollector());
            BundledStateRefinementStrategy newStrategy = new BundledStateRefinementStrategy(strategies);
            stateSpaceGenerationSettings.setStateRefinementStrategy(newStrategy);
        }

    }


}
