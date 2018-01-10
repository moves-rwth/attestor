package de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl;

import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.main.scene.Strategies;
import de.rwth.i2.attestor.phases.symbolicExecution.stateSpaceGenerationImpl.InternalStateSpace;
import de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies.AggressivePostProcessingStrategy;
import de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies.DepthFirstStateExplorationStrategy;
import de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies.FinalStateSubsumptionPostProcessingStrategy;
import de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies.NoPostProcessingStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.*;

import java.util.List;

public class StateSpaceGeneratorFactory extends SceneObject{

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

    protected StateSpaceGeneratorBuilder createBuilder() {

        Strategies strategies = scene().strategies();

        return StateSpaceGenerator
                .builder()
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
                .setStateExplorationStrategy(new DepthFirstStateExplorationStrategy())
                .setStateSpaceSupplier(() -> new InternalStateSpace(scene().options().getMaxStateSpaceSize()))
                .setPostProcessingStrategy(getPostProcessingStrategy())
                ;
    }

    private PostProcessingStrategy getPostProcessingStrategy() {

        CanonicalizationStrategy aggressiveStrategy = scene().strategies().getAggressiveCanonicalizationStrategy();

        if (!scene().options().isPostprocessingEnabled() || scene().options().getAbstractionDistance() == 0) {
            return new NoPostProcessingStrategy();
        }

        StateCanonicalizationStrategyWrapper strategy = new StateCanonicalizationStrategyWrapper(aggressiveStrategy);

        if (scene().options().isIndexedMode()) {
            return new AggressivePostProcessingStrategy(strategy, scene().options().getAbstractionDistance());
        }

        return new FinalStateSubsumptionPostProcessingStrategy(
                strategy,
                scene().strategies().getLanguageInclusionStrategy(),
                scene().options().getAbstractionDistance()
        );
    }


    public StateSpaceGenerator create(Program program, ProgramState initialState, StateSpace stateSpace) {

        if(stateSpace == null) {
            throw new IllegalArgumentException("Attempt to continue state space generation with empty state space.");
        }

        return createBuilder()
                .addInitialState(initialState)
                .setProgram(program)
                .setInitialStateSpace(stateSpace)
                .build();
    }
}
