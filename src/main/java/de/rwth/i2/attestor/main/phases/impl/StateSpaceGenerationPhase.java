package de.rwth.i2.attestor.main.phases.impl;

import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.phases.communication.InputSettings;
import de.rwth.i2.attestor.main.phases.transformers.InputSettingsTransformer;
import de.rwth.i2.attestor.main.phases.transformers.InputTransformer;
import de.rwth.i2.attestor.main.phases.transformers.ProgramTransformer;
import de.rwth.i2.attestor.main.phases.transformers.StateSpaceTransformer;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.main.scene.Strategies;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.AggressivePostProcessingStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.FinalStateSubsumptionPostProcessingStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.InternalStateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.NoPostProcessingStrategy;

import java.util.ArrayList;
import java.util.List;

public class StateSpaceGenerationPhase extends AbstractPhase implements StateSpaceTransformer {

    private StateSpace stateSpace;

    public StateSpaceGenerationPhase(Scene scene) {

        super(scene);
    }

    @Override
    public String getName() {

        return "State space generation";
    }

    @Override
    protected void executePhase() {

        Program program = getPhase(ProgramTransformer.class).getProgram();
        List<HeapConfiguration> inputs = getPhase(InputTransformer.class).getInputs();

        StateSpaceGenerator stateSpaceGenerator = createStateSpaceGenerator(program, inputs);

        printAnalyzedMethod();

        try {
            stateSpace = stateSpaceGenerator.generate();
            logger.info("State space generation finished. #states: "
                    + scene().getNumberOfGeneratedStates());
        } catch (StateSpaceGenerationAbortedException e) {
            logger.error("State space generation has been aborted prematurely.");
            stateSpace = stateSpaceGenerator.getStateSpace();
        }
    }

    private StateSpaceGenerator createStateSpaceGenerator(Program program,
                                                          List<HeapConfiguration> inputs) {

        List<ProgramState> inputStates = new ArrayList<>(inputs.size());
        inputs.forEach(hc -> inputStates.add(scene().createProgramState(hc)));

        return getStateSpaceGeneratorBuilder()
                .setProgram(program)
                .addInitialStates(
                        inputStates
                )
                .build();
    }

    private StateSpaceGeneratorBuilder getStateSpaceGeneratorBuilder() {

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
                .setBreadthFirstSearchEnabled(false)
                .setExplorationStrategy((s, sp) -> true)
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

    private void printAnalyzedMethod() {

        InputSettings inputSettings = getPhase(InputSettingsTransformer.class).getInputSettings();

        logger.info("Analyzing '"
                + inputSettings.getClasspath()
                + "/"
                + inputSettings.getClassName()
                + "."
                + inputSettings.getMethodName()
                + "'..."
        );
    }

    @Override
    public void logSummary() {

        logSum("+-------------------------+------------------+");
        logHighlight("| Generated states        | Number of states |");
        logSum("+-------------------------+------------------+");
        logSum(String.format("| w/ procedure calls      | %16d |",
                scene().getNumberOfGeneratedStates()));
        logSum(String.format("| w/o procedure calls     | %16d |",
                stateSpace.getStates().size()));
        logSum(String.format("| final states            | %16d |",
                stateSpace.getFinalStateIds().size()));
        logSum("+-------------------------+------------------+");
    }

    @Override
    public boolean isVerificationPhase() {

        return true;
    }

    @Override
    public StateSpace getStateSpace() {

        return stateSpace;
    }
}
