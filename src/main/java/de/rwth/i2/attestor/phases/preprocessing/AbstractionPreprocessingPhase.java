package de.rwth.i2.attestor.phases.preprocessing;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategyBuilder;
import de.rwth.i2.attestor.grammar.materialization.strategies.MaterializationStrategy;
import de.rwth.i2.attestor.grammar.materialization.strategies.MaterializationStrategyBuilder;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies.*;
import de.rwth.i2.attestor.phases.transformers.GrammarTransformer;
import de.rwth.i2.attestor.phases.transformers.StateLabelingStrategyBuilderTransformer;
import de.rwth.i2.attestor.refinement.BundledStateRefinementStrategy;
import de.rwth.i2.attestor.refinement.garbageCollection.GarbageCollector;
import de.rwth.i2.attestor.stateSpaceGeneration.StateLabelingStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.StateMaterializationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.StateRefinementStrategy;

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
    public void executePhase() {

        grammar = getPhase(GrammarTransformer.class).getGrammar();

        checkSelectors();

        setupMaterialization();
        setupAbstractDomain();
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

        Set<String> usedSelectors = new LinkedHashSet<>(scene().options().getUsedSelectorLabels());
        usedSelectors.removeAll(scene().options().getGrammarSelectorLabels());

        if (!usedSelectors.isEmpty()) {
            logger.info("+------------------------------------------------------------------+");
            logger.info("| Some selector labels are not used within any grammar rule.       |");
            logger.info("| These selector labels can never be abstracted!                   |");
            logger.info("| This might be intended if they refer to numerical values         |");
            logger.info("| The selectors in question are listed below:                      |");
            for (String badSel : usedSelectors) {
                logger.info(String.format("| - %-63s|", badSel));
            }
            logger.info("+------------------------------------------------------------------+");
        }
    }

    private void setupMaterialization() {

        final int abstractionDistance = scene().options().getAbstractionDistance();
        final boolean indexedMode = scene().options().isIndexedMode();
        final boolean verifyCounterexamples = scene().options().isVerifyCounterexamples();

        MaterializationStrategy materializationStrategy = new MaterializationStrategyBuilder()
                .setGrammar(grammar)
                .setIndexedMode(indexedMode)
                .build();

        if(verifyCounterexamples && abstractionDistance == 1){
            scene().strategies().setMaterializationStrategy(new NoMaterializationStrategy());
            scene().strategies().setStateRectificationStrategy(
                    new AdmissibleStateRectificationStrategy(new StateMaterializationStrategy(materializationStrategy))
            );
        } else {
            scene().strategies().setMaterializationStrategy(materializationStrategy);
            scene().strategies().setStateRectificationStrategy(new NoRectificationStrategy());
        }

    }

    private void setupAbstractDomain() {

        final int abstractionDistance = scene().options().getAbstractionDistance();
        final boolean aggressiveNullAbstraction = scene().options().getAggressiveNullAbstraction();
        final boolean indexedMode = scene().options().isIndexedMode();
        final boolean verifyCounterexamples = scene().options().isVerifyCounterexamples();

        boolean aggressiveCompositeMarkingAbstraction = verifyCounterexamples && abstractionDistance == 1;

        CanonicalizationStrategy canonicalizationStrategy =
                new CanonicalizationStrategyBuilder()
                        .setAggressiveNullAbstraction(aggressiveNullAbstraction)
                        .setMinAbstractionDistance(abstractionDistance)
                        .setIndexedMode(indexedMode)
                        .setAggressiveCompositeMarkingAbstraction(aggressiveCompositeMarkingAbstraction)
                        .setGrammar(grammar)
                        .build();

        scene().strategies()
                .setCanonicalizationStrategy(canonicalizationStrategy);

        CanonicalizationStrategy aggressiveCanonicalizationStrategy =
                new CanonicalizationStrategyBuilder()
                        .setAggressiveNullAbstraction(aggressiveNullAbstraction)
                        .setMinAbstractionDistance(0)
                        .setIndexedMode(indexedMode)
                        .setAggressiveCompositeMarkingAbstraction(true)
                        .setGrammar(grammar)
                        .build();

        scene().strategies()
                .setAggressiveCanonicalizationStrategy(
                        aggressiveCanonicalizationStrategy
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
