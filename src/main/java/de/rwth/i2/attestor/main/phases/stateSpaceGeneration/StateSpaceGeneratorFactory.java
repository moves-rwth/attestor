package de.rwth.i2.attestor.main.phases.stateSpaceGeneration;

import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.main.scene.Strategies;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.*;

import java.util.List;

class StateSpaceGeneratorFactory extends SceneObject{

    public StateSpaceGeneratorFactory(Scene scene) {
        super(scene);
    }

    public StateSpaceGenerator create(Program program, ProgramState initialState) {

        return createBuilder()
                .addInitialState(initialState)
                .setProgram(program)
                .build();
    }

    public StateSpaceGenerator create(Program program, List<ProgramState> initialStates) {

        return createBuilder()
                .addInitialStates(initialStates)
                .setProgram(program)
                .build();
    }

    private SSGBuilder createBuilder() {

        Strategies strategies = scene().strategies();

        return StateSpaceGenerator
                .builder(this)
                .setStateLabelingStrategy(
                        strategies.getStateLabelingStrategy()
                )
                .setMaterializationStrategy(
                        strategies.getMaterializationStrategy()
                )
                .setCanonizationStrategy(
                        strategies.getLenientCanonicalizationStrategy()
                )
                .setAbortStrategy(
                        strategies.getAbortStrategy()
                )
                .setStateRefinementStrategy(
                        strategies.getStateRefinementStrategy()
                )
                .setStateCounter(
                        scene()::addNumberOfGeneratedStates
                )
                .setDeadVariableElimination(
                        scene().options().isRemoveDeadVariables()
                )
                .setBreadthFirstSearchEnabled(false)
                .setExplorationStrategy((s, sp) -> true)
                .setStateSpaceSupplier(() -> new InternalStateSpace(scene().options().getMaxStateSpaceSize()))
                .setSemanticsOptionsSupplier(DefaultSymbolicExecutionObserver::new)
                .setPostProcessingStrategy(getPostProcessingStrategy())
                ;
    }

    private PostProcessingStrategy getPostProcessingStrategy() {

        CanonicalizationStrategy aggressiveStrategy = scene().strategies().getAggressiveCanonicalizationStrategy();

        if (!scene().options().isPostprocessingEnabled() || scene().options().getAbstractionDistance() == 0) {
            return new NoPostProcessingStrategy();
        }

        StateCanonicalizationStrategy strategy = new StateCanonicalizationStrategy(aggressiveStrategy);

        if (scene().options().isIndexedMode()) {
            return new AggressivePostProcessingStrategy(strategy, scene().options().getAbstractionDistance());
        }

        return new FinalStateSubsumptionPostProcessingStrategy(
                strategy,
                scene().strategies().getLanguageInclusionStrategy(),
                scene().options().getAbstractionDistance()
        );
    }


}
