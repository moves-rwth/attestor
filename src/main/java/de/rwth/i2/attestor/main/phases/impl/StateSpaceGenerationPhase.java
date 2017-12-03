package de.rwth.i2.attestor.main.phases.impl;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.environment.Scene;
import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.phases.transformers.InputTransformer;
import de.rwth.i2.attestor.main.phases.transformers.ProgramTransformer;
import de.rwth.i2.attestor.main.phases.transformers.StateSpaceGenerationTransformer;
import de.rwth.i2.attestor.main.phases.transformers.StateSpaceTransformer;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.AggressivePostProcessingStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.FinalStateSubsumptionPostProcessingStrategy;
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
        } catch(StateSpaceGenerationAbortedException e) {
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

    private SSGBuilder getStateSpaceGeneratorBuilder() {

        StateSpaceGenerationTransformer settings = getPhase(StateSpaceGenerationTransformer.class);

        return StateSpaceGenerator
                .builder(this)
                .setStateLabelingStrategy(
                        settings.getStateLabelingStrategy()
                )
                .setMaterializationStrategy(
                        settings.getMaterializationStrategy()
                )
                .setCanonizationStrategy(
                        settings.getCanonicalizationStrategy()
                )
                .setAbortStrategy(
                        settings.getAbortStrategy()
                )
                .setStateRefinementStrategy(
                        settings.getStateRefinementStrategy()
                )
                .setStateCounter(
                        scene()::addNumberOfGeneratedStates
                )
                .setDeadVariableElimination(
                        scene().options().isRemoveDeadVariables()
                )
                .setBreadthFirstSearchEnabled(false)
                .setExplorationStrategy((s,sp) -> true)
                .setStateSpaceSupplier(() -> new InternalStateSpace(scene().options().getMaxStateSpaceSize()))
                .setSemanticsOptionsSupplier(DefaultSymbolicExecutionObserver::new)
                .setPostProcessingStrategy(getPostProcessingStrategy())
                ;
    }

    private PostProcessingStrategy getPostProcessingStrategy() {

        StateSpaceGenerationTransformer settings = getPhase(StateSpaceGenerationTransformer.class);
        CanonicalizationStrategy aggressiveStrategy = settings.getAggressiveCanonicalizationStrategy();

        if(!scene().options().isPostprocessingEnabled() || scene().options().getAbstractionDistance() == 0) {
            return new NoPostProcessingStrategy();
        }

        if(scene().options().isIndexedMode()) {
            return new AggressivePostProcessingStrategy(aggressiveStrategy, scene().options().getAbstractionDistance());
        }

        return new FinalStateSubsumptionPostProcessingStrategy(aggressiveStrategy, scene().options().getAbstractionDistance());
    }

    private void printAnalyzedMethod() {

        logger.info("Analyzing '"
                + settings.input().getClasspath()
                + "/"
                + settings.input().getClassName()
                + "."
                + settings.input().getMethodName()
                + "'..."
        );
    }

    @Override
    public void logSummary() {

        logSum("+----------------------------------+--------------------------------+");
        logSum(String.format("| # states w/ procedure calls      | %30d |",
                scene().getNumberOfGeneratedStates()));
        logSum(String.format("| # states w/o procedure calls     | %30d |",
                stateSpace.getStates().size()));
        logSum(String.format("| # final states                   | %30d |",
                stateSpace.getFinalStateIds().size()));
        logSum("+-----------+----------------------+--------------------------------+");
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
