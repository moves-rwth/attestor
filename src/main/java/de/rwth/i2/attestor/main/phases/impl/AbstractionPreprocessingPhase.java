package de.rwth.i2.attestor.main.phases.impl;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationHelper;
import de.rwth.i2.attestor.grammar.canonicalization.EmbeddingCheckerProvider;
import de.rwth.i2.attestor.grammar.canonicalization.GeneralCanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.canonicalization.defaultGrammar.DefaultCanonicalizationHelper;
import de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar.EmbeddingIndexChecker;
import de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar.IndexedCanonicalizationHelper;
import de.rwth.i2.attestor.grammar.materialization.*;
import de.rwth.i2.attestor.grammar.materialization.communication.DefaultGrammarResponseApplier;
import de.rwth.i2.attestor.grammar.materialization.defaultGrammar.DefaultMaterializationRuleManager;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexMaterializationStrategy;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedGrammarResponseApplier;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedMaterializationRuleManager;
import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.phases.transformers.StateLabelingStrategyBuilderTransformer;
import de.rwth.i2.attestor.stateSpaceGeneration.CanonicalizationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.MaterializationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.StateLabelingStrategy;
import de.rwth.i2.attestor.strategies.StateSpaceBoundedAbortStrategy;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.AVLIndexCanonizationStrategy;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.DefaultIndexMaterialization;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.IndexCanonizationStrategy;

public class AbstractionPreprocessingPhase extends AbstractPhase {


    private Grammar grammar;

    @Override
    public String getName() {

        return "Abstraction preprocessing";
    }

    @Override
    protected void executePhase() {

        grammar = settings.grammar().getGrammar();
        setupMaterialization();
        setupCanonicalization();
        setupAbortTest();
        setupStateLabeling();
        setupStateRefinement();
    }

    @Override
    public void logSummary() {
        // nothing to report
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

        EmbeddingCheckerProvider checkerProvider = getEmbeddingCheckerProvider();
        CanonicalizationHelper canonicalizationHelper;

        if(settings.options().isIndexedMode()) {

            canonicalizationHelper = getIndexedCanonicalizationHelper(checkerProvider);
            logger.debug("Setup canonicalization using indexed grammar.");

        } else {
            canonicalizationHelper = new DefaultCanonicalizationHelper( checkerProvider );
            logger.debug("Setup canonicalization using standard hyperedge replacement grammar.");
        }
        CanonicalizationStrategy strategy = new GeneralCanonicalizationStrategy(grammar, canonicalizationHelper);
        settings.stateSpaceGeneration().setCanonicalizationStrategy(strategy);
    }

    private CanonicalizationHelper getIndexedCanonicalizationHelper(EmbeddingCheckerProvider checkerProvider) {
        CanonicalizationHelper canonicalizationHelper;
        IndexCanonizationStrategy indexStrategy = new AVLIndexCanonizationStrategy();



        IndexMaterializationStrategy materializer = new IndexMaterializationStrategy();
        DefaultIndexMaterialization indexGrammar = new DefaultIndexMaterialization();
        IndexMatcher indexMatcher = new IndexMatcher( indexGrammar);
        EmbeddingIndexChecker indexChecker =
                new EmbeddingIndexChecker( indexMatcher,
                        materializer );

        canonicalizationHelper = new IndexedCanonicalizationHelper( indexStrategy, checkerProvider, indexChecker);
        return canonicalizationHelper;
    }

    private EmbeddingCheckerProvider getEmbeddingCheckerProvider() {
        final int abstractionDifference = settings.options().getAbstractionDistance();
        final int aggressiveAbstractionThreshold = settings.options().getAggressiveAbstractionThreshold();
        final boolean aggressiveReturnAbstraction = settings.options().isAggressiveReturnAbstraction();
        return new EmbeddingCheckerProvider(abstractionDifference ,
                aggressiveAbstractionThreshold,
                aggressiveReturnAbstraction);
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

        if(settings.stateSpaceGeneration().getStateRefinementStrategy() == null) {
            settings.stateSpaceGeneration().setStateRefinementStrategy(s -> s);
        }
    }


}
