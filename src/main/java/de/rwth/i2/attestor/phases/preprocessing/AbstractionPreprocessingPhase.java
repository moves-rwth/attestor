package de.rwth.i2.attestor.phases.preprocessing;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategyBuilder;
import de.rwth.i2.attestor.grammar.concretization.ConcretizationStrategyBuilder;
import de.rwth.i2.attestor.grammar.languageInclusion.LanguageInclusionStrategyBuilder;
import de.rwth.i2.attestor.grammar.materialization.MaterializationStrategyBuilder;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.transformers.GrammarTransformer;
import de.rwth.i2.attestor.phases.transformers.StateLabelingStrategyBuilderTransformer;
import de.rwth.i2.attestor.refinement.BundledStateRefinementStrategy;
import de.rwth.i2.attestor.refinement.garbageCollection.GarbageCollector;
import de.rwth.i2.attestor.stateSpaceGeneration.StateLabelingStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.StateRefinementStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.NoStateLabelingStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.NoStateRefinementStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.StateSpaceBoundedAbortStrategy;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AbstractionPreprocessingPhase extends AbstractPhase {


    private Grammar grammar;

    public AbstractionPreprocessingPhase(Scene scene) {

        super(scene);
    }

    @Override
    public String getName() {

        return "Abstraction preprocessing";
    }

    @Override
    protected void executePhase() {

        grammar = getPhase(GrammarTransformer.class).getGrammar();

        checkSelectors();

        setupConcretization();
        setupMaterialization();
        setupCanonicalization();
        setupAbortTest();
        setupStateLabeling();
        setupStateRefinement();
        setupInclusionCheck();
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

        Set<String> usedSelectors = new LinkedHashSet<>(scene().options().getUsedSelectorLabels());
        usedSelectors.removeAll(scene().options().getGrammarSelectorLabels());

        if (!usedSelectors.isEmpty()) {
            logger.warn("+------------------------------------------------------------------+");
            logger.warn("| Some selector labels are not used within any grammar rule.       |");
            logger.warn("| These selector labels can never be abstracted!                   |");
            logger.warn("| This might be intended if they refer to numerical values         |");
            logger.warn("| The selectors in question are listed below:                      |");
            for (String badSel : usedSelectors) {
                logger.warn(String.format("| - %-63s|", badSel));
            }
            logger.warn("+------------------------------------------------------------------+");
        }
    }

    private void setupConcretization() {

        ConcretizationStrategyBuilder builder = new ConcretizationStrategyBuilder();
        builder.setGrammar(grammar);

        scene().strategies().setSingleStepConcretizationStrategy(builder.buildSingleStepStrategy());
        scene().strategies().setFullConcretizationStrategy(builder.buildFullConcretizationStrategy());
    }

    private void setupMaterialization() {

        scene().strategies().setMaterializationStrategy(
                new MaterializationStrategyBuilder()
                .setGrammar(grammar)
                .setIndexedMode(scene().options().isIndexedMode())
                .build()
        );
    }

    private void setupCanonicalization() {

        final int abstractionDistance = scene().options().getAbstractionDistance();
        final boolean aggressiveNullAbstraction = scene().options().getAggressiveNullAbstraction();
        final boolean indexedMode = scene().options().isIndexedMode();

        scene().strategies().setLenientCanonicalizationStrategy(
                new CanonicalizationStrategyBuilder()
                .setAggressiveNullAbstraction(aggressiveNullAbstraction)
                .setMinAbstractionDistance(abstractionDistance)
                .setIndexedMode(indexedMode)
                .setGrammar(grammar)
                .build()
        );

        scene().strategies().setAggressiveCanonicalizationStrategy(
                new CanonicalizationStrategyBuilder()
                        .setAggressiveNullAbstraction(aggressiveNullAbstraction)
                        .setMinAbstractionDistance(0)
                        .setIndexedMode(indexedMode)
                        .setGrammar(grammar)
                        .build()
        );
    }

    private void setupInclusionCheck() {

        scene().strategies().setLanguageInclusionStrategy(
               new LanguageInclusionStrategyBuilder()
                       .setMinAbstractionDistance(scene().options().getAbstractionDistance())
                       .setIndexedMode(scene().options().isIndexedMode())
                       .setCanonicalizationStrategy(scene().strategies().getLenientCanonicalizationStrategy())
                       .setSingleStepConcretizationStrategy(scene().strategies().getSingleStepConcretizationStrategy())
                        .build()
        );
    }

    private void setupAbortTest() {

        int stateSpaceBound = scene().options().getMaxStateSpaceSize();
        int stateBound = scene().options().getMaxStateSize();
        scene().strategies().setAbortStrategy(
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
        if (stateLabelingStrategy == null) {
            stateLabelingStrategy = new NoStateLabelingStrategy();
        }
        scene().strategies().setStateLabelingStrategy(stateLabelingStrategy);
    }

    private void setupStateRefinement() {

        StateRefinementStrategy stateRefinementStrategy = scene().strategies().getStateRefinementStrategy();

        boolean isGarbageCollectionEnabled = scene().options().isGarbageCollectionEnabled();

        if (stateRefinementStrategy == null) {
            if (isGarbageCollectionEnabled) {
                stateRefinementStrategy = new GarbageCollector();
            } else {
                stateRefinementStrategy = new NoStateRefinementStrategy();
            }
        } else if (isGarbageCollectionEnabled) {

            List<StateRefinementStrategy> strategies = new ArrayList<>(2);
            strategies.add(stateRefinementStrategy);
            strategies.add(new GarbageCollector());
            stateRefinementStrategy = new BundledStateRefinementStrategy(strategies);
        }
        scene().strategies().setStateRefinementStrategy(stateRefinementStrategy);
    }
}
